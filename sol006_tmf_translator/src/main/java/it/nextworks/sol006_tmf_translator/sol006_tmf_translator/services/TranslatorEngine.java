package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import it.nextworks.nfvmano.libs.common.enums.*;
import it.nextworks.nfvmano.libs.descriptors.sol006.*;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.information_models.persistence.IdVsbNameMapping;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MalformattedElementException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.tmf_offering_catalog.information_models.common.*;
import it.nextworks.tmf_offering_catalog.information_models.resource.*;
import it.nextworks.tmf_offering_catalog.information_models.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final IdVsbNameMappingService idVsbNameMappingService;

    @Autowired
    public TranslatorEngine(IdVsbNameMappingService idVsbNameMappingService) {
        this.idVsbNameMappingService = idVsbNameMappingService;
    }

    private Pair<Integer, Integer> getMinMaxInt(ArrayList<Integer> arr) {
        Integer min = arr.get(0);
        Integer max = arr.get(0);
        for(int i = 1; i < arr.size(); i++) {
            Integer curr = arr.get(i);
            if(curr < min)
                min = curr;
            if(curr > max)
                max = curr;
        }

        return new Pair<>(min, max);
    }

    private Pair<Double, Double> getMinMaxDou(ArrayList<Double> arr) {
        Double min = arr.get(0);
        Double max = arr.get(0);
        for(int i = 1; i < arr.size(); i++) {
            Double curr = arr.get(i);
            if(curr < min)
                min = curr;
            if(curr > max)
                max = curr;
        }

        return new Pair<>(min, max);
    }

    private List<ResourceSpecCharacteristic> computeVnfRequirements(Vnfd vnfd) throws MalformattedElementException {

        List<VnfdDf> vnfdDfs = vnfd.getDf();
        if(vnfdDfs == null)
            throw new MalformattedElementException("Cannot infer vnf requirements due to missing deployment flavor list.");

        List<VnfdVdu> vnfdVdus = vnfd.getVdu();
        if(vnfdVdus == null)
            throw new MalformattedElementException("Cannot infer vnf requirements due to missing vdu list.");

        List<VnfdVirtualcomputedesc> vnfdVirtualcomputedescs = vnfd.getVirtualComputeDesc();
        if(vnfdVirtualcomputedescs == null)
            throw new MalformattedElementException("Cannot infer vnf requirements due to missing virtual compute desc list.");

        List<VnfdVirtualstoragedesc> vnfdVirtualstoragedescs = vnfd.getVirtualStorageDesc();
        if(vnfdVirtualstoragedescs == null)
            throw new MalformattedElementException("Cannot infer vnf requirements due to missing virtual storage desc list.");

        ArrayList<Integer> cpus = new ArrayList<>();
        ArrayList<Double> memories = new ArrayList<>();
        ArrayList<Integer> storages = new ArrayList<>();

        for(VnfdDf vnfdDf : vnfdDfs) {
            List<VnfdInstantiationlevel> vnfdInstantiationlevels = vnfdDf.getInstantiationLevel();
            if(vnfdInstantiationlevels == null)
                throw new MalformattedElementException("Cannot infer vnf requirements due to empty instantiation level list");

            for(VnfdInstantiationlevel vnfdInstantiationlevel : vnfdInstantiationlevels) {
                List<VnfdVdulevel> vdulevels = vnfdInstantiationlevel.getVduLevel();
                if(vdulevels == null)
                   throw new MalformattedElementException("Cannot infer vnf requirements due to empty vdu level list");

                int cpu = 0;
                double memory = 0.0;
                int storage = 0;

                for(VnfdVdulevel vdulevel : vdulevels) {
                   String vduId = vdulevel.getVduId();
                   if(vduId == null)
                       throw new MalformattedElementException("Cannot infer vnf requirements due to missing id for vdu level");

                   List<VnfdVdu> vdus = vnfdVdus.stream()
                           .filter(vdu -> vdu.getId().equals(vduId))
                           .collect(Collectors.toList());
                   if(vdus.size() != 1)
                       throw new MalformattedElementException("Cannot infer vnf requirements due to missing/multiple vdu for id " + vduId);
                   VnfdVdu vdu = vdus.get(0);

                   String virtualComputeDescId = vdu.getVirtualComputeDesc();
                   if(virtualComputeDescId != null) {
                       List<VnfdVirtualcomputedesc> virtualcomputedescs = vnfdVirtualcomputedescs.stream()
                               .filter(vnfdVirtualcomputedesc -> vnfdVirtualcomputedesc.getId().equals(virtualComputeDescId))
                               .collect(Collectors.toList());
                       if(virtualcomputedescs.size() != 1)
                           throw new MalformattedElementException("Cannot infer vnf requirements due to missing/multiple virtual compute desc");
                       VnfdVirtualcomputedesc virtualcomputedesc = virtualcomputedescs.get(0);

                       VnfdVirtualcpu vnfdVirtualcpu = virtualcomputedesc.getVirtualCpu();
                       if(vnfdVirtualcpu != null) {
                           String numVirtualCpu = vnfdVirtualcpu.getNumVirtualCpu();
                           if(numVirtualCpu != null)
                               cpu += Integer.parseInt(numVirtualCpu);
                       }

                       VnfdVirtualmemory vnfdVirtualmemory = virtualcomputedesc.getVirtualMemory();
                       if(vnfdVirtualmemory != null) {
                           Double size = vnfdVirtualmemory.getSize();
                           if(size != null)
                               memory += size;
                       }
                   }

                   List<String> virtualStorageDescIds = vdu.getVirtualStorageDesc();
                   if(virtualStorageDescIds != null) {
                       for(String virtualStorageDescId : virtualStorageDescIds) {
                           List<VnfdVirtualstoragedesc> virtualstoragedescs = vnfdVirtualstoragedescs.stream()
                                   .filter(virtualStorageDesc -> virtualStorageDesc.getId().equals(virtualStorageDescId))
                                   .collect(Collectors.toList());
                           if(virtualstoragedescs.size() != 1)
                               throw new MalformattedElementException("Cannot infer vnf requirements due to missing/multiple virtual storage");
                           VnfdVirtualstoragedesc virtualstoragedesc = virtualstoragedescs.get(0);

                           String size = virtualstoragedesc.getSizeOfStorage();
                           if(size != null)
                               storage += Integer.parseInt(size);
                       }
                   }
                }

                cpus.add(cpu);
                memories.add(memory);
                storages.add(storage);
            }
        }

        List<ResourceSpecCharacteristic> resourceSpecCharacteristics = new ArrayList<>();

        ResourceSpecCharacteristic cpuRequirements =
                new ResourceSpecCharacteristic()
                        .name("vCPU Requirements")
                        .description("vCPU lower bound and upper bound.");
        List<ResourceSpecCharacteristicValue> cpuRscvs = new ArrayList<>();
        Pair<Integer, Integer> minMaxCpu = getMinMaxInt(cpus);
        cpuRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("min-vCPU").value(minMaxCpu.getFirst().toString())));
        cpuRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("max-vCPU").value(minMaxCpu.getSecond().toString())));
        cpuRequirements.setResourceSpecCharacteristicValue(cpuRscvs);
        resourceSpecCharacteristics.add(cpuRequirements);

        ResourceSpecCharacteristic memoryRequirements =
                new ResourceSpecCharacteristic()
                        .name("Virtual Memory Requirements")
                        .description("Virtual Memory lower bound and upper bound.");
        List<ResourceSpecCharacteristicValue> memoryRscvs = new ArrayList<>();
        Pair<Double, Double> minMaxMemory = getMinMaxDou(memories);
        memoryRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("min-virtual-memory").value(minMaxMemory.getFirst().toString())).unitOfMeasure("GB"));
        memoryRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("max-virtual-memory").value(minMaxMemory.getSecond().toString())).unitOfMeasure("GB"));
        memoryRequirements.setResourceSpecCharacteristicValue(memoryRscvs);
        resourceSpecCharacteristics.add(memoryRequirements);

        ResourceSpecCharacteristic storageRequirements =
                new ResourceSpecCharacteristic()
                        .name("Storage Requirements")
                        .description("Storage lower bound and upper bound.");
        List<ResourceSpecCharacteristicValue> storageRscvs = new ArrayList<>();
        Pair<Integer, Integer> minMaxStorage = getMinMaxInt(storages);
        storageRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("min-storage").value(minMaxStorage.getFirst().toString())).unitOfMeasure("GB"));
        storageRscvs.add(new ResourceSpecCharacteristicValue()
                .value(new Any().alias("max-storage").value(minMaxStorage.getSecond().toString())).unitOfMeasure("GB"));
        storageRequirements.setResourceSpecCharacteristicValue(storageRscvs);
        resourceSpecCharacteristics.add(storageRequirements);

        return resourceSpecCharacteristics;
    }

    public ResourceSpecificationCreate buildVnfdResourceSpecification(Vnfd vnfd, String functionType)
            throws MalformattedElementException, NotExistingEntityException {

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
                .description("ID of the VNF descriptor.")
                .name("vnfdId")
                .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                        .value(new Any().alias("vnfdId").value(vnfdId))));
        resourceSpecCharacteristics.add(rscVnfdId);

        IdVsbNameMapping idVsbNameMapping = idVsbNameMappingService.getById(vnfdId);
        ResourceSpecCharacteristic rscVsbName = new ResourceSpecCharacteristic()
                .description("Name of the Vertical Service Blueprint.")
                .name("vsbName")
                .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                        .value(new Any().alias("vsbName").value(idVsbNameMapping.getVsbName()))));
        resourceSpecCharacteristics.add(rscVsbName);

        resourceSpecCharacteristics.addAll(computeVnfRequirements(vnfd));

        if(functionType != null) {
            resourceSpecCharacteristics.add(new ResourceSpecCharacteristic()
                    .description("Network function managed by this VNF.")
                    .name("Function Type")
                    .resourceSpecCharacteristicValue(Collections.singletonList(new ResourceSpecCharacteristicValue()
                            .value(new Any().alias("functionType").value(functionType)))));
        }

        List<ExtCpd> extCpds = vnfd.getExtCpd();
        ResourceSpecCharacteristic nExtCpd = new ResourceSpecCharacteristic()
                .description("Number of external connection points.")
                .name("nExtCpd");
        List<ResourceSpecCharacteristicValue> nExtCpdValueLst = new ArrayList<>();
        ResourceSpecCharacteristicValue nExtCpdValue;
        if(extCpds == null) {
            nExtCpdValue = new ResourceSpecCharacteristicValue()
                    .value(new Any().alias("number of external connection points").value("0"));

        } else {
            nExtCpdValue = new ResourceSpecCharacteristicValue()
                    .value(new Any().alias("number of external connection points").value(String.valueOf(extCpds.size())));

        }
        nExtCpdValueLst.add(nExtCpdValue);
        nExtCpd.setResourceSpecCharacteristicValue(nExtCpdValueLst);
        resourceSpecCharacteristics.add(nExtCpd);

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
                        .href(rs.getHref())
                        .name(rs.getName()));
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
                        .href(rs.getHref())
                        .name(rs.getName()));
    }

    private List<ServiceSpecCharacteristic> computeNsRequirements(List<ResourceSpecification> vnfResourceSpecifications,
                                                                  List<ServiceSpecification> nsServiceSpecifications)
            throws MalformattedElementException {

        int minCpu = 0;
        int maxCpu = 0;
        double minMemory = 0.0;
        double maxMemory = 0.0;
        int minStorage = 0;
        int maxStorage = 0;

        for(ResourceSpecification rs : vnfResourceSpecifications) {
            String rsId = rs.getId();
            List<ResourceSpecCharacteristic> resourceSpecCharacteristics = rs.getResourceSpecCharacteristic();
            if(resourceSpecCharacteristics == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty characteristic list " +
                        "for resource specification " + rsId);

            List<ResourceSpecCharacteristic> requirements = resourceSpecCharacteristics.stream()
                    .filter(rsc -> rsc.getName().equals("vCPU Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple vCPU requirements " +
                        "for resource specification " + rsId);
            List<ResourceSpecCharacteristicValue> rscvs = requirements.get(0).getResourceSpecCharacteristicValue();
            if(rscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty vCPU characteristic value list " +
                        "for resource specification " + rsId);
            List<ResourceSpecCharacteristicValue> minCpuRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("min-vCPU"))
                    .collect(Collectors.toList());
            if(minCpuRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-vCPU requirements " +
                        "for resource specification " + rsId);
            minCpu += Integer.parseInt(minCpuRscv.get(0).getValue().getValue());
            List<ResourceSpecCharacteristicValue> maxCpuRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("max-vCPU"))
                    .collect(Collectors.toList());
            if(maxCpuRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-vCPU requirement " +
                        "for resource specification " + rsId);
            maxCpu += Integer.parseInt(maxCpuRscv.get(0).getValue().getValue());

            requirements = resourceSpecCharacteristics.stream()
                    .filter(rsc -> rsc.getName().equals("Virtual Memory Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple Virtual Memory Requirements " +
                        "for resource specification " + rsId);
            rscvs = requirements.get(0).getResourceSpecCharacteristicValue();
            if(rscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty Virtual Memory characteristic value list " +
                        "for resource specification " + rsId);
            List<ResourceSpecCharacteristicValue> minMemoryRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("min-virtual-memory"))
                    .collect(Collectors.toList());
            if(minMemoryRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-virtual-memory requirement " +
                        "for resource specification " + rsId);
            minMemory += Double.parseDouble(minMemoryRscv.get(0).getValue().getValue());
            List<ResourceSpecCharacteristicValue> maxMemoryRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("max-virtual-memory"))
                    .collect(Collectors.toList());
            if(maxMemoryRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-virtual-memory requirement " +
                        "for resource specification " + rsId);
            maxMemory += Double.parseDouble(maxMemoryRscv.get(0).getValue().getValue());

            requirements = resourceSpecCharacteristics.stream()
                    .filter(rsc -> rsc.getName().equals("Storage Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple Storage Requirements " +
                        "for resource specification " + rsId);
            rscvs = requirements.get(0).getResourceSpecCharacteristicValue();
            if(rscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty Storage characteristic value list " +
                        "for resource specification " + rsId);
            List<ResourceSpecCharacteristicValue> minStorageRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("min-storage"))
                    .collect(Collectors.toList());
            if(minStorageRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-storage requirement " +
                        "for resource specification " + rsId);
            minStorage += Integer.parseInt(minStorageRscv.get(0).getValue().getValue());
            List<ResourceSpecCharacteristicValue> maxStorageRscv = rscvs.stream()
                    .filter(rscv -> rscv.getValue().getAlias().equals("max-storage"))
                    .collect(Collectors.toList());
            if(maxStorageRscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-storage requirement " +
                        "for resource specification " + rsId);
            maxStorage += Integer.parseInt(maxStorageRscv.get(0).getValue().getValue());
        }

        for(ServiceSpecification ss : nsServiceSpecifications) {
            String ssId = ss.getId();
            List<ServiceSpecCharacteristic> serviceSpecCharacteristics = ss.getServiceSpecCharacteristic();
            if(serviceSpecCharacteristics == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty characteristic list " +
                        "for service specification " + ssId);

            List<ServiceSpecCharacteristic> requirements = serviceSpecCharacteristics.stream()
                    .filter(ssc -> ssc.getName().equals("vCPU Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple vCPU Requirements " +
                        "for service specification " + ssId);
            List<ServiceSpecCharacteristicValue> sscvs = requirements.get(0).getServiceSpecCharacteristicValue();
            if(sscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty vCPU characteristic value list " +
                        "for service specification " + ssId);
            List<ServiceSpecCharacteristicValue> minCpuSscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("min-vCPU"))
                    .collect(Collectors.toList());
            if(minCpuSscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-vCPU requirements " +
                        "for service specification " + ssId);
            minCpu += Integer.parseInt(minCpuSscv.get(0).getValue().getValue());
            List<ServiceSpecCharacteristicValue> maxCpuSscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("max-vCPU"))
                    .collect(Collectors.toList());
            if(maxCpuSscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-vCPU requirement " +
                        "for resource specification " + ssId);
            maxCpu += Integer.parseInt(maxCpuSscv.get(0).getValue().getValue());

            requirements = serviceSpecCharacteristics.stream()
                    .filter(ssc -> ssc.getName().equals("Virtual Memory Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple Virtual Memory Requirements " +
                        "for service specification " + ssId);
            sscvs = requirements.get(0).getServiceSpecCharacteristicValue();
            if(sscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty Virtual Memory characteristic value list " +
                        "for service specification " + ssId);
            List<ServiceSpecCharacteristicValue> minMemorySscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("min-virtual-memory"))
                    .collect(Collectors.toList());
            if(minMemorySscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-virtual-memory requirements " +
                        "for service specification " + ssId);
            minMemory += Double.parseDouble(minMemorySscv.get(0).getValue().getValue());
            List<ServiceSpecCharacteristicValue> maxMemorySscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("max-virtual-memory"))
                    .collect(Collectors.toList());
            if(maxMemorySscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-virtual-memory requirements " +
                        "for service specification " + ssId);
            maxMemory += Double.parseDouble(maxMemorySscv.get(0).getValue().getValue());

            requirements = serviceSpecCharacteristics.stream()
                    .filter(ssc -> ssc.getName().equals("Storage Requirements"))
                    .collect(Collectors.toList());
            if(requirements.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple Storage Requirements " +
                        "for service specification " + ssId);
            sscvs = requirements.get(0).getServiceSpecCharacteristicValue();
            if(sscvs == null)
                throw new MalformattedElementException("Cannot infer ns requirements, empty Storage characteristic value list " +
                        "for service specification " + ssId);
            List<ServiceSpecCharacteristicValue> minStorageSscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("min-storage"))
                    .collect(Collectors.toList());
            if(minMemorySscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple min-storage requirements " +
                        "for service specification " + ssId);
            minStorage += Integer.parseInt(minStorageSscv.get(0).getValue().getValue());
            List<ServiceSpecCharacteristicValue> maxStorageSscv = sscvs.stream()
                    .filter(sscv -> sscv.getValue().getAlias().equals("max-storage"))
                    .collect(Collectors.toList());
            if(maxMemorySscv.size() != 1)
                throw new MalformattedElementException("Cannot infer ns requirements, missing/multiple max-storage requirements " +
                        "for service specification " + ssId);
            maxStorage += Integer.parseInt(maxStorageSscv.get(0).getValue().getValue());
        }

        List<ServiceSpecCharacteristic> serviceSpecCharacteristics = new ArrayList<>();

        ServiceSpecCharacteristic cpuRequirements =
                new ServiceSpecCharacteristic()
                        .name("vCPU Requirements")
                        .description("vCPU lower bound and upper bound.");
        List<ServiceSpecCharacteristicValue> cpuSscv = new ArrayList<>();
        cpuSscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("min-vCPU").value(String.valueOf(minCpu))));
        cpuSscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("max-vCPU").value(String.valueOf(maxCpu))));
        cpuRequirements.setServiceSpecCharacteristicValue(cpuSscv);
        serviceSpecCharacteristics.add(cpuRequirements);

        ServiceSpecCharacteristic memoryRequirements =
                new ServiceSpecCharacteristic()
                        .name("Virtual Memory Requirements")
                        .description("Virtual Memory lower bound and upper bound.");
        List<ServiceSpecCharacteristicValue> memorySscv = new ArrayList<>();
        memorySscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("min-virtual-memory").value(String.valueOf(minMemory))).unitOfMeasure("GB"));
        memorySscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("max-virtual-memory").value(String.valueOf(maxMemory))).unitOfMeasure("GB"));
        memoryRequirements.setServiceSpecCharacteristicValue(memorySscv);
        serviceSpecCharacteristics.add(memoryRequirements);

        ServiceSpecCharacteristic storageRequirements =
                new ServiceSpecCharacteristic()
                        .name("Storage Requirements")
                        .description("Storage lower bound and upper bound.");
        List<ServiceSpecCharacteristicValue> storageSscv = new ArrayList<>();
        storageSscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("min-storage").value(String.valueOf(minStorage))).unitOfMeasure("GB"));
        storageSscv.add(new ServiceSpecCharacteristicValue()
                .value(new Any().alias("max-storage").value(String.valueOf(maxStorage))).unitOfMeasure("GB"));
        storageRequirements.setServiceSpecCharacteristicValue(storageSscv);
        serviceSpecCharacteristics.add(storageRequirements);

        return serviceSpecCharacteristics;
    }

    public ServiceSpecificationCreate buildNsdServiceSpecification(Nsd nsd,
                                                                   List<ResourceSpecification> vnfResourceSpecifications,
                                                                   List<ResourceSpecification> pnfResourceSpecifications,
                                                                   List<ServiceSpecification> nsServiceSpecifications,
                                                                   String serviceType)
            throws MalformattedElementException, NotExistingEntityException {

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

        for(ResourceSpecification rs : vnfResourceSpecifications) {
            rsRefs.add(new ResourceSpecificationRef()
                    .id(rs.getId())
                    .href(rs.getHref())
                    .name(rs.getName()));
        }

        for(ResourceSpecification rs : pnfResourceSpecifications) {
            rsRefs.add(new ResourceSpecificationRef()
                    .id(rs.getId())
                    .href(rs.getHref())
                    .name(rs.getName()));
        }

        ssc.setResourceSpecification(rsRefs);

        List<ServiceSpecRelationship> ssrRefs = new ArrayList<>();

        for(ServiceSpecification ss : nsServiceSpecifications) {
            ssrRefs.add(new ServiceSpecRelationship()
                    .id(ss.getId())
                    .href(ss.getHref())
                    .name(ss.getName()));
        }

        ssc.setServiceSpecRelationship(ssrRefs);

        ServiceSpecCharacteristic sscNsdId = new ServiceSpecCharacteristic()
                .description("ID of the NS Descriptor")
                .name("nsdId")
                .serviceSpecCharacteristicValue(Collections.singletonList(new ServiceSpecCharacteristicValue()
                        .value(new Any().alias("nsdId").value(nsdId))));
        serviceSpecCharacteristics.add(sscNsdId);

        IdVsbNameMapping idVsbNameMapping = idVsbNameMappingService.getById(nsdId);
        ServiceSpecCharacteristic rscVsbName = new ServiceSpecCharacteristic()
                .description("Name of the Vertical Service Blueprint.")
                .name("vsbName")
                .serviceSpecCharacteristicValue(Collections.singletonList(new ServiceSpecCharacteristicValue()
                        .value(new Any().alias("vsbName").value(idVsbNameMapping.getVsbName()))));
        serviceSpecCharacteristics.add(rscVsbName);

        serviceSpecCharacteristics.addAll(computeNsRequirements(vnfResourceSpecifications, nsServiceSpecifications));

        if(serviceType != null) {
            serviceSpecCharacteristics.add(new ServiceSpecCharacteristic()
                    .description("Network service managed by this NS.")
                    .name("Service Type")
                    .serviceSpecCharacteristicValue(Collections.singletonList(new ServiceSpecCharacteristicValue()
                            .value(new Any().alias("serviceType").value(serviceType)))));
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
                        .href(ss.getHref())
                        .name(ss.getName()));
    }

    public ResourceCandidateCreate buildSpcResourceCandidate(Pair<String, String> pair, ResourceSpecification rs) {

        String name = rs.getName();

        return new ResourceCandidateCreate()
                .name(name)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .category(Collections.singletonList(new ResourceCategoryRef()
                        .name(Kind.SPC.name())
                        .href(pair.getFirst())
                        .id(pair.getSecond())))
                .resourceSpecification(new ResourceSpecificationRef()
                        .id(rs.getId())
                        .href(rs.getHref())
                        .name(name));
    }

    public ResourceCandidateCreate buildRadResourceCandidate(Pair<String, String> pair, ResourceSpecification rs) {

        String name = rs.getName();

        return new ResourceCandidateCreate()
                .name(name)
                .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .category(Collections.singletonList(new ResourceCategoryRef()
                        .name(Kind.RAD.name())
                        .href(pair.getFirst())
                        .id(pair.getSecond())))
                .resourceSpecification(new ResourceSpecificationRef()
                        .id(rs.getId())
                        .href(rs.getHref())
                        .name(name));
    }
}
