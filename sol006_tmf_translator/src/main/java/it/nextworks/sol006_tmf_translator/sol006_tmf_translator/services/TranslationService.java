package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.persistence.MappingInfo;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.config.CustomOffsetDateTimeSerializer;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import it.nextworks.tmf_offering_catalog.information_models.product.*;
import it.nextworks.tmf_offering_catalog.information_models.resource.*;
import it.nextworks.tmf_offering_catalog.information_models.service.*;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private final ObjectMapper objectMapper;

    private final TranslatorEngine translatorEngine;

    private final TranslatorCatalogInteractionService translatorCatalogInteractionService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    private final MappingInfoService mappingInfoService;

    private final ApplicationContext applicationContext;

    @Autowired
    public TranslationService(ObjectMapper objectMapper,
                              TranslatorEngine translatorEngine,
                              TranslatorCatalogInteractionService translatorCatalogInteractionService,
                              TranslatorDescSourceInteractionService translatorDescSourceInteractionService,
                              MappingInfoService mappingInfoService,
                              ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        this.objectMapper.registerModule(module);
        this.applicationContext = applicationContext;

        this.translatorEngine = translatorEngine;
        this.translatorCatalogInteractionService = translatorCatalogInteractionService;
        this.translatorDescSourceInteractionService = translatorDescSourceInteractionService;
        this.mappingInfoService = mappingInfoService;
    }

    @PostConstruct
    public void initializeOfferCatalog() {
        try {
            getCategoryOrCreateIfNotExists(Kind.VNF, "/resourceCatalogManagement/v2/resourceCategory/filter");
            getCategoryOrCreateIfNotExists(Kind.PNF, "/resourceCatalogManagement/v2/resourceCategory/filter");
            getCategoryOrCreateIfNotExists(Kind.NS, "/serviceCatalogManagement/v4/serviceCategory/filter");
            getCategoryOrCreateIfNotExists(Kind.VS, "/serviceCatalogManagement/v4/serviceCategory/filter");
            getCategoryOrCreateIfNotExists(Kind.SPC, "/resourceCatalogManagement/v2/resourceCategory/filter");
            getCategoryOrCreateIfNotExists(Kind.RAD, "/resourceCatalogManagement/v2/resourceCategory/filter");
        } catch (CatalogException | IOException e) {
            log.error(e.getMessage());
            SpringApplication.exit(applicationContext, () -> -1);
        }
    }

    public Pair<String, String> getCategoryOrCreateIfNotExists(Kind kind, String requestPath)
            throws CatalogException, IOException {

        String name = kind.name();
        try {
            HttpEntity en = this.translatorCatalogInteractionService.isCategoryPresent(kind, requestPath);
            switch(kind) {
                case VNF:
                case PNF:
                case SPC:
                case RAD:
                    ResourceCategory rc = objectMapper.readValue(EntityUtils.toString(en), ResourceCategory.class);
                    return new Pair<>(rc.getHref(), rc.getId());

                case NS:
                case VS:
                    ServiceCategory sc = objectMapper.readValue(EntityUtils.toString(en), ServiceCategory.class);
                    return new Pair<>(sc.getHref(), sc.getId());

                default:
                    throw new IllegalArgumentException("Specified kind not VNFD, PNFD or NSD.");
            }
        } catch (MissingEntityOnCatalogException e) {
            log.info("Posting Category " + name + " to Offer Catalog.");

            switch(kind) {
                case VNF:
                case PNF:
                case SPC:
                case RAD:
                    ResourceCategoryCreate rcc = new ResourceCategoryCreate()
                            .name(name)
                            .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
                    HttpEntity rcEntity = this.translatorCatalogInteractionService
                            .post(objectMapper.writeValueAsString(rcc),
                                    "/resourceCatalogManagement/v2/resourceCategory");
                    ResourceCategory rc = objectMapper.readValue(EntityUtils.toString(rcEntity), ResourceCategory.class);

                    log.info("Category " + name + " posted to Offer Catalog.");
                    return new Pair<>(rc.getHref(), rc.getId());

                case NS:
                case VS:
                    ServiceCategoryCreate scc = new ServiceCategoryCreate()
                            .name(name)
                            .lastUpdate(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
                    HttpEntity scEntity = this.translatorCatalogInteractionService
                            .post(objectMapper.writeValueAsString(scc),
                                    "/serviceCatalogManagement/v4/serviceCategory");
                    ServiceCategory sc = objectMapper.readValue(EntityUtils.toString(scEntity), ServiceCategory.class);

                    log.info("Category " + name + " posted to Offer Catalog.");
                    return new Pair<>(sc.getHref(), sc.getId());

                default:
                    throw new IllegalArgumentException("Specified kind not VNFD, PNFD or NSD.");
            }
        }
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateAndPostVnfd(Vnfd vnfd, String functionType)
            throws IOException, CatalogException, MalformattedElementException {

        String vnfdId = vnfd.getId();
        ResourceSpecificationCreate rsc = translatorEngine.buildVnfdResourceSpecification(vnfd, functionType);

        log.info("Posting Resource Specification to Offer Catalog for vnfd " + vnfdId + ".");

        String rscJson = objectMapper.writeValueAsString(rsc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(rscJson, "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs =
                objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        Pair<String, String> pair = getCategoryOrCreateIfNotExists(Kind.VNF,
                "/resourceCatalogManagement/v2/resourceCategory/filter");

        ResourceCandidateCreate rcc = translatorEngine.buildVnfdResourceCandidate(vnfd.getProductName(), pair, rs);

        log.info("Posting Resource Candidate to Offer Catalog for vnfd " + vnfdId + ".");

        String rccJson = objectMapper.writeValueAsString(rcc);
        httpEntity =
                translatorCatalogInteractionService.post(rccJson, "/resourceCatalogManagement/v2/resourceCandidate");
        ResourceCandidate rc = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceCandidate.class);

        mappingInfoService.save(new MappingInfo(vnfdId, rc.getId(), rs.getId()));

        log.info("vnfd " + vnfdId + " translated & posted.");

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateVnfd(Vnfd vnfd, String functionType)
            throws IOException, CatalogException, MalformattedElementException {

        String vnfdId = vnfd.getId();

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(vnfdId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            Pair<ResourceCandidate, ResourceSpecification> pair = null;
            try {
                pair = translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                log.info("Vnfd " + vnfdId + " already translated and correctly posted on Offer Catalog.");
                return pair;
            }
            else {
                try {
                    mappingInfoService.delete(vnfdId);
                } catch (NotExistingEntityException e) {
                    log.info("Entry for " + vnfdId + " that should exists in DB, not found.");
                }

                return translateAndPostVnfd(vnfd, functionType);
            }
        }
        else
            return translateAndPostVnfd(vnfd, functionType);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateAndPostPnfd(Pnfd pnfd) throws IOException, CatalogException {

        String pnfdId = pnfd.getId();
        ResourceSpecificationCreate rsc = translatorEngine.buildPnfdResourceSpecification(pnfd);

        log.info("Posting Resource Specification to Offer Catalog for pnfd " + pnfdId + ".");

        String rscJson = objectMapper.writeValueAsString(rsc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(rscJson, "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        Pair<String, String> pair = getCategoryOrCreateIfNotExists(Kind.PNF,
                "/resourceCatalogManagement/v2/resourceCategory/filter");

        ResourceCandidateCreate rcc = translatorEngine.buildPnfdResourceCandidate(pnfd.getName(), pair, rs);

        log.info("Posting Resource Candidate to Offer Catalog for pnfd " + pnfdId + ".");

        String rccJson = objectMapper.writeValueAsString(rcc);
        httpEntity =
                translatorCatalogInteractionService.post(rccJson, "/resourceCatalogManagement/v2/resourceCandidate");
        ResourceCandidate rc = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceCandidate.class);

        mappingInfoService.save(new MappingInfo(pnfdId, rc.getId(), rs.getId()));

        log.info("pnfd " + pnfdId + " translated & posted.");

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translatePnfd(Pnfd pnfd) throws IOException, CatalogException {

        String pnfdId = pnfd.getId();

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(pnfdId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            Pair<ResourceCandidate, ResourceSpecification> pair = null;
            try {
                pair = translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                log.info("Pnfd " + pnfdId + " already translated and correctly posted on Offer Catalog.");
                return pair;
            }
            else {
                try {
                    mappingInfoService.delete(pnfdId);
                } catch (NotExistingEntityException e) {
                    log.info("Entry for " + pnfdId + " that should exists in DB, not found.");
                }

                return translateAndPostPnfd(pnfd);
            }
        }
        else
            return translateAndPostPnfd(pnfd);
    }

    public ResourceSpecification getFromSourceAndTranslateResource(Kind kind, String resource)
            throws MissingEntityOnSourceException, SourceException, IOException,
            CatalogException, MalformattedElementException {

        String infoId = translatorDescSourceInteractionService.getInfoIdFromDescriptorId(kind, resource);
        HttpEntity httpEntity = translatorDescSourceInteractionService.getFromSource(kind, infoId);

        ObjectMapper objectMapper = new ObjectMapper();
        switch(kind) {
            case VNF:
                String vnfdStringFromEntity = EntityUtils.toString(httpEntity);
                Vnfd vnfd;
                try {
                    vnfd = objectMapper.readValue(vnfdStringFromEntity, Vnfd.class);
                } catch (JsonProcessingException e) {
                    objectMapper = new ObjectMapper(new YAMLFactory());
                    vnfd = objectMapper.readValue(vnfdStringFromEntity, Vnfd.class);
                }
                return translateVnfd(vnfd, null).getSecond();

            case PNF:
                String pnfdStringFromEntity = EntityUtils.toString(httpEntity);
                Pnfd pnfd;
                try {
                    pnfd = objectMapper.readValue(EntityUtils.toString(httpEntity), Pnfd.class);
                } catch (JsonProcessingException e) {
                    objectMapper = new ObjectMapper(new YAMLFactory());
                    pnfd = objectMapper.readValue(pnfdStringFromEntity, Pnfd.class);
                }
                return translatePnfd(pnfd).getSecond();

            default:
                return null;
        }
    }

    public ServiceSpecification getFromSourceAndTranslateService(String service)
            throws MissingEntityOnSourceException, SourceException, IOException,
            CatalogException, MissingEntityOnCatalogException, MalformattedElementException {

        String infoId = translatorDescSourceInteractionService.getInfoIdFromDescriptorId(Kind.NS, service);
        HttpEntity httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.NS, infoId);

        ObjectMapper objectMapper = new ObjectMapper();
        String nsdStringFromEntity = EntityUtils.toString(httpEntity);
        Nsd nsd;
        try {
            nsd = objectMapper.readValue(nsdStringFromEntity, Nsd.class);
        } catch (JsonProcessingException e) {
            objectMapper = new ObjectMapper(new YAMLFactory());
            nsd = objectMapper.readValue(nsdStringFromEntity, Nsd.class);
        }
        return translateNsd(nsd, null).getSecond();
    }

    public List<ResourceSpecification> areResourcesPresent(Kind kind, List<String> resources)
            throws IOException, CatalogException, MissingEntityOnSourceException,
            SourceException, MalformattedElementException {

        List<ResourceSpecification> resourceSpecifications = new ArrayList<>();
        for(String resource : resources) {

            boolean found = true;
            MappingInfo mappingInfo = null;
            try {
                mappingInfo = mappingInfoService.get(resource);
            } catch (NotExistingEntityException e) {
                found = false;
            }

            if(found) {
                ResourceSpecification resourceSpecification = null;
                try {
                    resourceSpecification = translatorCatalogInteractionService
                            .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                    mappingInfo.getSpecificationCatalogId()).getSecond();
                } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                    found = false;
                }

                if(found) {
                    log.info("Resource " + resource + " exist and correctly posted on Offer Catalog.");
                    resourceSpecifications.add(resourceSpecification);
                }
                else {
                    log.info("Resource " +  resource + " not exist in Offer Catalog, trying to retrieve from " +
                            "descriptors source in order to translate.");

                    try {
                        mappingInfoService.delete(resource);
                    } catch (NotExistingEntityException e) {
                        log.info("Entry for " + resource + " that should exists in DB, not found.");
                    }

                    ResourceSpecification newResourceSpecification;
                    try {
                        newResourceSpecification = getFromSourceAndTranslateResource(kind, resource);
                    } catch (MissingEntityOnSourceException e) {
                        String msg = "Resource " + resource + " missing in descriptors source, abort.";
                        log.info(msg);
                        throw new MissingEntityOnSourceException(msg);
                    }

                    resourceSpecifications.add(newResourceSpecification);
                }
            }
            else {
                log.info("Resource " +  resource + " not translated, trying to retrieve from " +
                        "descriptors source in order to translate.");

                ResourceSpecification newResourceSpecification;
                try {
                    newResourceSpecification = getFromSourceAndTranslateResource(kind, resource);
                } catch (MissingEntityOnSourceException e) {
                    String msg = "Resource " + resource + " missing in descriptors source, abort.";
                    log.info(msg);
                    throw new MissingEntityOnSourceException(msg);
                }

                resourceSpecifications.add(newResourceSpecification);
            }
        }

        return resourceSpecifications;
    }

    public List<ServiceSpecification> areServicesPresent(List<String> nsdIds)
            throws CatalogException, IOException, MissingEntityOnCatalogException,
            SourceException, MissingEntityOnSourceException, MalformattedElementException {

        List<ServiceSpecification> serviceSpecifications = new ArrayList<>();
        for(String nsdId : nsdIds) {

            boolean found = true;
            MappingInfo mappingInfo = null;
            try {
                mappingInfo = mappingInfoService.get(nsdId);
            } catch (NotExistingEntityException e) {
                found = false;
            }

            if(found) {
                ServiceSpecification serviceSpecification = null;
                try {
                    serviceSpecification = translatorCatalogInteractionService
                            .isServicePresent(mappingInfo.getCandidateCatalogId(),
                                    mappingInfo.getSpecificationCatalogId()).getSecond();
                } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                    found = false;
                }

                if(found) {
                    log.info("Service " + nsdId + " exist and correctly posted on Offer Catalog.");
                    serviceSpecifications.add(serviceSpecification);
                }
                else {
                    try {
                        mappingInfoService.delete(nsdId);
                    } catch (NotExistingEntityException e) {
                        log.info("Entry for " + nsdId + " that should exists in DB, not found.");
                    }

                    ServiceSpecification newServiceSpecification;
                    try {
                        newServiceSpecification = getFromSourceAndTranslateService(nsdId);
                    } catch (MissingEntityOnSourceException e) {
                        String msg = "Service " + nsdId + " missing on descriptors source, abort.";
                        log.info(msg);
                        throw new MissingEntityOnSourceException(msg);
                    }

                    serviceSpecifications.add(newServiceSpecification);
                }
            }
            else {
                log.info("Service " + nsdId + " not translated, trying to retrieve from " +
                        "descriptors source in order to translate.");

                ServiceSpecification newServiceSpecification;
                try {
                    newServiceSpecification = getFromSourceAndTranslateService(nsdId);
                } catch (MissingEntityOnSourceException e) {
                    String msg = "Service " + nsdId + " missing on descriptors source, abort.";
                    log.info(msg);
                    throw new MissingEntityOnSourceException(msg);
                }

                serviceSpecifications.add(newServiceSpecification);
            }
        }

        return serviceSpecifications;
    }

    public Pair<ServiceCandidate, ServiceSpecification> translateAndPostNsd(Nsd nsd, String serviceType)
            throws CatalogException, IOException, MissingEntityOnCatalogException,
            MissingEntityOnSourceException, SourceException, MalformattedElementException {

        List<String> vnfds = nsd.getVnfdId();
        List<ResourceSpecification> vnfResourceSpecifications = new ArrayList<>();
        if(vnfds != null)
            vnfResourceSpecifications = areResourcesPresent(Kind.VNF, vnfds);

        List<String> pnfds = nsd.getPnfdId();
        List<ResourceSpecification> pnfResourceSpecifications = new ArrayList<>();
        if(pnfds != null)
            pnfResourceSpecifications = areResourcesPresent(Kind.PNF, pnfds);

        List<String> nsdIds = nsd.getNestedNsdId();
        List<ServiceSpecification> nsServiceSpecifications = new ArrayList<>();
        if(nsdIds != null)
            nsServiceSpecifications = areServicesPresent(nsdIds);

        String nsdId = nsd.getId();
        ServiceSpecificationCreate ssc =
                translatorEngine.buildNsdServiceSpecification(nsd, vnfResourceSpecifications,
                        pnfResourceSpecifications, nsServiceSpecifications, serviceType);

        log.info("Posting Service Specification to Offer Catalog for nsd " + nsdId + ".");

        String sscJson = objectMapper.writeValueAsString(ssc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(sscJson, "/serviceCatalogManagement/v4/serviceSpecification");
        ServiceSpecification ss = objectMapper.readValue(EntityUtils.toString(httpEntity), ServiceSpecification.class);

        Pair<String, String> pair = getCategoryOrCreateIfNotExists(Kind.NS,
                "/serviceCatalogManagement/v4/serviceCategory/filter");

        ServiceCandidateCreate scc = translatorEngine.buildNsdServiceCandidate(nsd.getName(), pair, ss);

        log.info("Posting Service Candidate to Offer Catalog for nsd " + nsdId + ".");

        String sccJson = objectMapper.writeValueAsString(scc);
        httpEntity =
                translatorCatalogInteractionService.post(sccJson, "/serviceCatalogManagement/v4/serviceCandidate");
        ServiceCandidate sc = objectMapper.readValue(EntityUtils.toString(httpEntity), ServiceCandidate.class);

        mappingInfoService.save(new MappingInfo(nsdId, sc.getId(), ss.getId()));

        log.info("nsd " + nsdId + " translated & posted.");

        return new Pair<>(sc, ss);
    }

    public Pair<ServiceCandidate, ServiceSpecification> translateNsd(Nsd nsd, String serviceType)
            throws IOException, CatalogException, MissingEntityOnCatalogException,
            MissingEntityOnSourceException, SourceException, MalformattedElementException {

        String nsdId = nsd.getId();

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(nsdId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            Pair<ServiceCandidate, ServiceSpecification> pair = null;
            try {
                pair = translatorCatalogInteractionService
                        .isServicePresent(mappingInfo.getCandidateCatalogId(), mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                log.info("Nsd " + nsdId + " already translated and correctly posted in Offer Catalog.");
                return pair;
            }
            else {
                try {
                    mappingInfoService.delete(nsdId);
                } catch (NotExistingEntityException e) {
                    log.info("Entry for " + nsdId + " that should exists in DB, not found.");
                }

                return translateAndPostNsd(nsd, serviceType);
            }
        }
        else
            return translateAndPostNsd(nsd, serviceType);
    }

    private Pair<String, String> getGeographicAddressOrCreate(GeographicAddressCreate geographicAddressCreate)
            throws CatalogException, IOException, MalformattedElementException {

        if(geographicAddressCreate == null)
            throw new MalformattedElementException("Empty Geographic Address, cannot build Resource Specification.");

        GeographicLocation geographicLocation = geographicAddressCreate.getGeographicLocation();
        if(geographicLocation == null)
            throw new MalformattedElementException("Empty Geographic Location in Geographic Address, cannot build Resource Specification.");

        List<GeographicPoint> geographicPoints = geographicLocation.getGeometry();
        if(geographicPoints == null || geographicPoints.isEmpty())
            throw new MalformattedElementException("Empty Geographic Point in Geographic Location, cannot build Resource Specification.");

        GeographicPoint geographicPoint = geographicPoints.get(0);
        String x = geographicPoint.getX();
        String y = geographicPoint.getY();

        log.info("Checking if GeographicAddress with coordinate <{}, {}> exists in Offer Catalog.", x, y);

        List<GeographicAddress> geographicAddresses = null;
        try {
            geographicAddresses = Arrays.asList(objectMapper.readValue(EntityUtils
                    .toString(translatorCatalogInteractionService.getFromCatalog("/geographicAddressManagement/v4/geographicAddress/coordinates",
                            "?x=" + x + "&y=" + y)), GeographicAddress[].class));
        } catch (MissingEntityOnCatalogException ignored) {}

        if((geographicAddresses != null ? geographicAddresses.size() : 0) != 1) {
            log.info("No or multiple Geographic Address with coordinates <{}, {}> found in the Offer Catalog, creating new Geographic Address.", x, y);

            GeographicAddressValidationCreate geographicAddressValidationCreate =
                    new GeographicAddressValidationCreate().submittedGeographicAddress(geographicAddressCreate);
            HttpEntity httpEntity = translatorCatalogInteractionService.post(objectMapper.writeValueAsString(geographicAddressValidationCreate),
                    "/geographicAddressManagement/v4/geographicAddressValidation");
            GeographicAddress geographicAddress = objectMapper.readValue(EntityUtils.toString(httpEntity), GeographicAddress.class);

            return new Pair<>(geographicAddress.getId(), geographicAddress.getHref());
        }

        GeographicAddress geographicAddress = geographicAddresses.get(0);
        return new Pair<>(geographicAddress.getId(), geographicAddress.getHref());
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateAndPostSpc(TranslatorRAPPInteractionService.RAPPWrapper rappWrapper, String spcId)
            throws IOException, CatalogException, MalformattedElementException {

        Pair<String, String> geographicAddressRef = getGeographicAddressOrCreate(rappWrapper.getGeographicAddressCreate());
        ResourceSpecificationCreate resourceSpecificationCreate = rappWrapper.getResourceSpecificationCreate();
        resourceSpecificationCreate.setFeature(Collections.singletonList(new Feature()
                .name("Geographic Address")
                .id(geographicAddressRef.getFirst())
                .href(geographicAddressRef.getSecond())));

        log.info("Posting Resource Specification to Offer Catalog for spectrum resource " + spcId + ".");

        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(objectMapper.writeValueAsString(resourceSpecificationCreate), "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs =
                objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        Pair<String, String> pair = getCategoryOrCreateIfNotExists(Kind.SPC,
                "/resourceCatalogManagement/v2/resourceCategory/filter");

        ResourceCandidateCreate rcc = translatorEngine.buildSpcResourceCandidate(pair, rs);

        log.info("Posting Resource Candidate to Offer Catalog for spectrum resource " + spcId + ".");

        String rccJson = objectMapper.writeValueAsString(rcc);
        httpEntity =
                translatorCatalogInteractionService.post(rccJson, "/resourceCatalogManagement/v2/resourceCandidate");
        ResourceCandidate rc = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceCandidate.class);

        mappingInfoService.save(new MappingInfo(spcId, rc.getId(), rs.getId()));

        log.info("Spectrum Resource " + spcId + " translated & posted.");

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateSpc(TranslatorRAPPInteractionService.RAPPWrapper rappWrapper, String spcId)
            throws IOException, CatalogException, MalformattedElementException {

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(spcId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            Pair<ResourceCandidate, ResourceSpecification> pair = null;
            try {
                pair = translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(), mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                log.info("Spectrum Resource " + spcId + " already translated and correctly posted on Offer Catalog.");
                return pair;
            } else {
                try {
                    mappingInfoService.delete(spcId);
                } catch (NotExistingEntityException e) {
                    log.info("Entry for " + spcId + " that should exists in DB, not found.");
                }

                return translateAndPostSpc(rappWrapper, spcId);
            }
        } else
            return translateAndPostSpc(rappWrapper, spcId);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateAndPostRad(TranslatorRAPPInteractionService.RAPPWrapper rappWrapper, String radId)
            throws IOException, CatalogException, MalformattedElementException {

        Pair<String, String> geographicAddressRef = getGeographicAddressOrCreate(rappWrapper.getGeographicAddressCreate());
        ResourceSpecificationCreate resourceSpecificationCreate = rappWrapper.getResourceSpecificationCreate();
        resourceSpecificationCreate.setFeature(Collections.singletonList(new Feature()
                .name("Geographic Address")
                .id(geographicAddressRef.getFirst())
                .href(geographicAddressRef.getSecond())));

        log.info("Posting Resource Specification to Offer Catalog for radio resource " + radId + ".");

        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(objectMapper.writeValueAsString(resourceSpecificationCreate), "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs =
                objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        Pair<String, String> pair = getCategoryOrCreateIfNotExists(Kind.RAD,
                "/resourceCatalogManagement/v2/resourceCategory/filter");

        ResourceCandidateCreate rcc = translatorEngine.buildRadResourceCandidate(pair, rs);

        log.info("Posting Resource Candidate to Offer Catalog for radio resource " + radId + ".");

        String rccJson = objectMapper.writeValueAsString(rcc);
        httpEntity =
                translatorCatalogInteractionService.post(rccJson, "/resourceCatalogManagement/v2/resourceCandidate");
        ResourceCandidate rc = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceCandidate.class);

        mappingInfoService.save(new MappingInfo(radId, rc.getId(), rs.getId()));

        log.info("Spectrum Resource " + radId + " translated & posted.");

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateRad(TranslatorRAPPInteractionService.RAPPWrapper rappWrapper, String radId)
            throws IOException, CatalogException, MalformattedElementException {

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(radId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            Pair<ResourceCandidate, ResourceSpecification> pair = null;
            try {
                pair = translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(), mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                log.info("Radio Resource " + radId + " already translated and correctly posted on Offer Catalog.");
                return pair;
            } else {
                try {
                    mappingInfoService.delete(radId);
                } catch (NotExistingEntityException e) {
                    log.info("Entry for " + radId + " that should exists in DB, not found.");
                }

                return translateAndPostRad(rappWrapper, radId);
            }
        } else
            return translateAndPostRad(rappWrapper, radId);
    }
}
