package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.persistence.MappingInfo;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceCandidate;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceCandidateCreate;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceSpecification;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceSpecificationCreate;
import it.nextworks.sol006_tmf_translator.information_models.sol006.Pnfd;
import it.nextworks.sol006_tmf_translator.information_models.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.config.CustomOffsetDateTimeSerializer;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;

@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private final ObjectMapper objectMapper;

    private final TranslatorEngine translatorEngine;

    private final TranslatorCatalogInteractionService translatorCatalogInteractionService;

    private final MappingInfoService mappingInfoService;

    @Autowired
    public TranslationService(ObjectMapper objectMapper,
                              TranslatorEngine translatorEngine,
                              TranslatorCatalogInteractionService translatorCatalogInteractionService,
                              MappingInfoService mappingInfoService) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        this.objectMapper.registerModule(module);

        this.translatorEngine = translatorEngine;
        this.translatorCatalogInteractionService = translatorCatalogInteractionService;
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

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translateVnfd(Vnfd vnfd)
            throws IOException, CatalogException, DescriptorAlreadyTranslatedException {

        String vnfdId = vnfd.getId();

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(vnfdId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            try {
                translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found) {
                String msg = "Vnfd " + vnfdId + " already translated and correctly posted on Offer Catalog.";
                log.info(msg);
                throw new DescriptorAlreadyTranslatedException(msg);
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

        return new Pair<>(rc, rs);
    }

    public Pair<ResourceCandidate, ResourceSpecification> translatePnfd(Pnfd pnfd)
            throws IOException, CatalogException, DescriptorAlreadyTranslatedException {

        String pnfdId = pnfd.getId();

        boolean found = true;
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingInfoService.get(pnfdId);
        } catch (NotExistingEntityException e) {
            found = false;
        }

        if(found) {
            try {
                translatorCatalogInteractionService
                        .isResourcePresent(mappingInfo.getCandidateCatalogId(),
                                mappingInfo.getSpecificationCatalogId());
            } catch (MissingEntityOnCatalogException | ResourceMismatchException e) {
                found = false;
            }

            if(found){
                String msg = "Pnfd " + pnfdId + " already translated and correctly posted on Offer Catalog.";
                log.info(msg);
                throw new DescriptorAlreadyTranslatedException(msg);
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
}
