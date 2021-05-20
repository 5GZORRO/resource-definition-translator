package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.persistence.MappingInfo;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.config.CustomOffsetDateTimeSerializer;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import it.nextworks.tmf_offering_catalog.information_models.common.ResourceSpecificationRef;
import it.nextworks.tmf_offering_catalog.information_models.common.ServiceSpecificationRef;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceCandidateCreate;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecification;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecificationCreate;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceCandidateCreate;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceSpecification;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceSpecificationCreate;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private final ObjectMapper objectMapper;

    private final TranslatorEngine translatorEngine;

    private final TranslatorCatalogInteractionService translatorCatalogInteractionService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    private final MappingInfoService mappingInfoService;

    @Autowired
    public TranslationService(ObjectMapper objectMapper,
                              TranslatorEngine translatorEngine,
                              TranslatorCatalogInteractionService translatorCatalogInteractionService,
                              TranslatorDescSourceInteractionService translatorDescSourceInteractionService,
                              MappingInfoService mappingInfoService) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        this.objectMapper.registerModule(module);

        this.translatorEngine = translatorEngine;
        this.translatorCatalogInteractionService = translatorCatalogInteractionService;
        this.translatorDescSourceInteractionService = translatorDescSourceInteractionService;
        this.mappingInfoService = mappingInfoService;
    }

    private Pair<ResourceCandidate, ResourceSpecification> translateAndPostVnfd(Vnfd vnfd) throws IOException, CatalogException {

        String vnfdId = vnfd.getId();
        ResourceSpecificationCreate rsc = translatorEngine.buildVnfdResourceSpecification(vnfd);

        log.info("Posting Resource Specification to Offer Catalog for vnfd " + vnfdId + ".");

        String rscJson = objectMapper.writeValueAsString(rsc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(rscJson, "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs =
                objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        ResourceCandidateCreate rcc = translatorEngine.buildVnfdResourceCandidate(vnfdId, rs);

        log.info("Posting Resource Candidate to Offer Catalog for vnfd " + vnfdId + ".");

        String rccJson = objectMapper.writeValueAsString(rcc);
        httpEntity =
                translatorCatalogInteractionService.post(rccJson, "/resourceCatalogManagement/v2/resourceCandidate");
        ResourceCandidate rc = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceCandidate.class);

        mappingInfoService.save(new MappingInfo(vnfdId, rc.getId(), rs.getId()));

        log.info("vnfd " + vnfdId + " translated & posted.");

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateVnfd(Vnfd vnfd) throws IOException, CatalogException {

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

                return translateAndPostVnfd(vnfd);
            }
        }
        else
            return translateAndPostVnfd(vnfd);
    }

    private Pair<ResourceCandidate, ResourceSpecification> translateAndPostPnfd(Pnfd pnfd) throws IOException, CatalogException {

        String pnfdId = pnfd.getId();
        ResourceSpecificationCreate rsc = translatorEngine.buildPnfdResourceSpecification(pnfd);

        log.info("Posting Resource Specification to Offer Catalog for pnfd " + pnfdId + ".");

        String rscJson = objectMapper.writeValueAsString(rsc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(rscJson, "/resourceCatalogManagement/v2/resourceSpecification");
        ResourceSpecification rs = objectMapper.readValue(EntityUtils.toString(httpEntity), ResourceSpecification.class);

        ResourceCandidateCreate rcc = translatorEngine.buildPnfdResourceCandidate(pnfdId, rs);

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

    private ResourceSpecificationRef getFromSourceAndTranslateResource(Kind kind, String resource)
            throws MissingEntityOnSourceException, SourceException, IOException, CatalogException {

        HttpEntity httpEntity = translatorDescSourceInteractionService.getFromSource(kind, resource);

        switch(kind) {
            case VNFD:
                Vnfd vnfd = objectMapper.readValue(EntityUtils.toString(httpEntity), Vnfd.class);
                return translateVnfd(vnfd).getFirst().getResourceSpecification();

            case PNFD:
                Pnfd pnfd = objectMapper.readValue(EntityUtils.toString(httpEntity), Pnfd.class);
                return translatePnfd(pnfd).getFirst().getResourceSpecification();

            default:
                return null;
        }
    }

    private ServiceSpecificationRef getFromSourceAndTranslateService(String service)
            throws MissingEntityOnSourceException, SourceException, IOException,
            CatalogException, MissingEntityOnCatalogException {

        HttpEntity httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.NSD, service);
        Nsd nsd = objectMapper.readValue(EntityUtils.toString(httpEntity), Nsd.class);
        return translateNsd(nsd).getFirst().getServiceSpecification();
    }

    private List<ResourceSpecificationRef> areResourcesPresent(Kind kind, List<String> resources)
            throws IOException, CatalogException, MissingEntityOnSourceException, SourceException {

        List<ResourceSpecificationRef> refs = new ArrayList<>();
        for(String resource : resources) {

            boolean found = true;
            MappingInfo mappingInfo = null;
            try {
                mappingInfo = mappingInfoService.get(resource);
            } catch (NotExistingEntityException e) {
                found = false;
            }

            if(found) {
                ResourceSpecificationRef ref = null;
                try {
                    ref = translatorCatalogInteractionService
                            .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                    mappingInfo.getSpecificationCatalogId()).getFirst().getResourceSpecification();
                } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                    found = false;
                }

                if(found) {
                    log.info("Resource " + resource + " exist and correctly posted on Offer Catalog.");
                    refs.add(ref);
                }
                else {
                    log.info("Resource " +  resource + " not exist in Offer Catalog, trying to retrieve from " +
                            "descriptors source in order to translate.");

                    try {
                        mappingInfoService.delete(resource);
                    } catch (NotExistingEntityException e) {
                        log.info("Entry for " + resource + " that should exists in DB, not found.");
                    }

                    ResourceSpecificationRef newRef;
                    try {
                        newRef = getFromSourceAndTranslateResource(kind, resource);
                    } catch (MissingEntityOnSourceException e) {
                        String msg = "Resource " + resource + " missing in descriptors source, abort.";
                        log.info(msg);
                        throw new MissingEntityOnSourceException(msg);
                    }

                    refs.add(newRef);
                }
            }
            else {
                log.info("Resource " +  resource + " not translated, trying to retrieve from " +
                        "5G Catalog in order to translate.");

                ResourceSpecificationRef newRef;
                try {
                    newRef = getFromSourceAndTranslateResource(kind, resource);
                } catch (MissingEntityOnSourceException e) {
                    String msg = "Resource " + resource + " missing in descriptors source, abort.";
                    log.info(msg);
                    throw new MissingEntityOnSourceException(msg);
                }

                refs.add(newRef);
            }
        }

        return refs;
    }

    private List<ServiceSpecificationRef> areServicesPresent(List<String> nsdIds)
            throws CatalogException, IOException, MissingEntityOnCatalogException,
            SourceException, MissingEntityOnSourceException {

        List<ServiceSpecificationRef> refs = new ArrayList<>();
        for(String nsdId : nsdIds) {

            boolean found = true;
            MappingInfo mappingInfo = null;
            try {
                mappingInfo = mappingInfoService.get(nsdId);
            } catch (NotExistingEntityException e) {
                found = false;
            }

            if(found) {
                ServiceSpecificationRef ref = null;
                try {
                    ref = translatorCatalogInteractionService
                            .isServicePresent(mappingInfo.getCandidateCatalogId(),
                                    mappingInfo.getSpecificationCatalogId()).getFirst().getServiceSpecification();
                } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                    found = false;
                }

                if(found) {
                    log.info("Service " + nsdId + " exist and correctly posted on Offer Catalog.");
                    refs.add(ref);
                }
                else {
                    try {
                        mappingInfoService.delete(nsdId);
                    } catch (NotExistingEntityException e) {
                        log.info("Entry for " + nsdId + " that should exists in DB, not found.");
                    }

                    ServiceSpecificationRef newRef;
                    try {
                        newRef = getFromSourceAndTranslateService(nsdId);
                    } catch (MissingEntityOnSourceException e) {
                        String msg = "Service " + nsdId + " missing on 5G Catalog, abort.";
                        log.info(msg);
                        throw new MissingEntityOnSourceException(msg);
                    }

                    refs.add(newRef);
                }
            }
            else {
                log.info("Service " + nsdId + " not translated, trying to retrieve from " +
                        "5G Catalog in order to translate.");

                ServiceSpecificationRef newRef;
                try {
                    newRef = getFromSourceAndTranslateService(nsdId);
                } catch (MissingEntityOnSourceException e) {
                    String msg = "Service " + nsdId + " missing on 5G Catalog, abort.";
                    log.info(msg);
                    throw new MissingEntityOnSourceException(msg);
                }

                refs.add(newRef);
            }
        }

        return refs;
    }

    private Pair<ServiceCandidate, ServiceSpecification> translateAndPostNsd(Nsd nsd)
            throws CatalogException, IOException, MissingEntityOnCatalogException,
            MissingEntityOnSourceException, SourceException {

        List<String> vnfds = nsd.getVnfdId();
        List<ResourceSpecificationRef> vnfdRefs = new ArrayList<>();
        if(vnfds != null)
            vnfdRefs = areResourcesPresent(Kind.VNFD, vnfds);

        List<String> pnfds = nsd.getPnfdId();
        List<ResourceSpecificationRef> pnfdRefs = new ArrayList<>();
        if(pnfds != null)
            pnfdRefs = areResourcesPresent(Kind.PNFD, pnfds);

        List<String> nsdIds = nsd.getNestedNsdId();
        List<ServiceSpecificationRef> nsdRefs = new ArrayList<>();
        if(nsdIds != null)
            nsdRefs = areServicesPresent(nsdIds);

        String nsdId = nsd.getId();
        ServiceSpecificationCreate ssc =
                translatorEngine.buildNsdServiceSpecification(nsd, vnfdRefs, pnfdRefs, nsdRefs);

        log.info("Posting Service Specification to Offer Catalog for nsd " + nsdId + ".");

        String sscJson = objectMapper.writeValueAsString(ssc);
        HttpEntity httpEntity = translatorCatalogInteractionService
                .post(sscJson, "/serviceCatalogManagement/v4/serviceSpecification");
        ServiceSpecification ss = objectMapper.readValue(EntityUtils.toString(httpEntity), ServiceSpecification.class);

        ServiceCandidateCreate scc = translatorEngine.buildNsdServiceCandidate(nsdId, ss);

        log.info("Posting Service Candidate to Offer Catalog for nsd " + nsdId + ".");

        String sccJson = objectMapper.writeValueAsString(scc);
        httpEntity =
                translatorCatalogInteractionService.post(sccJson, "/serviceCatalogManagement/v4/serviceCandidate");
        ServiceCandidate sc = objectMapper.readValue(EntityUtils.toString(httpEntity), ServiceCandidate.class);

        mappingInfoService.save(new MappingInfo(nsdId, sc.getId(), ss.getId()));

        log.info("nsd " + nsdId + " translated & posted.");

        return new Pair<>(sc, ss);
    }

    public Pair<ServiceCandidate, ServiceSpecification> translateNsd(Nsd nsd)
            throws IOException, CatalogException, MissingEntityOnCatalogException,
            MissingEntityOnSourceException, SourceException {

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

                return translateAndPostNsd(nsd);
            }
        }
        else
            return translateAndPostNsd(nsd);
    }
}
