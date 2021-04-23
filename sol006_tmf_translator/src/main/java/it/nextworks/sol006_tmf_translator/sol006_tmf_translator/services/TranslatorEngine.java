package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.nextworks.sol006_tmf_translator.information_models.commons.Any;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.commons.ResourceSpecificationRef;
import it.nextworks.sol006_tmf_translator.information_models.resource.*;
import it.nextworks.sol006_tmf_translator.information_models.sol006.*;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.config.CustomOffsetDateTimeSerializer;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogPostException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranslatorEngine {

    private static final Logger log = LoggerFactory.getLogger(TranslatorEngine.class);

    private static final String protocol = "http://";

    @Value("${offer_catalog.hostname}")
    private String catalogHostname;

    @Value("${offer_catalog.port}")
    private String catalogPort;

    @Value("${offer_catalog.contextPath}")
    private String contextPath;

    private final ObjectMapper objectMapper;

    @Autowired
    public TranslatorEngine(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        this.objectMapper.registerModule(module);
    }

    public Pair<ResourceCandidateCreate, ResourceSpecificationCreate> translateVNFD(Vnfd vnfd)
            throws IOException, CatalogPostException {

        log.info("Received request to translate vnfd.");

        String vnfdProductName = vnfd.getProductName();
        ResourceSpecificationCreate rsc = new ResourceSpecificationCreate()
                .description(vnfdProductName + " version " +
                        vnfd.getSoftwareVersion() + " by " + vnfd.getProvider())
                .name(vnfdProductName)
                .version(vnfd.getVersion())
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));

        List<ResourceSpecCharacteristic> resourceSpecCharacteristics = new ArrayList<>();

        List<String> vnfmInfo = vnfd.getVnfmInfo();
        if(vnfmInfo == null)
            log.debug("null vnfm-info list, skipping characteristics.");
        else {
            ResourceSpecCharacteristic rscVnfmInfo = new ResourceSpecCharacteristic()
                    .configurable(true)
                    .extensible(true)
                    .isUnique(true)
                    .name("vnfm-info");

            List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

            ResourceSpecCharacteristicValue rscv = new ResourceSpecCharacteristicValue()
                    .value(new Any().value(vnfmInfo.toString()))
                    .valueType("List<String>");
            rscvs.add(rscv);

            rscVnfmInfo.setResourceSpecCharacteristicValue(rscvs);

            resourceSpecCharacteristics.add(rscVnfmInfo);
        }

        List<VnfdVdu> vnfdVdus = vnfd.getVdu();
        if(vnfdVdus == null)
            log.debug("null vdu list, skipping characteristics.");
        else {
            for(VnfdVdu vnfdVdu : vnfdVdus) {

                String vnfdVduId = vnfdVdu.getId();
                if(vnfdVduId == null){
                    log.debug("null vdu item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscVnfVdu = new ResourceSpecCharacteristic()
                        .configurable(true)
                        .description("vdu " + vnfdVduId)
                        .extensible(true)
                        .isUnique(true)
                        .name(vnfdVdu.getName());

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                List<VnfdVduIntCpdItem> vnfdVduIntCpdItems = vnfdVdu.getIntCpd();
                if(vnfdVduIntCpdItems == null)
                    log.debug("null int-cpd list, skipping values.");
                else {
                    for(VnfdVduIntCpdItem vnfdVduIntCpdItem : vnfdVduIntCpdItems) {

                        String vnfdVduIntCpdItemId = vnfdVduIntCpdItem.getId();
                        if(vnfdVduIntCpdItemId == null) {
                            log.debug("null int-cpd item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String intVirtualLinkDesc = vnfdVduIntCpdItem.getIntVirtualLinkDesc();
                        if(intVirtualLinkDesc == null)
                            log.debug("null int-cpd int-virtual-link-desc, not inserted in value filed.");
                        else
                            value = "int-virtual-link-desc: " + intVirtualLinkDesc;

                        List<String> layerProtocols = vnfdVduIntCpdItem.getLayerProtocol();
                        if(layerProtocols == null)
                            log.debug("null int-cpd layer-protocol list, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "layer-protocol: " + layerProtocols.toString();
                        }

                        ResourceSpecCharacteristicValue rscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("int-cpd " + vnfdVduIntCpdItemId)
                                        .value(value))
                                .valueType("VnfdVduIntCpdItem");
                        rscvs.add(rscv);
                    }
                }

                String virtualComputeDesc = vnfdVdu.getVirtualComputeDesc();
                if(virtualComputeDesc == null)
                    log.debug("null virtual-compute-desc, skipping value.");
                else {
                    ResourceSpecCharacteristicValue vcdRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("virtual-compute-desc")
                                    .value(virtualComputeDesc))
                            .valueType("String");
                    rscvs.add(vcdRscv);
                }

                List<String> virtualStorageDesc = vnfdVdu.getVirtualStorageDesc();
                if(virtualStorageDesc == null)
                    log.debug("null virtual-storage-desc list, skipping value.");
                else {
                    ResourceSpecCharacteristicValue vsdRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("virtual-storage-desc")
                                    .value(virtualStorageDesc.toString()))
                            .valueType("List<String>");
                    rscvs.add(vsdRscv);
                }

                String swImageDesc = vnfdVdu.getSwImageDesc();
                if(swImageDesc == null)
                    log.debug("null sw-image-desc, skipping value.");
                else {
                    ResourceSpecCharacteristicValue sidRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("sw-image-desc")
                                    .value(swImageDesc))
                            .valueType("String");
                    rscvs.add(sidRscv);
                }

                rscVnfVdu.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVnfVdu);
            }
        }

        List<VnfdVirtualcomputedesc> vnfdVirtualcomputedescs = vnfd.getVirtualComputeDesc();
        if(vnfdVirtualcomputedescs == null)
            log.debug("null virtual-compute-desc list, skipping characteristics.");
        else {
            for(VnfdVirtualcomputedesc vnfdVirtualcomputedesc : vnfdVirtualcomputedescs) {

                String vnfdVirtualcomputedescId = vnfdVirtualcomputedesc.getId();
                if(vnfdVirtualcomputedescId == null) {
                    log.debug("null virtual-compute-desc item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscVvcd = new ResourceSpecCharacteristic()
                        .configurable(true)
                        .description("virtual-compute-desc " + vnfdVirtualcomputedescId)
                        .extensible(true)
                        .isUnique(true)
                        .name(vnfdVirtualcomputedescId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                VnfdVirtualmemory vnfdVirtualmemory = vnfdVirtualcomputedesc.getVirtualMemory();
                if(vnfdVirtualmemory == null)
                    log.debug("null virtual-memory, skipping value.");
                else {
                    String value = "";

                    Double size = vnfdVirtualmemory.getSize();
                    if(size == null)
                        log.debug("null virtual-memory size, not inserted in value field.");
                    else
                        value = "size: " + size.toString();

                    ResourceSpecCharacteristicValue vmRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("virtual-memory")
                                    .value(value))
                            .unitOfMeasure("GB")
                            .valueType("VnfdVirtualmemory");
                    rscvs.add(vmRscv);
                }

                VnfdVirtualcpu vnfdVirtualcpu = vnfdVirtualcomputedesc.getVirtualCpu();
                if(vnfdVirtualcpu == null)
                    log.debug("null virtual-cpu, skipping value.");
                else {
                    String value = "";

                    String numVirtualCpu = vnfdVirtualcpu.getNumVirtualCpu();
                    if(numVirtualCpu == null)
                        log.debug("null virtual-cpu num-virtual-cpu, not inserted in value field.");
                    else
                        value = "num-virtual-cpu: " + numVirtualCpu;

                    String clock = vnfdVirtualcpu.getClock();
                    if(clock == null)
                        log.debug("null virtual-cpu clock, not inserted in value field.");
                    else{
                        if(!value.isEmpty())
                            value = value + ", ";

                        value = value + "clock: " + clock;
                    }

                    ResourceSpecCharacteristicValue vvcRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("virtual-cpu")
                                    .value(value))
                            .unitOfMeasure("num_cpu * GHz")
                            .valueType("VnfdVirtualcpu");
                    rscvs.add(vvcRscv);
                }

                rscVvcd.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVvcd);
            }
        }

        List<VnfdVirtualstoragedesc> vnfdVirtualstoragedescs = vnfd.getVirtualStorageDesc();
        if(vnfdVirtualstoragedescs == null)
            log.debug("null virtual-storage-desc list, skipping characteristics.");
        else {
            for(VnfdVirtualstoragedesc vnfdVirtualstoragedesc : vnfdVirtualstoragedescs) {

                String vnfdVirtualstoragedescId = vnfdVirtualstoragedesc.getId();
                if(vnfdVirtualstoragedescId == null) {
                    log.debug("null virtual-storage-desc item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscVvsd = new ResourceSpecCharacteristic()
                        .configurable(true)
                        .description("virtual-storage-desc " + vnfdVirtualstoragedescId)
                        .extensible(true)
                        .isUnique(true)
                        .name(vnfdVirtualstoragedescId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                String typeOfStorage = vnfdVirtualstoragedesc.getTypeOfStorage();
                if(typeOfStorage == null)
                    log.debug("null type-of-storage, skipping value.");
                else {
                    ResourceSpecCharacteristicValue tsRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("type-of-storage")
                                    .value(typeOfStorage))
                            .valueType("String");
                    rscvs.add(tsRscv);
                }

                String sizeOfStorage = vnfdVirtualstoragedesc.getSizeOfStorage();
                if(sizeOfStorage == null)
                    log.debug("null size-of-storage, skipping value.");
                else {
                    ResourceSpecCharacteristicValue ssRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("size-of-storage")
                                    .value(sizeOfStorage))
                            .unitOfMeasure("GB")
                            .valueType("String");
                    rscvs.add(ssRscv);
                }

                rscVvsd.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVvsd);
            }
        }

        List<VnfdSwimagedesc> vnfdSwimagedescs = vnfd.getSwImageDesc();
        if(vnfdSwimagedescs == null)
            log.debug("null sw-image-desc list, skipping characteristics.");
        else {
            for(VnfdSwimagedesc vnfdSwimagedesc : vnfdSwimagedescs) {

                String vnfdSwimagedescId = vnfdSwimagedesc.getId();
                if(vnfdSwimagedescId == null) {
                    log.debug("null sw-image-desc item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscVsid = new ResourceSpecCharacteristic()
                        .configurable(true)
                        .description("sw-image-desc " + vnfdSwimagedescId)
                        .extensible(true)
                        .isUnique(true)
                        .name(vnfdSwimagedescId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                String version = vnfdSwimagedesc.getVersion();
                if(version == null)
                    log.debug("null version, skipping value");
                else {
                    ResourceSpecCharacteristicValue versionRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("version")
                                    .value(version))
                            .valueType("String");
                    rscvs.add(versionRscv);
                }

                VnfdChecksum vnfdChecksum = vnfdSwimagedesc.getChecksum();
                if(vnfdChecksum == null)
                    log.debug("null checksum, skipping value");
                else {
                    String value = "";

                    String algorithm = vnfdChecksum.getAlgorithm();
                    if(algorithm == null)
                        log.debug("null checksum algorithm, not inserted in value field.");
                    else
                        value = "algorithm: " + algorithm;

                    String hash = vnfdChecksum.getHash();
                    if(hash == null)
                        log.debug("null checksum hash, not inserted in value field.");
                    else {
                        if(!value.isEmpty())
                            value = value + ", ";

                        value = value + "hash: " + hash;
                    }

                    ResourceSpecCharacteristicValue checksumRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("checksum")
                                    .value(value))
                            .valueType("VnfdChecksum");
                    rscvs.add(checksumRscv);
                }

                VnfdSwimagedesc.ContainerFormatEnum containerFormat = vnfdSwimagedesc.getContainerFormat();
                if(containerFormat == null)
                    log.debug("null container-format, skipping value");
                else {
                    ResourceSpecCharacteristicValue cfRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("container-format")
                                    .value(containerFormat.toString()))
                            .valueType("VnfdSwimagedesc.ContainerFormatEnum");
                    rscvs.add(cfRscv);
                }

                VnfdSwimagedesc.DiskFormatEnum diskFormat = vnfdSwimagedesc.getDiskFormat();
                if(diskFormat == null)
                    log.debug("null disk-format, skipping value.");
                else {
                    ResourceSpecCharacteristicValue dfRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("disk-format")
                                    .value(diskFormat.toString()))
                            .valueType("VnfdSwimagedesc.DiskFormatEnum");
                    rscvs.add(dfRscv);
                }

                String minDisk = vnfdSwimagedesc.getMinDisk();
                if(minDisk == null)
                    log.debug("null min-disk, skipping value.");
                else {
                    ResourceSpecCharacteristicValue mdRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("min-disk")
                                    .value(minDisk))
                            .valueType("String");
                    rscvs.add(mdRscv);
                }

                Double minRam = vnfdSwimagedesc.getMinRam();
                if(minRam == null)
                    log.debug("null min-ram, skipping value.");
                else {
                    ResourceSpecCharacteristicValue mrRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("min-ram")
                                    .value(minRam.toString()))
                            .unitOfMeasure("GB")
                            .valueType("Double");
                    rscvs.add(mrRscv);
                }

                String size = vnfdSwimagedesc.getSize();
                if(size == null)
                    log.debug("null size, skipping value.");
                else {
                    ResourceSpecCharacteristicValue sRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("size")
                                    .value(size))
                            .valueType("String");
                    rscvs.add(sRscv);
                }

                String image = vnfdSwimagedesc.getImage();
                if(image == null)
                    log.debug("null image, skipping value.");
                else {
                    ResourceSpecCharacteristicValue iRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("image")
                                    .value(image))
                            .valueType("String");
                    rscvs.add(iRscv);
                }

                rscVsid.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVsid);
            }
        }

        List<Object> intVirtualLinkDescObjs = vnfd.getIntVirtualLinkDesc();
        if(intVirtualLinkDescObjs == null)
            log.debug("null int-virtual-link-desc (objs) list, skipping characteristics.");
        else {
            ArrayNode intVirtualLinkDesc = objectMapper.valueToTree(intVirtualLinkDescObjs);
            if(intVirtualLinkDesc == null)
                log.debug("null int-virtual-link-desc list, skipping characteristics.");
            else if(!intVirtualLinkDesc.isArray())
                log.debug("int-virtual-link-desc not a list, skipping characteristics.");
            else {
                for(JsonNode jsonNode : intVirtualLinkDesc) {

                    JsonNode jsonId = jsonNode.get("id");
                    if(jsonId == null || !jsonId.isTextual()) {
                        log.debug("null or non textual int-virtual-link-desc item id, skipping characteristic");
                        continue;
                    }

                    String ivldId = jsonId.asText();
                    ResourceSpecCharacteristic rscIvld = new ResourceSpecCharacteristic()
                            .configurable(true)
                            .description("int-virtual-link-desc " + ivldId)
                            .extensible(true)
                            .isUnique(true)
                            .name(ivldId);

                    List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                    String value = "";

                    JsonNode connectivityType = jsonNode.get("connectivity-type");
                    if(connectivityType == null)
                        log.debug("null connectivity-type, skipping value.");
                    else {
                        JsonNode layerProtocol = connectivityType.get("layer-protocol");
                        if(layerProtocol == null)
                            log.debug("null connectivity-type layer-protocol, not inserted in value field.");
                        else {
                            JsonNode protocol = layerProtocol.get("protocol");
                            if(protocol == null || !protocol.isTextual())
                                log.debug("null or non textual connectivity-type layer-protocol " +
                                        "protocol, not inserted in value field");
                            else
                                value = protocol.asText();
                        }
                    }

                    ResourceSpecCharacteristicValue pRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("connectivity-type")
                                    .value("layer-protocol -> protocol: " + value))
                            .valueType("Object");
                    rscvs.add(pRscv);

                    rscIvld.setResourceSpecCharacteristicValue(rscvs);

                    resourceSpecCharacteristics.add(rscIvld);
                }
            }
        }

        List<Object> extCpdObjs = vnfd.getExtCpd();
        if(extCpdObjs == null)
            log.debug("null ext-cpd (objs) list, skipping characteristics.");
        else {
            ArrayNode extCpd = objectMapper.valueToTree(extCpdObjs);
            if(extCpd == null)
                log.debug("null ext-cpd list, skipping characteristics.");
            else if(!extCpd.isArray())
                log.debug("ext-cpd not a list, skipping characteristics.");
            else {
                for(JsonNode jsonNode : extCpd) {

                    JsonNode jsonId = jsonNode.get("id");
                    if(jsonId == null || !jsonId.isTextual()) {
                        log.debug("null or non textual ext-cpd item id, skipping characteristic");
                        continue;
                    }

                    String ecId = jsonId.asText();
                    ResourceSpecCharacteristic rscEc = new ResourceSpecCharacteristic()
                            .configurable(true)
                            .description("ext-cpd " + ecId)
                            .extensible(true)
                            .isUnique(true)
                            .name(ecId);

                    List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                    JsonNode intCpd = jsonNode.get("int-cpd");
                    if(intCpd == null)
                        log.debug("null int-cpd, skipping value.");
                    else {
                        String value = "";

                        JsonNode vduId = intCpd.get("vdu-id");
                        if(vduId == null || !vduId.isTextual())
                            log.debug("null or non textual vdu-id, not inserted in value.");
                        else
                            value = "vdu-id: " + vduId.asText();

                        JsonNode cpd = intCpd.get("cpd");
                        if(cpd == null || !cpd.isTextual())
                            log.debug("null or non textual cpd, not inserted in value");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "cpd: " + cpd.asText();
                        }

                        ResourceSpecCharacteristicValue ecRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("int-cpd")
                                        .value(value))
                                .valueType("Object");
                        rscvs.add(ecRscv);
                    }

                    JsonNode intVirtualLinkDescJson = jsonNode.get("int-virtual-link-desc");
                    if(intVirtualLinkDescJson == null || !intVirtualLinkDescJson.isTextual())
                        log.debug("null or non textual int-virtual-link-desc, skipping value.");
                    else {
                        ResourceSpecCharacteristicValue ivldRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("int-virtual-link-desc")
                                        .value(intVirtualLinkDescJson.asText()))
                                .valueType("String");
                        rscvs.add(ivldRscv);
                    }

                    JsonNode layerProtocolJson = jsonNode.get("layer-protocol");
                    if(layerProtocolJson == null || !layerProtocolJson.isArray())
                        log.debug("null or not array layer-protocol list, skipping value.");
                    else {
                        List<String> layerProtocol = new ArrayList<>();
                        for(JsonNode node : layerProtocolJson) {
                            if(node.isTextual())
                                layerProtocol.add(node.asText());
                            else
                                log.debug("found a non textual layer-protocol list item, not inserted in value.");
                        }

                        ResourceSpecCharacteristicValue lpRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("layer-protocol")
                                        .value(layerProtocol.toString()))
                                .valueType("Object");
                        rscvs.add(lpRscv);
                    }

                    rscEc.setResourceSpecCharacteristicValue(rscvs);

                    resourceSpecCharacteristics.add(rscEc);
                }
            }
        }

        List<VnfdDf> vnfdDfs = vnfd.getDf();
        if(vnfdDfs == null)
            log.debug("null df list, skipping characteristics.");
        else {
            for(VnfdDf vnfdDf : vnfdDfs) {

                String vnfdDfId = vnfdDf.getId();
                if(vnfdDfId == null) {
                    log.debug("null df item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscVnfdDf = new ResourceSpecCharacteristic()
                        .configurable(true)
                        .description("df " + vnfdDfId)
                        .extensible(true)
                        .isUnique(true)
                        .name(vnfdDfId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                List<VnfdDfVduProfileItem> vnfdDfVduProfileItems = vnfdDf.getVduProfile();
                if(vnfdDfVduProfileItems == null)
                    log.debug("null vdu-profile list, skipping values.");
                else {
                    for(VnfdDfVduProfileItem vnfdDfVduProfileItem : vnfdDfVduProfileItems) {

                        String vnfdDfVduProfileItemId = vnfdDfVduProfileItem.getId();
                        if(vnfdDfVduProfileItemId == null) {
                            log.debug("null vdu-profile item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String minNumberOfInstances = vnfdDfVduProfileItem.getMinNumberOfInstances();
                        if(minNumberOfInstances == null)
                            log.debug("null vdu-profile min-number-of-instances, not inserted in value field.");
                        else
                            value = "min-number-of-instances: " + minNumberOfInstances;

                        String maxNumberOfInstances = vnfdDfVduProfileItem.getMaxNumberOfInstances();
                        if(maxNumberOfInstances == null)
                            log.debug("null vdu-profile max-number-of-instances, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "max-number-of-instances: " + maxNumberOfInstances;
                        }

                        List<Object> affinityOrAntiAffinityGroupsObjs =
                                vnfdDfVduProfileItem.getAffinityOrAntiAffinityGroup();
                        if(affinityOrAntiAffinityGroupsObjs == null)
                            log.debug("null vdu-profile affinity-or-anti-affinity-group (objs), not inserted in value field.");
                        else {
                            ArrayNode affinityOrAntiAffinityGroups =
                                    objectMapper.valueToTree(affinityOrAntiAffinityGroupsObjs);
                            if(affinityOrAntiAffinityGroups == null)
                                log.debug("null vdu-profile affinity-or-anti-affinity-group, not inserted in value field.");
                            else if(!affinityOrAntiAffinityGroups.isArray())
                                log.debug("vdu-profile affinity-or-anti-affinity-group not a list, not inserted in value field.");
                            else {
                                int i = 0;
                                StringBuilder valueBuilder = new StringBuilder(value);
                                for(JsonNode affinityOrAntiAffinityGroup : affinityOrAntiAffinityGroups) {
                                    JsonNode affinityOrAntiAffinityGroupId = affinityOrAntiAffinityGroup.get("id");
                                    if(affinityOrAntiAffinityGroupId == null || !affinityOrAntiAffinityGroupId.isTextual())
                                        log.debug("null or non textual vdu-profile affinity-or-anti-affinity-group id, " +
                                                "not inserted in value field.");
                                    else {
                                        if(!valueBuilder.toString().isEmpty())
                                            valueBuilder.append(", ");

                                        valueBuilder.append("affinity-or-anti-affinity-group").append(i).append(": ")
                                                .append("id: ").append(affinityOrAntiAffinityGroupId.asText());
                                        i++;
                                    }
                                }
                                value = valueBuilder.toString();
                            }
                        }

                        ResourceSpecCharacteristicValue vdvpRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("vdu-profile " + vnfdDfVduProfileItemId)
                                        .value(value))
                                .valueType("VnfdDfVduProfileItem");
                        rscvs.add(vdvpRscv);
                    }
                }

                List<VnfdInstantiationlevel> vnfdInstantiationlevels = vnfdDf.getInstantiationLevel();
                if(vnfdInstantiationlevels == null)
                    log.debug("null instantiation-level list, skipping values.");
                else {
                    for(VnfdInstantiationlevel vnfdInstantiationlevel : vnfdInstantiationlevels) {

                        String vnfdInstantiationlevelId = vnfdInstantiationlevel.getId();
                        if(vnfdInstantiationlevelId == null) {
                            log.debug("null instantiation-level, skipping value.");
                            continue;
                        }

                        List<String> valueLst = new ArrayList<>();

                        List<VnfdVdulevel> vdulevels = vnfdInstantiationlevel.getVduLevel();
                        if(vdulevels == null)
                            log.debug("null instantiation-level vdu-level item, not inserted in valued field.");
                        else {
                            for (VnfdVdulevel vdulevel : vdulevels) {
                                String lstItem = "";

                                String vduId = vdulevel.getVduId();
                                if(vduId == null)
                                    log.debug("null instantiation-level vdu-level item id, not inserted in value field.");
                                else
                                    lstItem = "vdu-id: " + vduId;

                                String numberOfInstances = vdulevel.getNumberOfInstances();
                                if(numberOfInstances == null)
                                    log.debug("null instantiation-level vdu-level item number-of-instances, not inserted in value field");
                                else {
                                    if(!lstItem.isEmpty())
                                        lstItem = lstItem + ", ";

                                    lstItem = lstItem + "number-of-instances: " + numberOfInstances;
                                }

                                valueLst.add("(" + lstItem + ")");
                            }
                        }

                        ResourceSpecCharacteristicValue vilRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("instantiation-level " + vnfdInstantiationlevelId)
                                        .value(valueLst.toString()))
                                .valueType("VnfdInstantiationlevel");
                        rscvs.add(vilRscv);
                    }
                }

                String defaultInstantiationLevel = vnfdDf.getDefaultInstantiationLevel();
                if(defaultInstantiationLevel == null)
                    log.debug("null default-instantiation-level, skipping value.");
                else {
                    ResourceSpecCharacteristicValue dilRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("default-instantiation-level")
                                    .value(defaultInstantiationLevel))
                            .valueType("String");
                    rscvs.add(dilRscv);
                }

                VnfdLcmoperationsconfiguration lcmOperationsConfiguration = vnfdDf.getLcmOperationsConfiguration();
                if(lcmOperationsConfiguration == null)
                    log.debug("null lcm-operations-configuration, skipping value.");
                else {
                    String value = "";

                    VnfdLcmoperationsconfigurationScalevnftolevelopconfig scaleVnfToLevelOpConfiguration =
                            lcmOperationsConfiguration.getScaleVnfToLevelOpConfig();
                    if(scaleVnfToLevelOpConfiguration == null)
                        log.debug("null lcm-operations-configuration " +
                                "scale-vnf-to-level-op-config, not inserted in value field.");
                    else {
                        Boolean isArbitraryTargetLevelsSupported =
                                scaleVnfToLevelOpConfiguration.isArbitraryTargetLevelsSupported();
                        if(isArbitraryTargetLevelsSupported == null)
                            log.debug("null lcm-operations-configuration " +
                                    "scale-vnf-to-level-op-config arbitrary-target-levels-supported, " +
                                    "not inserted in value field.");
                        else
                            value = "scale-vnf-to-level-op-config -> arbitrary-target-levels-supported: " +
                                    isArbitraryTargetLevelsSupported;
                    }

                    VnfdLcmoperationsconfigurationTerminatevnfopconfig terminateVnfOpConfig =
                            lcmOperationsConfiguration.getTerminateVnfOpConfig();
                    if(terminateVnfOpConfig == null)
                        log.debug("null lcm-operations-configuration terminate-vnf-op-config, " +
                                "not inserted in value field.");
                    else {
                        String minGracefulTermination = terminateVnfOpConfig.getMinGracefulTermination();
                        if(minGracefulTermination == null)
                            log.debug("null lcm-operations-configuration terminate-vnf-op-config " +
                                    "min-graceful-termination, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "terminate-vnf-op-config -> min-graceful-termination: " +
                                    minGracefulTermination;
                        }
                    }

                    VnfdLcmoperationsconfigurationOperatevnfopconfig operateVnfOpConfig =
                            lcmOperationsConfiguration.getOperateVnfOpConfig();
                    if(operateVnfOpConfig == null)
                        log.debug("null lcm-operations-configuration operate-vnf-op-config, " +
                                "not inserted in value field.");
                    else {
                        String minGracefulStopTimeout = operateVnfOpConfig.getMinGracefulStopTimeout();
                        if(minGracefulStopTimeout == null)
                            log.debug("null cm-operations-configuration operate-vnf-op-config min-graceful-stop-timeout, " +
                                    "not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "operate-vnf-op-config -> min-graceful-stop-timeout: " +
                                    minGracefulStopTimeout;
                        }
                    }

                    ResourceSpecCharacteristicValue locRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("lcm-operations-configuration")
                                    .value(value))
                            .valueType("VnfdLcmoperationsconfiguration");
                    rscvs.add(locRscv);
                }

                List<VnfdAffinityorantiaffinitygroup> affinityorantiaffinitygroups =
                        vnfdDf.getAffinityOrAntiAffinityGroup();
                if(affinityorantiaffinitygroups == null)
                    log.debug("null affinity-or-anti-affinity-group list, skipping values.");
                else {
                    for(VnfdAffinityorantiaffinitygroup affinityorantiaffinitygroup : affinityorantiaffinitygroups) {

                        String affinityorantiaffinitygroupId = affinityorantiaffinitygroup.getId();
                        if(affinityorantiaffinitygroupId == null) {
                            log.debug("null affinity-or-anti-affinity-group item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        VnfdAffinityorantiaffinitygroup.TypeEnum type = affinityorantiaffinitygroup.getType();
                        if(type == null)
                            log.debug("null affinity-or-anti-affinity-group type, not inserted in value field.");
                        else
                            value = "type: " + type.toString();

                        VnfdAffinityorantiaffinitygroup.ScopeEnum scope = affinityorantiaffinitygroup.getScope();
                        if(scope == null)
                            log.debug("null affinity-or-anti-affinity-group scope, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "scope: " + scope.toString();
                        }

                        ResourceSpecCharacteristicValue aagRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("affinity-or-anti-affinity-group " +
                                        affinityorantiaffinitygroupId)
                                        .value(value))
                                .valueType("VnfdAffinityorantiaffinitygroup");
                        rscvs.add(aagRscv);
                    }
                }

                rscVnfdDf.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVnfdDf);
            }
        }

        rsc.setResourceSpecCharacteristic(resourceSpecCharacteristics);

        String request = protocol + catalogHostname + ":" + catalogPort + contextPath +
                "/resourceCatalogManagement/v2/resourceSpecification";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPostRs = new HttpPost(request);

        String rscJson = objectMapper.writeValueAsString(rsc);
        log.info(rscJson);
        StringEntity stringEntityRsc = new StringEntity(rscJson);

        httpPostRs.setEntity(stringEntityRsc);
        httpPostRs.setHeader("Accept", "application/json");
        httpPostRs.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPostRs);
        } catch(IOException e) {
            String msg = "Offer Catalog Unreachable";
            log.error(msg);
            throw new CatalogPostException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 201) {
            String msg = "Offer Catalog POST request (ResourceSpecification) failed, status code: " + statusCode;
            log.error(msg);
            throw new CatalogPostException(msg);
        }

        ResourceSpecification rs = objectMapper.readValue(EntityUtils.toString(response.getEntity()), ResourceSpecification.class);

        String vnfdId = vnfd.getId();
        ResourceCandidateCreate rcc = new ResourceCandidateCreate()
                .name("vnfd " + vnfdId)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .resourceSpecification(new ResourceSpecificationRef()
                        .id(rs.getId())
                        .href(rs.getHref())
                        .name("vnfd specification: " + vnfdId));

        request = protocol + catalogHostname + ":" + catalogPort + contextPath +
                "/resourceCatalogManagement/v2/resourceCandidate";
        HttpPost httpPostRc = new HttpPost(request);

        String rccJson = objectMapper.writeValueAsString(rcc);
        StringEntity stringEntityRcc = new StringEntity(rccJson);

        httpPostRc.setEntity(stringEntityRcc);
        httpPostRc.setHeader("Accept", "application/json");
        httpPostRc.setHeader("Content-type", "application/json");

        try {
            response = httpClient.execute(httpPostRc);
        } catch(IOException e) {
            String msg = "Offer Catalog Unreachable";
            log.error(msg);
            throw new CatalogPostException(msg);
        }

        statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 201) {
            String msg = "Offer Catalog POST request (ResourceCandidate) failed, status code: " + statusCode;
            log.error(msg);
            throw new CatalogPostException(msg);
        }

        log.info("vnfd successfully translated and posted ");

        return new Pair<>(rcc, rsc);
    }
}
