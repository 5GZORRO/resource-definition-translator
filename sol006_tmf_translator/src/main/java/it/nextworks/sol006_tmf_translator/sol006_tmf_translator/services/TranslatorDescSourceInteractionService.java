package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TranslatorDescSourceInteractionService {

    private static final Logger log = LoggerFactory.getLogger(TranslatorDescSourceInteractionService.class);

    private static final String protocol = "http://";

    @Value("${descriptors_source.hostname}")
    private String sourceHostname;

    @Value("${descriptors_source.port}")
    private String sourcePort;

    private final ObjectMapper objectMapper;

    @Autowired
    public TranslatorDescSourceInteractionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getRequestPath(Kind kind, String id) {
        String url;
        switch(kind) {
            case VNF:
                url = "/vnfpkgm/v1/vnf_packages";
                if(id != null)
                    url = url + "/" + id + "/vnfd";
                return url;

            case PNF:
                url = "/nsd/v1/pnf_descriptors";
                if(id != null)
                    url = url + "/" + id + "/pnfd_content";
                return url;

            case NS:
                url = "/nsd/v1/ns_descriptors";
                if(id != null)
                    url = url + "/" + id + "/nsd_content";
                return url;

            default:
                return null;
        }
    }

    public HttpEntity getFromSource(Kind kind, String id)
            throws SourceException, MissingEntityOnSourceException {

        String request = protocol + sourceHostname + ":" + sourcePort + getRequestPath(kind, id);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 404)
            throw new MissingEntityOnSourceException();
        else if(statusCode != 200) {
            String msg = "Descriptors Source GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        return response.getEntity();
    }

    public String getInfoIdFromDescriptorId(Kind kind, String descriptorId)
            throws SourceException, MissingEntityOnSourceException, IOException {

        String request = protocol + sourceHostname + ":" + sourcePort + getRequestPath(kind, null);
        switch(kind) {
            case VNF:
                request = request + "?vnfdId=" + descriptorId;
                break;
            case PNF:
                request = request + "?pnfdId=" + descriptorId;
                break;
            case NS:
                request = request + "?nsdId=" + descriptorId;
                break;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 200) {
            String msg = "Descriptors Source GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        JsonNode info = objectMapper.readTree(EntityUtils.toString(response.getEntity()));
        if(info.size() == 0)
            throw new MissingEntityOnSourceException();
        return info.get(0).get("id").asText();
    }

    public void postOnSource(Kind kind, String packagePath) throws IOException, SourceException {

        String request = protocol + sourceHostname + ":" + sourcePort + getRequestPath(kind, null);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(request);

        httpPost.setEntity(new StringEntity("{}"));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 201) {
            String msg = "Descriptors Source POST request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode packageInfo = objectMapper.readTree(EntityUtils.toString(response.getEntity()));

        String packageInfoId = packageInfo.get("id").asText();

        log.info("Package info resource created with id " + packageInfoId);

        request = request + "/" + packageInfoId;
        switch(kind) {
            case VNF:
                request = request + "/package_content";
                break;
            case PNF:
                request = request + "/pnfd_content?project=admin";
                break;
            case NS:
                request = request + "/nsd_content";
                break;
        }

        HttpPut httpPut = new HttpPut(request);

        File _package = new File(packagePath);
        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", _package,
                ContentType.APPLICATION_OCTET_STREAM, _package.getName()).build();
        httpPut.setEntity(entity);
        httpPut.setHeader("Accept", "application/json");

        try {
            response = httpClient.execute(httpPut);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 204) {
            String msg = "Descriptors Source PUT request failed, status code: " + statusCode + ".";
            log.error(msg + EntityUtils.toString(response.getEntity()));
            throw new SourceException(msg);
        }
    }
}
