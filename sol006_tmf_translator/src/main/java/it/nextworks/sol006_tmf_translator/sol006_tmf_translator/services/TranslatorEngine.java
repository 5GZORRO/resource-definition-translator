package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import it.nextworks.nfvmano.libs.common.enums.*;
import it.nextworks.nfvmano.libs.descriptors.sol006.*;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.enums.Kind;
import it.nextworks.tmf_offering_catalog.information_models.common.*;
import it.nextworks.tmf_offering_catalog.information_models.resource.*;
import it.nextworks.tmf_offering_catalog.information_models.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslatorEngine {

    private static final Logger log = LoggerFactory.getLogger(TranslatorEngine.class);

    public ResourceSpecificationCreate buildVnfdResourceSpecification(Vnfd vnfd) {

        String vnfdId = vnfd.getId();
        log.info("Translating vnfd " + vnfdId + ".");

        String vnfdProductName = vnfd.getProductName();
        String version = vnfd.getVersion();
        ResourceSpecificationCreate rsc = new ResourceSpecificationCreate()
                .description(vnfdProductName + " version " + version + " by " + vnfd.getProvider())
                .name(vnfdProductName)
                .version(version)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));

        List<ResourceSpecCharacteristic> resourceSpecCharacteristics = new ArrayList<>();

        ResourceSpecCharacteristic rscVnfdId = new ResourceSpecCharacteristic()
                .description("ID of the VNF descriptor")
                .name("vnfdId")
                .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                        .value(new Any().alias("vnfdId").value(vnfdId))));

        resourceSpecCharacteristics.add(rscVnfdId);

        List<String> vnfmInfo = vnfd.getVnfmInfo();
        if(vnfmInfo == null)
            log.debug("null vnfm-info list, skipping characteristics.");
        else {
            ResourceSpecCharacteristic rscVnfmInfo = new ResourceSpecCharacteristic()
                    .name("vnfm-info");

            List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

            ResourceSpecCharacteristicValue rscv = new ResourceSpecCharacteristicValue()
                    .value(new Any().value(vnfmInfo.toString()));
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
                        .description("vdu " + vnfdVduId)
                        .name(vnfdVdu.getName());

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                String virtualComputeDesc = vnfdVdu.getVirtualComputeDesc();
                if(virtualComputeDesc == null)
                    log.debug("null virtual-compute-desc, skipping value.");
                else {
                    List<VnfdVirtualcomputedesc> virtualcomputedescs = vnfd.getVirtualComputeDesc();
                    if(virtualcomputedescs == null)
                        log.debug("null virtual-compute-desc list, skipping value.");
                    else {
                        List<VnfdVirtualcomputedesc> tmp =
                                virtualcomputedescs.stream()
                                        .filter(vnfdVirtualcomputedesc ->
                                                vnfdVirtualcomputedesc.getId().equals(virtualComputeDesc))
                                        .collect(Collectors.toList());
                        if(tmp.size() != 1)
                            log.debug("virtual-compute-desc list for id " + virtualComputeDesc +
                                    ", skipping value.");
                        else {
                            VnfdVirtualcomputedesc vnfdVirtualcomputedesc = tmp.get(0);

                            VnfdVirtualmemory vnfdVirtualmemory = vnfdVirtualcomputedesc.getVirtualMemory();
                            if(vnfdVirtualmemory == null)
                                log.debug("null virtual-memory, skipping value.");
                            else {
                                String value = "";

                                Double size = vnfdVirtualmemory.getSize();
                                if(size == null)
                                    log.debug("null virtual-memory size, not inserted in value field.");
                                else
                                    value = size.toString();

                                ResourceSpecCharacteristicValue vmRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("virtual-memory")
                                                .value(value))
                                        .unitOfMeasure("MB");
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
                                    value = numVirtualCpu + " vCPU";

                                String clock = vnfdVirtualcpu.getClock();
                                if(clock == null)
                                    log.debug("null virtual-cpu clock, not inserted in value field.");
                                else{
                                    if(!value.isEmpty())
                                        value = value + ", ";

                                    value = value + " @" + clock + "GHz";
                                }

                                ResourceSpecCharacteristicValue vvcRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("virtual-cpu")
                                                .value(value))
                                        .unitOfMeasure("num_cpu * GHz");
                                rscvs.add(vvcRscv);
                            }
                        }
                    }
                }

                List<String> virtualStorageDescs = vnfdVdu.getVirtualStorageDesc();
                if(virtualStorageDescs == null)
                    log.debug("null virtual-storage-desc list, skipping value.");
                else {
                    List<VnfdVirtualstoragedesc> vnfdVirtualstoragedescs = vnfd.getVirtualStorageDesc();
                    if(vnfdVirtualstoragedescs == null)
                        log.debug("null virtual-storage-desc list, skipping value.");
                    else {
                        int i = 0;
                        for(String virtualStorageDesc : virtualStorageDescs) {
                            List<VnfdVirtualstoragedesc> tmp =
                                    vnfdVirtualstoragedescs.stream()
                                            .filter(virtualStorageDescItem ->
                                                    virtualStorageDescItem.getId().equals(virtualStorageDesc))
                                            .collect(Collectors.toList());
                            if(tmp.size() != 1)
                                log.debug("virtual-compute-desc list for id " + virtualComputeDesc +
                                        ", skipping value.");
                            else {
                                VnfdVirtualstoragedesc vnfdVirtualstoragedesc = tmp.get(0);

                                String typeOfStorage = vnfdVirtualstoragedesc.getTypeOfStorage();
                                if(typeOfStorage == null)
                                    log.debug("null type-of-storage, skipping value.");
                                else {
                                    ResourceSpecCharacteristicValue typeRscv = new ResourceSpecCharacteristicValue()
                                            .value(new Any().alias("type-of-storage " + i).value(typeOfStorage));
                                    rscvs.add(typeRscv);
                                }

                                String sizeOfStorage = vnfdVirtualstoragedesc.getSizeOfStorage();
                                if(sizeOfStorage == null)
                                    log.debug("null size-of-storage, skipping value.");
                                else {
                                    ResourceSpecCharacteristicValue sizeRscv = new ResourceSpecCharacteristicValue()
                                            .value(new Any().alias("size-of-storage " + i).value(sizeOfStorage ))
                                            .unitOfMeasure("GB");
                                    rscvs.add(sizeRscv);
                                }
                                i++;
                            }
                        }
                    }
                }

                String swImageDesc = vnfdVdu.getSwImageDesc();
                if(swImageDesc == null)
                    log.debug("null sw-image-desc, skipping value.");
                else {
                    List<VnfdSwimagedesc> swImageDescs= vnfd.getSwImageDesc();
                    if(swImageDescs == null)
                        log.debug("null sw-image-desc list, skipping value.");
                    else {
                        List<VnfdSwimagedesc> tmp =
                                swImageDescs.stream()
                                        .filter(vnfdSwimagedesc ->
                                                vnfdSwimagedesc.getId().equals(swImageDesc))
                                        .collect(Collectors.toList());
                        if(tmp.size() != 1)
                            log.debug("virtual-compute-desc list for id " + virtualComputeDesc +
                                    ", skipping value.");
                        else {
                            VnfdSwimagedesc vnfdSwimagedesc = tmp.get(0);

                            String vnfdSwimagedescVersion = vnfdSwimagedesc.getVersion();
                            if(vnfdSwimagedescVersion == null)
                                log.debug("null version, skipping value");
                            else {
                                ResourceSpecCharacteristicValue versionRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-version").value(vnfdSwimagedescVersion));
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
                                        .value(new Any().alias("sw-image-checksum").value(value));
                                rscvs.add(checksumRscv);
                            }

                            ContainerFormatEnum containerFormat = vnfdSwimagedesc.getContainerFormat();
                            if(containerFormat == null)
                                log.debug("null container-format, skipping value");
                            else {
                                ResourceSpecCharacteristicValue cfRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-container-format").value(containerFormat.toString()));
                                rscvs.add(cfRscv);
                            }

                            DiskFormatEnum diskFormat = vnfdSwimagedesc.getDiskFormat();
                            if(diskFormat == null)
                                log.debug("null disk-format, skipping value.");
                            else {
                                ResourceSpecCharacteristicValue dfRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-disk-format").value(diskFormat.toString()));
                                rscvs.add(dfRscv);
                            }

                            String minDisk = vnfdSwimagedesc.getMinDisk();
                            if(minDisk == null)
                                log.debug("null min-disk, skipping value.");
                            else {
                                ResourceSpecCharacteristicValue mdRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-min-disk").value(minDisk));
                                rscvs.add(mdRscv);
                            }

                            Double minRam = vnfdSwimagedesc.getMinRam();
                            if(minRam == null)
                                log.debug("null min-ram, skipping value.");
                            else {
                                ResourceSpecCharacteristicValue mrRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-min-ram")
                                                .value(minRam.toString()))
                                        .unitOfMeasure("MB");
                                rscvs.add(mrRscv);
                            }

                            String size = vnfdSwimagedesc.getSize();
                            if(size == null)
                                log.debug("null size, skipping value.");
                            else {
                                ResourceSpecCharacteristicValue sRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image-size").value(size));
                                rscvs.add(sRscv);
                            }

                            String image = vnfdSwimagedesc.getImage();
                            if(image == null)
                                log.debug("null image, skipping value.");
                            else {
                                ResourceSpecCharacteristicValue iRscv = new ResourceSpecCharacteristicValue()
                                        .value(new Any().alias("sw-image").value(image));
                                rscvs.add(iRscv);
                            }
                        }
                    }
                }

                rscVnfVdu.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVnfVdu);
            }
        }

        List<IntVirtualLinkDesc> intVirtualLinkDescs = vnfd.getIntVirtualLinkDesc();
        if(intVirtualLinkDescs == null)
            log.debug("null int-virtual-link-desc list, skipping characteristics.");
        else {
            for(IntVirtualLinkDesc intVirtualLinkDesc : intVirtualLinkDescs) {

                String intVirtualLinkDescId = intVirtualLinkDesc.getId();
                if(intVirtualLinkDescId == null) {
                    log.debug("null int-virtual-link-desc item id, skipping characteristic");
                    continue;
                }

                ResourceSpecCharacteristic rscIvld = new ResourceSpecCharacteristic()
                        .description("int-virtual-link-desc " + intVirtualLinkDescId)
                        .name(intVirtualLinkDescId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                String value = "";

                ConnectivityTypeSchema connectivityTypeSchema = intVirtualLinkDesc.getConnectivityTypeSchema();
                if(connectivityTypeSchema == null)
                    log.debug("null connectivity-type, skipping value.");
                else {
                    List<String> layerProtocols = connectivityTypeSchema.getLayerProtocol();
                    if(layerProtocols == null)
                        log.debug("null connectivity-type layer-protocol, not inserted in value field.");
                    else
                        value = layerProtocols.toString();
                }

                ResourceSpecCharacteristicValue pRscv = new ResourceSpecCharacteristicValue()
                        .value(new Any().alias("connectivity-type").value("layer-protocol -> protocol: " + value));
                rscvs.add(pRscv);

                rscIvld.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscIvld);
            }
        }

        List<ExtCpd> extCpds = vnfd.getExtCpd();
        ResourceSpecCharacteristic nExtCpd = new ResourceSpecCharacteristic()
                .description("Number of external connection points.")
                .name("nExtCpd");
        List<ResourceSpecCharacteristicValue> nExtCpdValueLst = new ArrayList<>();
        if(extCpds == null) {
            ResourceSpecCharacteristicValue nExtCpdValue = new ResourceSpecCharacteristicValue()
                    .value(new Any().alias("number of external connection points").value("0"));
            nExtCpdValueLst.add(nExtCpdValue);

            nExtCpd.setResourceSpecCharacteristicValue(nExtCpdValueLst);
            resourceSpecCharacteristics.add(nExtCpd);
        } else {

            ResourceSpecCharacteristicValue nExtCpdValue = new ResourceSpecCharacteristicValue()
                    .value(new Any().alias("number of external connection points").value(String.valueOf(extCpds.size())));
            nExtCpdValueLst.add(nExtCpdValue);

            nExtCpd.setResourceSpecCharacteristicValue(nExtCpdValueLst);
            resourceSpecCharacteristics.add(nExtCpd);

            for(ExtCpd extCpd : extCpds) {

                String extCpdId = extCpd.getId();
                if(extCpdId == null) {
                    log.debug("null ext-cpd item id, skipping characteristic");
                    continue;
                }

                ResourceSpecCharacteristic rscEc = new ResourceSpecCharacteristic()
                        .description(extCpd.getDescription())
                        .name("External Connection Point " + extCpdId);

                List<ResourceSpecCharacteristicValue> rscvs = new ArrayList<>();

                String intVirtualLinkDesc = extCpd.getIntVirtualLinkDesc();
                if(intVirtualLinkDesc == null)
                    log.debug("null int-virtual-link-desc, skipping value.");
                else {
                    ResourceSpecCharacteristicValue ivldRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("int-virtual-link-desc").value(intVirtualLinkDesc));
                    rscvs.add(ivldRscv);
                }

                List<String> layerProtocols = extCpd.getLayerProtocols();
                if(layerProtocols == null)
                    log.debug("null layer-protocol list, skipping value.");
                else {
                    ResourceSpecCharacteristicValue lpRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("layer-protocol").value(layerProtocols.toString()));
                    rscvs.add(lpRscv);
                }

                rscEc.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscEc);
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
                        .description("df " + vnfdDfId)
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

                        List<AffinityOrAntiAffinityGroupIdSchema> affinityOrAntiAffinityGroupIds =
                                vnfdDfVduProfileItem.getAffinityOrAntiAffinityGroup();
                        if(affinityOrAntiAffinityGroupIds == null)
                            log.debug("null vdu-profile affinity-or-anti-affinity-group, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(AffinityOrAntiAffinityGroupIdSchema affinityOrAntiAffinityGroupId : affinityOrAntiAffinityGroupIds) {
                                String id = affinityOrAntiAffinityGroupId.getAffinityOrAntiAffinityGroupIdId();
                                if(id == null)
                                    log.debug("null vdu-profile affinity-or-anti-affinity-group id, " +
                                            "not inserted in value field.");
                                else
                                    valueLst.add("( " + "id: " + id + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "affinity-or-anti-affinity-group: " + valueLst.toString();
                        }

                        ResourceSpecCharacteristicValue vdvpRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("vdu-profile " + vnfdDfVduProfileItemId).value(value));
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
                                        .value(valueLst.toString()));
                        rscvs.add(vilRscv);
                    }
                }

                String defaultInstantiationLevel = vnfdDf.getDefaultInstantiationLevel();
                if(defaultInstantiationLevel == null)
                    log.debug("null default-instantiation-level, skipping value.");
                else {
                    ResourceSpecCharacteristicValue dilRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("default-instantiation-level").value(defaultInstantiationLevel));
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
                            .value(new Any().alias("lcm-operations-configuration").value(value));
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

                        TypeEnum type = affinityorantiaffinitygroup.getType();
                        if(type == null)
                            log.debug("null affinity-or-anti-affinity-group type, not inserted in value field.");
                        else
                            value = "type: " + type.toString();

                        ScopeEnum scope = affinityorantiaffinitygroup.getScope();
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
                                        .value(value));
                        rscvs.add(aagRscv);
                    }
                }

                rscVnfdDf.setResourceSpecCharacteristicValue(rscvs);

                resourceSpecCharacteristics.add(rscVnfdDf);
            }
        }

        rsc.setResourceSpecCharacteristic(resourceSpecCharacteristics);

        return rsc;
    }

    public ResourceCandidateCreate
    buildVnfdResourceCandidate(String productName, Pair<String, String> pair, ResourceSpecification rs) {

        return new ResourceCandidateCreate()
                .name(productName)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .category(Collections.singletonList(new ResourceCategoryRef()
                        .name(Kind.VNF.name())
                        .href(pair.getFirst())
                        .id(pair.getSecond())))
                .resourceSpecification(new ResourceSpecificationRef()
                        .id(rs.getId())
                        .href(rs.getHref()));
    }

    public ResourceSpecificationCreate buildPnfdResourceSpecification(Pnfd pnfd) {

        String pnfdId = pnfd.getId();
        log.info("Translating pnfd " + pnfdId + ".");

        String pnfdName = pnfd.getName();
        String pnfdVersion = pnfd.getVersion();
        ResourceSpecificationCreate rsc = new ResourceSpecificationCreate()
                .description(pnfdName + " version " + pnfdVersion + " by " + pnfd.getProvider() +
                        "; " + pnfd.getFunctionDescription())
                .name(pnfdName)
                .version(pnfdVersion)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));

        List<ResourceSpecCharacteristic> resourceSpecCharacteristics = new ArrayList<>();

        ResourceSpecCharacteristic rscPnfdId = new ResourceSpecCharacteristic()
                .name("pnfdId")
                .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                        .value(new Any().alias("pnfdId").value(pnfdId))));

        resourceSpecCharacteristics.add(rscPnfdId);

        List<Cpd> extCpds = pnfd.getExtCpd();
        if(extCpds == null)
            log.debug("null ext-cpd list, skipping characteristics.");
        else {
            for(Cpd extCpd : extCpds) {

                String extCpdId = extCpd.getId();
                if(extCpdId == null) {
                    log.debug("null ext-cpd item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscPnfdExtCpd = new ResourceSpecCharacteristic()
                        .description("ext-cpd " + extCpdId + ": " + extCpd.getDescription())
                        .name(extCpdId);

                List<ResourceSpecCharacteristicValue> rscv = new ArrayList<>();

                List<String> layerProtocol = extCpd.getLayerProtocol();
                if(layerProtocol == null)
                    log.debug("null layer-protocol list, skipping value.");
                else {
                    ResourceSpecCharacteristicValue lpRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("layer-protocol")
                                    .value(layerProtocol.toString()));
                    rscv.add(lpRscv);
                }

                List<CpdProtocol> protocols = extCpd.getProtocol();
                if(protocols == null)
                    log.debug("null ext-cpd protocol list, skipping values.");
                else {
                    int i = 0;
                    for(CpdProtocol cpdProtocol : protocols) {

                        String value = "";

                        List<CpdAddressdata> cpdAddressdata = cpdProtocol.getAddressData();
                        if(cpdAddressdata == null)
                            log.debug("null ext-cpd protocol address-data list, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();

                            for(CpdAddressdata cpdAddressdatum : cpdAddressdata) {
                                String valueAddressData = "";

                                CpdL2addressdata cpdL2addressdata = cpdAddressdatum.getL2AddressData();
                                if(cpdL2addressdata == null)
                                    log.debug("null ext-cpd protocol address-data item l2-address-data, " +
                                            "not inserted in value field.");
                                else {
                                    Boolean isMacAddressAssignment = cpdL2addressdata.isMacAddressAssignment();
                                    if(isMacAddressAssignment == null)
                                        log.debug("null ext-cpd protocol address-data item l2-address-data " +
                                                "mac-address-assignment, not inserted in value field.");
                                    else
                                        valueAddressData = "l2-address-data -> mac-address-assignment: " +
                                                        isMacAddressAssignment;
                                }

                                CpdL3addressdata cpdL3addressdata = cpdAddressdatum.getL3AddressData();
                                if(cpdL3addressdata == null)
                                    log.debug("null ext-cpd protocol address-data item l3-address-data, " +
                                            "not inserted in value field");
                                else {
                                    Boolean ipAddressAssignment = cpdL3addressdata.isIpAddressAssignment();
                                    if(ipAddressAssignment == null)
                                        log.debug("null ext-cpd protocol address-data item l3-address-data" +
                                                "ip-address-assignment, not inserted in value field.");
                                    else {
                                        if(!valueAddressData.isEmpty())
                                            valueAddressData = valueAddressData + ", ";

                                        valueAddressData =
                                                valueAddressData + "l3-address-data -> " + "ip-address-assignment: " +
                                                        ipAddressAssignment;
                                    }

                                    Boolean floatingIpActivated = cpdL3addressdata.isFloatingIpActivated();
                                    if(floatingIpActivated == null)
                                        log.debug("null ext-cpd protocol address-data item l3-address-data" +
                                                "floating-ip-activated, not inserted in value field.");
                                    else {
                                        if(!valueAddressData.isEmpty())
                                            valueAddressData = valueAddressData + ", ";

                                        valueAddressData =
                                                valueAddressData + "l3-address-data -> floating-ip-activated: " +
                                                        floatingIpActivated;
                                    }

                                    String numberOfIpAddresses = cpdL3addressdata.getNumberOfIpAddresses();
                                    if(numberOfIpAddresses == null)
                                        log.debug("null ext-cpd protocol address-data item l3-address-data" +
                                                "number-of-ip-addresses, not inserted in value field.");
                                    else {
                                        if(!valueAddressData.isEmpty())
                                            valueAddressData = valueAddressData + ", ";

                                        valueAddressData =
                                                valueAddressData + "l3-address-data -> number-of-ip-addresses: " +
                                                        numberOfIpAddresses;
                                    }
                                }

                                String type = cpdAddressdatum.getType();
                                if(type == null)
                                    log.debug("null ext-cpd protocol address-data item type, " +
                                            "not inserted in value field.");
                                else {
                                    if(!valueAddressData.isEmpty())
                                        valueAddressData = valueAddressData + ", ";

                                    valueAddressData = valueAddressData + "type: " + type;
                                }

                                valueLst.add("( " + valueAddressData + " )");
                            }

                            value = "address-data: " + valueLst.toString();
                        }

                        String associatedLayerProtocol = cpdProtocol.getAssociatedLayerProtocol();
                        if(associatedLayerProtocol == null)
                            log.debug("null ext-cpd protocol associated-layer-protocol, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "associated-layer-protocol: " + associatedLayerProtocol;
                        }

                        ResourceSpecCharacteristicValue pRscv = new ResourceSpecCharacteristicValue()
                                .value(new Any().alias("protocol" + i).value(value));
                        rscv.add(pRscv);
                        i++;
                    }
                }

                String role = extCpd.getRole();
                if(role == null)
                    log.debug("null role, skipping value.");
                else {
                    ResourceSpecCharacteristicValue rRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("role").value(role));
                    rscv.add(rRscv);
                }

                Boolean isTrunkMode = extCpd.isTrunkMode();
                if(isTrunkMode == null)
                    log.debug("null trunk-mode, skipping value.");
                else {
                    ResourceSpecCharacteristicValue itmRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("trunk-mode").value(isTrunkMode.toString()));
                    rscv.add(itmRscv);
                }

                rscPnfdExtCpd.setResourceSpecCharacteristicValue(rscv);

                resourceSpecCharacteristics.add(rscPnfdExtCpd);
            }
        }

        String geographicalLocationInfo = pnfd.getGeographicalLocationInfo();
        if(geographicalLocationInfo == null)
            log.debug("null geographical-location-info, skipping characteristic");
        else {
            ResourceSpecCharacteristic rscGli = new ResourceSpecCharacteristic()
                    .description("geographical-location-info")
                    .name("geographical-location-info")
                    .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                            .value(new Any().value(geographicalLocationInfo))));
            resourceSpecCharacteristics.add(rscGli);
        }

        String invariantId = pnfd.getInvariantId();
        if(invariantId == null)
            log.debug("null invariant-id, skipping characteristic.");
        else {
            ResourceSpecCharacteristic rscIi = new ResourceSpecCharacteristic()
                    .description("invariant-id")
                    .name("invariant-id")
                    .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                    .value(new Any().value(invariantId))));
            resourceSpecCharacteristics.add(rscIi);
        }

        List<SecurityParameters> securityParameters = pnfd.getSecurity();
        if(securityParameters == null)
            log.debug("null security list, skipping characteristics.");
        else {
            int i = 0;
            for(SecurityParameters securityParameter : securityParameters) {
                ResourceSpecCharacteristic rscSp = new ResourceSpecCharacteristic()
                        .description("security" + i)
                        .name("security" + i);

                List<ResourceSpecCharacteristicValue> rscv = new ArrayList<>();

                String certificate = securityParameter.getCertificate();
                if(certificate == null)
                    log.debug("null certificate, skipping value.");
                else {
                    ResourceSpecCharacteristicValue cRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("certificate").value(certificate));
                    rscv.add(cRscv);
                }

                String algorithm = securityParameter.getAlgorithm();
                if(algorithm == null)
                    log.debug("null algorithm, skipping value.");
                else {
                    ResourceSpecCharacteristicValue aRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("algorithm").value(algorithm));
                    rscv.add(aRscv);
                }

                String signature = securityParameter.getSignature();
                if(signature == null)
                    log.debug("null signature, skipping value.");
                else {
                    ResourceSpecCharacteristicValue sRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("signature").value(signature));
                    rscv.add(sRscv);
                }

                rscSp.setResourceSpecCharacteristicValue(rscv);

                resourceSpecCharacteristics.add(rscSp);

                i++;
            }
        }

        List<SecuritygroupruleSecuritygrouprule> securityGroupRules = pnfd.getSecurityGroupRule();
        if(securityGroupRules == null)
            log.debug("null security-group-rule list, skipping characteristics.");
        else {
            for(SecuritygroupruleSecuritygrouprule securityGroupRule : securityGroupRules) {

                String securityGroupRuleId = securityGroupRule.getId();
                if(securityGroupRuleId == null) {
                    log.debug("null security-group-rule item id, skipping characteristic.");
                    continue;
                }

                ResourceSpecCharacteristic rscSgr = new ResourceSpecCharacteristic()
                        .description("security-group-rule " + securityGroupRuleId + ": " +
                                securityGroupRule.getDescription())
                        .name(securityGroupRuleId);

                List<ResourceSpecCharacteristicValue> rscv = new ArrayList<>();

                DirectionEnum direction = securityGroupRule.getDirection();
                if(direction == null)
                    log.debug("null direction, skipping value");
                else {
                    ResourceSpecCharacteristicValue dRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("direction").value(direction.toString()));
                    rscv.add(dRscv);
                }

                EtherTypeEnum etherType = securityGroupRule.getEtherType();
                if(etherType == null)
                    log.debug("null ether-type, skipping value");
                else {
                    ResourceSpecCharacteristicValue etRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("ether-type").value(etherType.toString()));
                    rscv.add(etRscv);
                }

                String portRangeMax = securityGroupRule.getPortRangeMax();
                if(portRangeMax == null)
                    log.debug("null port-range-max, skipping value");
                else {
                    ResourceSpecCharacteristicValue prmRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("port-range-max").value(portRangeMax));
                    rscv.add(prmRscv);
                }

                String portRangeMin = securityGroupRule.getPortRangeMin();
                if(portRangeMin == null)
                    log.debug("null port-range-min, skipping value");
                else {
                    ResourceSpecCharacteristicValue prmRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("port-range-min").value(portRangeMin));
                    rscv.add(prmRscv);
                }

                ProtocolEnum protocol = securityGroupRule.getProtocol();
                if(protocol == null)
                    log.debug("null protocol, skipping value");
                else {
                    ResourceSpecCharacteristicValue pRscv = new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("protocol").value(protocol.toString()));
                    rscv.add(pRscv);
                }

                rscSgr.setResourceSpecCharacteristicValue(rscv);

                resourceSpecCharacteristics.add(rscSgr);
            }
        }

        rsc.setResourceSpecCharacteristic(resourceSpecCharacteristics);

        return rsc;
    }

    public ResourceCandidateCreate
    buildPnfdResourceCandidate(String name, Pair<String, String> pair, ResourceSpecification rs) {
        return new ResourceCandidateCreate()
                .name("pnfd:" + name)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .category(Collections.singletonList(new ResourceCategoryRef()
                        .name(Kind.PNF.name())
                        .href(pair.getFirst())
                        .id(pair.getSecond())))
                .resourceSpecification(new ResourceSpecificationRef()
                        .id(rs.getId())
                        .href(rs.getHref()));
    }

    public ServiceSpecificationCreate buildNsdServiceSpecification(Nsd nsd,
                                                                   List<ResourceSpecificationRef> vnfdRefs,
                                                                   List<ResourceSpecificationRef> pnfdRefs,
                                                                   List<ServiceSpecificationRef> nsdRefs) {

        String nsdId = nsd.getId();
        log.info("Translating nsd " + nsdId + ".");

        String nsdName = nsd.getName();
        String nsdVersion = nsd.getVersion();
        ServiceSpecificationCreate ssc = new ServiceSpecificationCreate()
                .description(nsdName + " version " + nsdVersion + " by " + nsd.getDesigner())
                .name(nsdName)
                .version(nsdVersion)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));

        List<ServiceSpecCharacteristic> serviceSpecCharacteristics = new ArrayList<>();

        List<ResourceSpecificationRef> rsRefs = new ArrayList<>();

        for(ResourceSpecificationRef vnfdRef : vnfdRefs) {
            rsRefs.add(new ResourceSpecificationRef()
                    .id(vnfdRef.getId())
                    .href(vnfdRef.getHref()));
        }

        for(ResourceSpecificationRef pnfdRef : pnfdRefs) {
            rsRefs.add(new ResourceSpecificationRef()
                    .id(pnfdRef.getId())
                    .href(pnfdRef.getHref()));
        }

        ssc.setResourceSpecification(rsRefs);

        List<ServiceSpecRelationship> ssrRefs = new ArrayList<>();

        for(ServiceSpecificationRef nsdRef : nsdRefs) {
            ssrRefs.add(new ServiceSpecRelationship()
                    .id(nsdRef.getId())
                    .href(nsdRef.getHref()));
        }

        ssc.setServiceSpecRelationship(ssrRefs);

        ServiceSpecCharacteristic sscNsdId = new ServiceSpecCharacteristic()
                .name("nsdId")
                .serviceSpecCharacteristicValue(Collections.singletonList(new ServiceSpecCharacteristicValue()
                        .value(new Any().alias("nsdId").value(nsdId))));

        serviceSpecCharacteristics.add(sscNsdId);

        List<NsdSapd> nsdSapds = nsd.getSapd();
        if(nsdSapds == null)
            log.debug("null sapd list, skipping characteristics.");
        else {
            for(NsdSapd nsdSapd : nsdSapds) {

                String nsdSapdId = nsdSapd.getId();
                if(nsdSapdId == null) {
                    log.debug("null sapd item id, skipping characteristic.");
                    continue;
                }

                ServiceSpecCharacteristic sscNsdSapd = new ServiceSpecCharacteristic()
                        .description("sapd " + nsdSapdId)
                        .name(nsdSapdId);

                List<ServiceSpecCharacteristicValue> sscv = new ArrayList<>();

                String virtualLinkDesc = nsdSapd.getVirtualLinkDesc();
                if(virtualLinkDesc ==  null)
                    log.debug("null virtual-link-desc, skipping value.");
                else {
                    ServiceSpecCharacteristicValue vldSscv = new ServiceSpecCharacteristicValue()
                            .value(new Any().alias("virtual-link-desc").value(virtualLinkDesc));
                    sscv.add(vldSscv);
                }

                VnfAssociatedCpdId vnf = nsdSapd.getVnf();
                if(vnf == null)
                    log.debug("null vnf, skipping value.");
                else {
                    String value = "";

                    String vnfId = vnf.getVnfdId();
                    if(vnfId == null)
                        log.debug("null vnf id, not inserted in value field.");
                    else
                        value = "vnfd-id: " + vnfId;

                    String extCpdId = vnf.getExtCpdId();
                    if(extCpdId == null)
                        log.debug("null vnf ext-cpd-id, not inserted in value field.");
                    else {
                        if(!value.isEmpty())
                            value = value + ", ";

                        value = value + "ext-cpd-id: " + extCpdId;
                    }

                    ServiceSpecCharacteristicValue vnfSscv = new ServiceSpecCharacteristicValue()
                            .value(new Any().alias("vnf").value(value));
                    sscv.add(vnfSscv);
                }

                PnfAssociatedCpdId pnf = nsdSapd.getPnf();
                if(pnf == null)
                    log.debug("null pnf, skipping value");
                else {
                    String value = "";

                    String pnfId = pnf.getPnfdId();
                    if(pnfId == null)
                        log.debug("null pnf id, not inserted in value field.");
                    else
                        value = "pnfd-id: " + pnfId;

                    String extCpdId = pnf.getExtCpdId();
                    if(extCpdId == null)
                        log.debug("null pnf ext-cpd-id, not inserted in value field.");
                    else {
                        if(!value.isEmpty())
                            value = value + ", ";

                        value = value + "ext-cpd-id: " + extCpdId;
                    }

                    ServiceSpecCharacteristicValue pnfSscv = new ServiceSpecCharacteristicValue()
                            .value(new Any().alias("pnf").value(value));
                    sscv.add(pnfSscv);
                }

                NsAssociatedCpdId ns = nsdSapd.getNs();
                if(ns == null)
                    log.debug("null ns, skipping value.");
                else {
                    String value = "";

                    String nsId = ns.getNsdId();
                    if(nsId == null)
                        log.debug("null ns id, not inserted in value field.");
                    else
                        value = "ns-id: " + nsId;

                    String extCpdId = ns.getExtCpdId();
                    if(extCpdId == null)
                        log.debug("null ns ext-cpd-id, not inserted in value field.");
                    else {
                        if(!value.isEmpty())
                            value = value + ", ";

                        value = value + "ext-cpd-id: " + extCpdId;
                    }

                    ServiceSpecCharacteristicValue nsSscv = new ServiceSpecCharacteristicValue()
                            .value(new Any().alias("ns").value(value));
                    sscv.add(nsSscv);
                }

                sscNsdSapd.setServiceSpecCharacteristicValue(sscv);

                serviceSpecCharacteristics.add(sscNsdSapd);
            }
        }

        List<VirtualLinkDesc> virtualLinkDescs = nsd.getVirtualLinkDesc();
        if(virtualLinkDescs == null)
            log.debug("null virtual-link-desc list, skipping characteristics.");
        else {
            for(VirtualLinkDesc virtualLinkDesc : virtualLinkDescs) {

                String virtualLinkDescId = virtualLinkDesc.getId();
                if(virtualLinkDescId == null) {
                    log.debug("null virtual-link-desc item id, skipping characteristic.");
                    continue;
                }

                ServiceSpecCharacteristic sscVld = new ServiceSpecCharacteristic()
                        .description(virtualLinkDesc.getDescription())
                        .name("Virtual Link " + virtualLinkDescId);

                List<ServiceSpecCharacteristicValue> sscv = new ArrayList<>();

                ConnectivityTypeSchema connectivityType = virtualLinkDesc.getConnectivityTypeSchema();
                if(connectivityType == null)
                    log.debug("null connectivity-type, skipping value.");
                else {
                    List<String> layerProtocols = connectivityType.getLayerProtocol();
                    if(layerProtocols == null)
                        log.debug("null connectivity-type layer-protocol, not inserted in value field.");
                    else {
                        ServiceSpecCharacteristicValue lpSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("connectivity-type")
                                        .value("layer-protocol: " + layerProtocols.toString()));
                        sscv.add(lpSscv);
                    }
                }

                List<VirtualLinkDescDf> virtualLinkDescDfs = virtualLinkDesc.getDf();
                if(virtualLinkDescDfs == null)
                    log.debug("null virtual-link-desc df list, skipping values.");
                else {
                    for (VirtualLinkDescDf virtualLinkDescDf : virtualLinkDescDfs) {

                        String virtualLinkDescDfId = virtualLinkDescDf.getId();
                        if(virtualLinkDescDfId == null) {
                            log.debug("null virtual-link-desc df item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        QosSchema qos = virtualLinkDescDf.getQos();
                        if(qos == null)
                            log.debug("null virtual-link-desc df qos, not inserted in value field.");
                        else {
                            String latency = qos.getLatency();
                            if(latency == null)
                                log.debug("null virtual-link-desc df qos latency, not inserted in value field.");
                            else
                                value = "qos -> latency: " + latency;

                            String packetDelayVariation = qos.getPacketDelayVariation();
                            if(packetDelayVariation == null)
                                log.debug("null virtual-link-desc df qos packet-delay-variation, " +
                                        "not inserted in value field.");
                            else {
                                if(!value.isEmpty())
                                    value = value + ", ";

                                value = value + "qos -> packet-delay-variation: " + packetDelayVariation;
                            }

                            String priority = qos.getPriority();
                            if(priority == null)
                                log.debug("null virtual-link-desc df qos priority, not inserted in value field.");
                            else {
                                if(!value.isEmpty())
                                    value = value + ", ";

                                value = value + "qos -> priority: " + priority;
                            }

                            Double packetLossRatio = qos.getPacketLossRatio();
                            if(packetLossRatio == null)
                                log.debug("null virtual-link-desc df qos packet-loss-ratio, " +
                                        "not inserted in value field.");
                            else {
                                if(!value.isEmpty())
                                    value = value + ", ";

                                value = value + "qos -> packet-loss-ratio: " + packetLossRatio;
                            }
                        }

                        ServiceSpecCharacteristicValue dfSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("df " + virtualLinkDescDfId).value(value));
                        sscv.add(dfSscv);
                    }
                }

                sscVld.setServiceSpecCharacteristicValue(sscv);

                serviceSpecCharacteristics.add(sscVld);
            }
        }

        List<NsdDf> dfs = nsd.getDf();
        if(dfs == null)
            log.debug("null df list, skipping characteristics.");
        else {
            for(NsdDf df : dfs) {

                String dfId = df.getId();
                if(dfId == null) {
                    log.debug("null df item id, skipping value.");
                    continue;
                }

                ServiceSpecCharacteristic sscDf = new ServiceSpecCharacteristic()
                        .description("df " + dfId)
                        .name(dfId);

                List<ServiceSpecCharacteristicValue> sscv = new ArrayList<>();

                List<VnfProfileItem> vnfProfile = df.getVnfProfile();
                if(vnfProfile == null)
                    log.debug("null vnf-profile list, skipping values.");
                else {
                    for(VnfProfileItem vnfProfileItem : vnfProfile) {

                        String vnfProfileItemId = vnfProfileItem.getId();
                        if(vnfProfileItemId == null) {
                            log.debug("null vnf-profile item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String vnfdId = vnfProfileItem.getVnfdId();
                        if(vnfdId == null)
                            log.debug("null vnf-profile vnfd-id, not inserted in value field.");
                        else
                            value = "vnfd-id: " + vnfdId;

                        String flavourId = vnfProfileItem.getFlavourId();
                        if(flavourId == null)
                            log.debug("null vnf-profile flavour-id, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "flavour-id: " + flavourId;
                        }

                        String instantiationLevel = vnfProfileItem.getInstantiationLevel();
                        if(instantiationLevel == null)
                            log.debug("null vnf-profile instantiation-level, not inserted in value field");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "instantiation-level: " + instantiationLevel;
                        }

                        String minNumberOfInstances = vnfProfileItem.getMinNumberOfInstances();
                        if(minNumberOfInstances == null)
                            log.debug("null vnf-profile min-number-of-instances, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "min-number-of-instances: " + minNumberOfInstances;
                        }

                        String maxNumberOfInstances = vnfProfileItem.getMaxNumberOfInstances();
                        if(maxNumberOfInstances == null)
                            log.debug("null vnf-profile max-number-of-instances");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "max-number-of-instances: " + maxNumberOfInstances;
                        }

                        List<VirtualLinkConnectivitySchema> virtualLinkConnectivities =
                                vnfProfileItem.getVirtualLinkConnectivity();
                        if(virtualLinkConnectivities == null)
                            log.debug("null vnf-profile virtual-link-connectivity list, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(VirtualLinkConnectivitySchema virtualLinkConnectivity : virtualLinkConnectivities) {

                                String tmp = "";

                                String virtualLinkProfileId = virtualLinkConnectivity.getVirtualLinkProfileId();
                                if(virtualLinkProfileId == null)
                                    log.debug("null vnf-profile virtual-link-connectivity " +
                                            "virtual-link-profile-id, not inserted in value field.");
                                else
                                    tmp = "virtual-link-profile-id: " + virtualLinkProfileId;

                                List<NsdConstituentcpdid2> constituentCpdIds =
                                        virtualLinkConnectivity.getConstituentCpdId();
                                if(constituentCpdIds == null)
                                    log.debug("null vnf-profile virtual-link-connectivity " +
                                            "constituent-cpd-id, not inserted in value field.");
                                else {
                                    List<String> valueLst2 = new ArrayList<>();
                                    for(NsdConstituentcpdid2 constituentCpdId : constituentCpdIds) {

                                        String tmp2 = "";

                                        String constituentBaseElementId =
                                                constituentCpdId.getConstituentBaseElementId();
                                        if(constituentBaseElementId == null)
                                            log.debug("null vnf-profile virtual-link-connectivity " +
                                                    "constituent-cpd-id constituent-base-element-id, " +
                                                    "not inserted in value field.");
                                        else
                                            tmp2 = "constituent-base-element-id: " + constituentBaseElementId;

                                        String constituentCpdIdStr = constituentCpdId.getConstituentCpdId();
                                        if(constituentCpdIdStr == null)
                                            log.debug("null vnf-profile virtual-link-connectivity " +
                                                    "constituent-cpd-id constituent-cpd-id, " +
                                                    "not inserted in value field.");
                                        else {
                                            if(!tmp2.isEmpty())
                                                tmp2 = tmp2 + ", ";

                                            tmp2 = tmp2 + "constituent-cpd-id: " + constituentCpdIdStr;
                                        }

                                        valueLst2.add("( " + tmp2 + " )");
                                    }

                                    if(!tmp.isEmpty())
                                        tmp = tmp + ", ";

                                    tmp = tmp + "constituent-cpd-id: " + valueLst2.toString();
                                }

                                valueLst.add("( " + tmp + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "virtual-link-connectivity: " + valueLst.toString();
                        }

                        ServiceSpecCharacteristicValue vpiSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("vnf-profile " + vnfProfileItemId).value(value));
                        sscv.add(vpiSscv);
                    }
                }

                List<NsdPnfprofile> pnfProfile = df.getPnfProfile();
                if(pnfProfile == null)
                    log.debug("null pnf-profile list, skipping values.");
                else {
                    for(NsdPnfprofile nsdPnfprofile : pnfProfile) {

                        String nsdPnfprofileId = nsdPnfprofile.getId();
                        if(nsdPnfprofileId == null) {
                            log.debug("null pnf-profile item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String pnfdId = nsdPnfprofile.getPnfdId();
                        if(pnfdId == null)
                            log.debug("null pnf-profile pnfd-id, not inserted in value field.");
                        else
                            value = value + "pnfd-id: " + pnfdId;

                        List<VirtualLinkConnectivitySchema> virtualLinkConnectivity =
                                nsdPnfprofile.getVirtualLinkConnectivity();
                        if(virtualLinkConnectivity == null)
                            log.debug("null pnf-profile virtual-link-connectivity, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(VirtualLinkConnectivitySchema virtualLinkConnectivitySchema : virtualLinkConnectivity) {

                                String tmp = "";

                                String virtualLinkProfileId = virtualLinkConnectivitySchema.getVirtualLinkProfileId();
                                if(virtualLinkProfileId == null)
                                    log.debug("null pnf-profile virtual-link-connectivity virtual-link-profile-id, " +
                                            "not inserted in value field.");
                                else
                                    tmp = tmp + "virtual-link-profile-id: " +virtualLinkProfileId;

                                List<NsdConstituentcpdid2> constituentCpdId =
                                        virtualLinkConnectivitySchema.getConstituentCpdId();
                                if(constituentCpdId == null)
                                    log.debug("null pnf-profile virtual-link-connectivity constituent-cpd-id, " +
                                            "not inserted in value field.");
                                else {
                                    List<String> valueLst2 = new ArrayList<>();
                                    for(NsdConstituentcpdid2 nsdConstituentcpdid2 : constituentCpdId) {

                                        String tmp2 = "";

                                        String constituentBaseElementId =
                                                nsdConstituentcpdid2.getConstituentBaseElementId();
                                        if(constituentBaseElementId == null)
                                            log.debug("null pnf-profile virtual-link-connectivity constituent-cpd-id " +
                                                    "constituent-base-element-id, not inserted in value field.");
                                        else
                                            tmp2 = tmp2 + "constituent-base-element-id: " + constituentBaseElementId;

                                        String constituentCpdId1 = nsdConstituentcpdid2.getConstituentCpdId();
                                        if(constituentCpdId1 == null)
                                            log.debug("null pnf-profile virtual-link-connectivity constituent-cpd-id " +
                                                    "constituent-cpd-id, not inserted in value field.");
                                        else {
                                            if(!tmp2.isEmpty())
                                                tmp2 = tmp2 + ", ";

                                            tmp2 = tmp2 + "constituent-cpd-id: " + constituentCpdId1;
                                        }

                                        valueLst2.add("( " + tmp2 + " )");
                                    }

                                    if(!tmp.isEmpty())
                                        tmp = tmp + ", ";

                                    tmp = tmp + "constituent-cpd-id: " + valueLst2.toString();
                                }

                                valueLst.add("( " + tmp + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "virtual-link-connectivity: " + valueLst.toString();
                        }

                        ServiceSpecCharacteristicValue nppSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("pnf-profile " + nsdPnfprofileId).value(value));
                        sscv.add(nppSscv);
                    }
                }

                List<NsdNsprofile> nsProfile = df.getNsProfile();
                if(nsProfile == null)
                    log.debug("null ns-profile list, skipping values.");
                else {
                    for (NsdNsprofile nsdNsprofile : nsProfile) {

                        String nsdNsprofileId = nsdNsprofile.getId();
                        if(nsdNsprofileId == null) {
                            log.debug("null ns-profile item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String nsdNsProfileNsdId = nsdNsprofile.getNsdId();
                        if(nsdNsProfileNsdId == null)
                            log.debug("null ns-profile nsd-id, not inserted in value field.");
                        else
                            value = "nsd-id: " + nsdNsProfileNsdId;

                        String instantiationLevelId = nsdNsprofile.getInstantiationLevelId();
                        if(instantiationLevelId == null)
                            log.debug("null ns-profile instantiation-level-id, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "instantiation-level-id: " + instantiationLevelId;
                        }

                        String minNumberOfInstances = nsdNsprofile.getMinNumberOfInstances();
                        if(minNumberOfInstances == null)
                            log.debug("null ns-profile min-number-of-instances, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "min-number-of-instances: " + minNumberOfInstances;
                        }

                        String maxNumberOfInstances = nsdNsprofile.getMaxNumberOfInstances();
                        if(maxNumberOfInstances == null)
                            log.debug("null ns-profile max-number-of-instances, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "max-number-of-instances: " + maxNumberOfInstances;
                        }

                        String nsDfId = nsdNsprofile.getNsDfId();
                        if(nsDfId == null)
                            log.debug("null ns-profile ns-df-id, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "ns-df-id: " + nsDfId;
                        }

                        List<VirtualLinkConnectivitySchema> virtualLinkConnectivity =
                                nsdNsprofile.getVirtualLinkConnectivity();
                        if(virtualLinkConnectivity == null)
                            log.debug("null ns-profile virtual-link-connectivity, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(VirtualLinkConnectivitySchema virtualLinkConnectivitySchema : virtualLinkConnectivity) {

                                String tmp = "";

                                String virtualLinkProfileId = virtualLinkConnectivitySchema.getVirtualLinkProfileId();
                                if(virtualLinkProfileId == null)
                                    log.debug("null ns-profile virtual-link-connectivity virtual-link-profile-id, " +
                                            "not inserted in value field.");
                                else
                                    tmp = tmp + "virtual-link-profile-id: " + virtualLinkProfileId;

                                List<NsdConstituentcpdid2> constituentCpdId =
                                        virtualLinkConnectivitySchema.getConstituentCpdId();
                                if(constituentCpdId == null)
                                    log.debug("null ns-profile virtual-link-connectivity constituent-cpd-id, " +
                                            "not inserted in value field.");
                                else {
                                    List<String> valueLst2 = new ArrayList<>();
                                    for(NsdConstituentcpdid2 nsdConstituentcpdid2 : constituentCpdId) {

                                        String tmp2 = "";

                                        String constituentBaseElementId =
                                                nsdConstituentcpdid2.getConstituentBaseElementId();
                                        if(constituentBaseElementId == null)
                                            log.debug("null ns-profile virtual-link-connectivity constituent-cpd-id " +
                                                    "constituent-base-element-id, not inserted in value field.");
                                        else
                                            tmp2 = tmp2 + "constituent-base-element-id: " + constituentBaseElementId;

                                        String constituentCpdId1 = nsdConstituentcpdid2.getConstituentCpdId();
                                        if(constituentCpdId1 == null)
                                            log.debug("null ns-profile virtual-link-connectivity constituent-cpd-id " +
                                                    "constituent-cpd-id, not inserted in value field.");
                                        else {
                                            if(!tmp2.isEmpty())
                                                tmp2 = tmp2 + ", ";

                                            tmp2 = tmp2 + "constituent-cpd-id: " + constituentCpdId1;
                                        }

                                        valueLst2.add("( " + tmp2 + " )");
                                    }

                                    if(!tmp.isEmpty())
                                        tmp = tmp + ", ";

                                    tmp = tmp + "constituent-cpd-id: " + valueLst2.toString();
                                }

                                valueLst.add("( " + tmp + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "virtual-link-connectivity: " + valueLst.toString();
                        }

                        ServiceSpecCharacteristicValue nspSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("ns-profile " + nsdNsprofileId).value(value));
                        sscv.add(nspSscv);
                    }
                }

                List<VirtualLinkProfileItem> virtualLinkProfile = df.getVirtualLinkProfile();
                if(virtualLinkProfile == null)
                    log.debug("null virtual-link-profile list, skipping values.");
                else {
                    for(VirtualLinkProfileItem virtualLinkProfileItem : virtualLinkProfile) {

                        String virtualLinkProfileItemId = virtualLinkProfileItem.getId();
                        if(virtualLinkProfileItemId == null) {
                            log.debug("null virtual-link-profile item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String virtualLinkDescId = virtualLinkProfileItem.getVirtualLinkDescId();
                        if(virtualLinkDescId == null)
                            log.debug("null virtual-link-profile virtual-link-desc-id, not inserted in value field.");
                        else
                            value = value + "virtual-link-desc-id: " + virtualLinkDescId;

                        String flavourId = virtualLinkProfileItem.getFlavourId();
                        if(flavourId == null)
                            log.debug("null virtual-link-profile flavour-id, not inserted in value field.");
                        else {
                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "flavour-id: " + flavourId;
                        }

                        LinkBitrateRequirements maxBitrateRequirements =
                                virtualLinkProfileItem.getMaxBitrateRequirements();
                        if(maxBitrateRequirements == null)
                            log.debug("null virtual-link-profile max-bitrate-requirements, not inserted in value field.");
                        else {
                            String root = maxBitrateRequirements.getRoot();
                            if(root == null)
                                log.debug("null virtual-link-profile max-bitrate-requirements root, " +
                                        "not inserted in value field.");
                            else {
                                if(!value.isEmpty())
                                    value = value + ", ";

                                value = value + "max-bitrate-requirements -> root: " + root;
                            }
                        }

                        LinkBitrateRequirements minBitrateRequirements =
                                virtualLinkProfileItem.getMinBitrateRequirements();
                        if(minBitrateRequirements == null)
                            log.debug("null virtual-link-profile min-bitrate-requirements, not inserted in value field.");
                        else {
                            String root = minBitrateRequirements.getRoot();
                            if(root == null)
                                log.debug("null virtual-link-profile min-bitrate-requirements root, " +
                                        "not inserted in value field.");
                            else {
                                if(!value.isEmpty())
                                    value = value + ", ";

                                value = value + "min-bitrate-requirements -> root: " + root;
                            }
                        }

                        ServiceSpecCharacteristicValue vlpiSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("virtual-link-profile " + virtualLinkProfileItemId).value(value));
                        sscv.add(vlpiSscv);
                    }
                }

                List<NsdNsinstantiationlevel> nsInstantiationlevels = df.getNsInstantiationLevel();
                if(nsInstantiationlevels == null)
                    log.debug("null ns-instantiation-level list, skipping values.");
                else {
                    for(NsdNsinstantiationlevel nsInstantiationlevel : nsInstantiationlevels) {

                        String nsInstantiationlevelId = nsInstantiationlevel.getId();
                        if(nsInstantiationlevelId == null) {
                            log.debug("null ns-instantiation-level item id, skipping value.");
                            continue;
                        }

                        String value = "";

                        String description = nsInstantiationlevel.getDescription();
                        if(description == null)
                            log.debug("null ns-instantiation-level description, not inserted in value field.");
                        else
                            value = value + "description: " + description;

                        List<NsdVnftolevelmapping> vnfToLevelMapping = nsInstantiationlevel.getVnfToLevelMapping();
                        if(vnfToLevelMapping == null)
                            log.debug("null ns-instantiation-level vnf-to-level-mapping, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(NsdVnftolevelmapping vnftolevelmappingItem : vnfToLevelMapping) {
                                String tmp = "";

                                String vnfProfileId = vnftolevelmappingItem.getVnfProfileId();
                                if(vnfProfileId == null)
                                    log.debug("null ns-instantiation-level vnf-to-level-mapping vnf-profile-id, " +
                                            "not inserted in value field.");
                                else
                                    tmp = tmp + "vnf-profile-id: " + vnfProfileId;

                                String numberOfInstances = vnftolevelmappingItem.getNumberOfInstances();
                                if(numberOfInstances == null)
                                    log.debug("null ns-instantiation-level vnf-to-level-mapping number-of-instances, " +
                                            "not inserted in value field.");
                                else {
                                    if(!tmp.isEmpty())
                                        tmp = tmp + ", ";

                                    tmp = tmp + "number-of-instances: " + numberOfInstances;
                                }

                                valueLst.add("( " + tmp + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "vnf-to-level-mapping: " + valueLst.toString();
                        }

                        List<NsdNstolevelmapping> nsdNstolevelmappings = nsInstantiationlevel.getNsToLevelMapping();
                        if(nsdNstolevelmappings == null)
                            log.debug("null ns-instantiation-level ns-to-level-mapping, not inserted in value field.");
                        else {
                            List<String> valueLst = new ArrayList<>();
                            for(NsdNstolevelmapping nsdNstolevelmapping : nsdNstolevelmappings) {
                                String tmp = "";

                                String nsProfileId = nsdNstolevelmapping.getNsProfileId();
                                if(nsProfileId == null)
                                    log.debug("null ns-instantiation-level ns-to-level-mapping ns-profile-id, " +
                                            "not inserted in value field.");
                                else
                                    tmp = tmp + "ns-profile-id: " + nsProfileId;

                                String numberOfInstances = nsdNstolevelmapping.getNumberOfInstances();
                                if(numberOfInstances == null)
                                    log.debug("null ns-instantiation-level ns-to-level-mapping number-of-instances, " +
                                            "not inserted in value field.");
                                else {
                                    if(!tmp.isEmpty())
                                        tmp = tmp + ", ";

                                    tmp = tmp + "number-of-instances: " + numberOfInstances;
                                }

                                valueLst.add("( " + tmp + " )");
                            }

                            if(!value.isEmpty())
                                value = value + ", ";

                            value = value + "ns-to-level-mapping: " + valueLst.toString();
                        }

                        ServiceSpecCharacteristicValue nilSscv = new ServiceSpecCharacteristicValue()
                                .value(new Any().alias("ns-instantiation-level " + nsInstantiationlevelId).value(value));
                        sscv.add(nilSscv);
                    }
                }

                sscDf.setServiceSpecCharacteristicValue(sscv);

                serviceSpecCharacteristics.add(sscDf);
            }
        }

        ssc.setServiceSpecCharacteristic(serviceSpecCharacteristics);

        return ssc;
    }

    public ServiceCandidateCreate buildNsdServiceCandidate(String name, Pair<String, String> pair, ServiceSpecification ss) {
        return new ServiceCandidateCreate()
                .name(name)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .category(Collections.singletonList(new ServiceCategoryRef()
                        .name(Kind.NS.name())
                        .href(pair.getFirst())
                        .id(pair.getSecond())))
                .serviceSpecification(new ServiceSpecificationRef()
                        .id(ss.getId())
                        .href(ss.getHref()));
    }
}
