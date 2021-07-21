package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.CSARInfo;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.PnfdTranslatorInterface;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.ArchiveParser;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslationService;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslatorDescSourceInteractionService;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecification;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class PnfdTranslatorController implements PnfdTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(PnfdTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslationService translationService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    private final ArchiveParser archiveParser;

    @Autowired
    public PnfdTranslatorController(ObjectMapper objectMapper,
                                    HttpServletRequest request,
                                    TranslationService translationService,
                                    TranslatorDescSourceInteractionService translatorDescSourceInteractionService,
                                    ArchiveParser archiveParser) {
        this.objectMapper       = objectMapper;
        this.request            = request;
        this.translationService = translationService;
        this.translatorDescSourceInteractionService = translatorDescSourceInteractionService;
        this.archiveParser = archiveParser;
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/pnfdToTmf",
            produces = { "application/json;charset=utf-8" },
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translatePnfd(@ApiParam(value = "PNF package", required = true) @RequestPart("file") MultipartFile body) {

        CSARInfo csarInfo;
        try {
            csarInfo = archiveParser.archiveToCSARInfo(body, Kind.PNF);
        } catch (IOException | FailedOperationException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MalformattedElementException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        Pnfd pnfd = csarInfo.getPnfd();
        String pnfdId = pnfd.getId();

        log.info("Received request to translate & post pnfd with id " + pnfdId + ".");

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translatePnfd(pnfd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        try {
            translatorDescSourceInteractionService.getInfoIdFromDescriptorId(Kind.PNF, pnfdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            log.info("Posting pnfd " + pnfdId + " to descriptors source.");
            try {
                translatorDescSourceInteractionService.postOnSource(Kind.PNF, csarInfo.getPackagePath());
                log.info("pnfd " + pnfdId + " posted on descriptors source.");
            } catch (SourceException | IOException ee) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(ee.getMessage()));
            }
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/pnfdToTmf/{pnfPkgInfoId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translatePnfdById(@ApiParam(value = "pnf package info ID of the pnf to be translated.", required = true) @PathVariable("pnfPkgInfoId") String pnfPkgInfoId) {

        log.info("Received request to translate & post pnfd for pnf with pnf package info id " + pnfPkgInfoId + ".");

        HttpEntity httpEntity;
        try {
            log.info("Retrieving pnfd for pnf with pnf package info id " + pnfPkgInfoId + " from descriptors source.");
            httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.PNF, pnfPkgInfoId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = "pnf with pnf package info id " + pnfPkgInfoId + " not found in descriptor source.";
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        String pnfdStringFromEntity;
        try {
            pnfdStringFromEntity = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error("Error parsing http entity: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Pnfd pnfd;
        try {
            pnfd = objectMapper.readValue(pnfdStringFromEntity, Pnfd.class);
        } catch (JsonProcessingException e) {
            objectMapper = new ObjectMapper(new YAMLFactory());
            try {
                pnfd = objectMapper.readValue(pnfdStringFromEntity, Pnfd.class);
            } catch (JsonProcessingException jsonProcessingException) {
                String msg = e.getMessage();
                log.error("Error parsing descriptor: \n" + msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
            }
        }

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translatePnfd(pnfd);
        } catch (IOException | CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
