/*
 * Copyright 2018 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.CSARInfo;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.FailedOperationException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MalformattedElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ArchiveParser {

    private static final Logger log = LoggerFactory.getLogger(ArchiveParser.class);

    private static Set<String> admittedFolders = new HashSet<>();

    public ArchiveParser() {
    }

    @PostConstruct
    void init() {
        admittedFolders.add("TOSCA-Metadata/");
        admittedFolders.add("Definitions/");
        admittedFolders.add("Files/");
        admittedFolders.add("Files/Tests/");
        admittedFolders.add("Files/Licenses/");
        admittedFolders.add("Files/Scripts/");
        admittedFolders.add("Files/Monitoring/");
    }

    public <T> T stringToSol006(String mstName, String descriptor, Class<T> type) throws IOException {

        ObjectMapper mapper;

        if(mstName.endsWith(".yaml") || mstName.endsWith(".yml"))
            mapper = new ObjectMapper(new YAMLFactory());
        else
            mapper = new ObjectMapper();

        return mapper.readValue(descriptor, type);
    }

    public static String storePkg(String descriptorId, MultipartFile file)
            throws MalformattedElementException, FailedOperationException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new MalformattedElementException("Failed to store empty file " + filename);
            }

            File tmpDir = new File("/tmp/translator");
            if(!tmpDir.exists()) {
                if(!tmpDir.mkdir()) {
                    String msg = "translator tmp dir cannot be created in: /tmp/";
                    log.error(msg);
                    throw new IllegalStateException(msg);
                }
            }
            Path packagePath = tmpDir.toPath().resolve(descriptorId + ".zip");

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, packagePath,
                        StandardCopyOption.REPLACE_EXISTING);
                return packagePath.toString();
            }
        } catch (IOException e) {
            throw new FailedOperationException("Failed to store file " + filename, e);
        }
    }

    public CSARInfo archiveToCSARInfo(MultipartFile file, Kind descriptorType)
            throws IOException, MalformattedElementException, FailedOperationException {

        CSARInfo csarInfo = new CSARInfo();

        ByteArrayOutputStream metadata = null;
        ByteArrayOutputStream manifest = null;
        ByteArrayOutputStream mainServiceTemplate;
        Map<String, ByteArrayOutputStream> templates = new HashMap<>();

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();

            InputStream input = new ByteArrayInputStream(bytes);

            log.debug("Going to parse archive " + file.getName() + "...");

            ZipEntry entry;

            ZipInputStream zipStream = new ZipInputStream(input);
            while ((entry = zipStream.getNextEntry()) != null) {

                if (!entry.isDirectory()) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                    int count;
                    byte[] buffer = new byte[1024];
                    while ((count = zipStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, count);
                    }

                    String fileName = entry.getName();
                    log.debug("Parsing Archive: found file with name " + fileName);

                    if (fileName.toLowerCase().endsWith(".mf")) {
                        manifest = outStream;
                        csarInfo.setMfFilename(fileName);
                    } else if (fileName.toLowerCase().endsWith(".meta")) {
                        metadata = outStream;
                        csarInfo.setMetaFilename(fileName);
                    } else if (fileName.toLowerCase().endsWith(".yaml")
                            || fileName.toLowerCase().endsWith(".yml")
                            || fileName.toLowerCase().endsWith(".json")) {
                        templates.put(fileName, outStream);
                    }
                } else {
                    log.debug("Parsing Archive: checking folder with name " + entry.getName());
                    if (!admittedFolders.contains(entry.getName())) {
                        log.error("Folder with name " + entry.getName() + " not admitted in CSAR option#1 structure");
                        throw new MalformattedElementException("Folder with name " + entry.getName() + " not admitted in CSAR option#1 structure");
                    }
                }
            }

            String mst_name;

            if (metadata == null) {
                log.error("CSAR without TOSCA.meta");
                throw new MalformattedElementException("CSAR without TOSCA.meta");
            }
            if (manifest == null) {
                log.error("CSAR without manifest");
                throw new MalformattedElementException("CSAR without manifest");
            } else {
                mst_name = getMainServiceTemplateFromMetadata(metadata.toByteArray());
                if (mst_name != null) {
                    csarInfo.setDescriptorFilename(mst_name);
                    log.debug("Parsing metadata: found Main Service Template " + mst_name);
                    if (templates.containsKey(mst_name)) {
                        mainServiceTemplate = templates.get(mst_name);
                    } else {
                        log.error("Main Service Template specified in TOSCA.meta not present in CSAR Definitions directory: " + mst_name);
                        throw new MalformattedElementException(
                                "Main Service Template specified in TOSCA.meta not present in CSAR Definitions directory: " + mst_name);
                    }
                } else {
                    log.error("Unable to get Main Service Template name from TOSCA.meta");
                    throw new MalformattedElementException("Unable to get Main Service Template name from TOSCA.meta");
                }
            }

            String descriptorId = null;
            String version;
            if (mainServiceTemplate != null) {
                log.debug("Going to parse main service template...");
                String mst_content = mainServiceTemplate.toString("UTF-8");

                if(descriptorType == Kind.VNF) {
                    Vnfd vnfd = stringToSol006(mst_name, mst_content, Vnfd.class);
                    csarInfo.setVnfd(vnfd);
                    descriptorId = vnfd.getId();
                    version = vnfd.getVersion();
                    log.debug("Main service template with descriptor Id {} and version {} successfully parsed",
                            descriptorId, version);
                }
                else if(descriptorType == Kind.PNF) {
                    Pnfd pnfd = stringToSol006(mst_name, mst_content, Pnfd.class);
                    csarInfo.setPnfd(pnfd);
                    descriptorId = pnfd.getId();
                    version = pnfd.getVersion();
                    log.debug("Main service template with descriptor Id {} and version {} successfully parsed",
                            descriptorId, version);
                } else {
                    Nsd nsd = stringToSol006(mst_name, mst_content, Nsd.class);
                    csarInfo.setNsd(nsd);
                    descriptorId = nsd.getId();
                    version = nsd.getVersion();
                    log.debug("Main service template with descriptor Id {} and version {} successfully parsed",
                            descriptorId, version);
                }
            }

            try {
                String packageFilename = storePkg(descriptorId, file);
                csarInfo.setPackagePath(packageFilename);
                log.debug("Stored Pkg: " + packageFilename);
            } catch (FailedOperationException e) {
                log.error("Failure while storing Pkg with descriptor Id " + descriptorId + ": " + e.getMessage());
                throw new FailedOperationException("Failure while storing Pkg with descriptor Id " + descriptorId +
                        ": " + e.getMessage());
            }

            if(mainServiceTemplate != null)
                mainServiceTemplate.close();
            metadata.close();
            manifest.close();
            for (Map.Entry<String, ByteArrayOutputStream> template : templates.entrySet()) {
                template.getValue().close();
            }
            input.close();
        }

        return csarInfo;
    }

    private String getMainServiceTemplateFromMetadata(byte[] metadata) throws IOException {

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(metadata), StandardCharsets.UTF_8));

        log.debug("Going to parse TOSCA.meta...");

        String mst_name = null;

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    String regex = "^Entry-Definitions: (Definitions/[^\\\\]*\\.(yaml|json))$";
                    if (line.matches(regex)) {
                        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            mst_name = matcher.group(1);
                        }
                    }
                }
            }
        } finally {
            reader.close();
        }

        return mst_name;
    }
}
