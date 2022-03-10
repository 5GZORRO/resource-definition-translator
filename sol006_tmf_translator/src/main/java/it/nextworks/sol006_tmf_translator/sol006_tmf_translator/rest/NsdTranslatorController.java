package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.sol006_tmf_translator.information_models.commons.CSARInfo;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.NsdTranslatorInterface;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.ArchiveParser;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslationService;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslatorDescSourceInteractionService;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceSpecification;
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
public class NsdTranslatorController implements NsdTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(NsdTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslationService translationService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    private final ArchiveParser archiveParser;

    @Autowired
    public NsdTranslatorController(ObjectMapper objectMapper,
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
    @RequestMapping(value = "/nsdToTmf",
            produces = { "application/json;charset=utf-8" },
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateNsd(@ApiParam(value = "NS package", required = true) @RequestPart("file") MultipartFile body) {

        CSARInfo csarInfo;
        try {
            csarInfo = archiveParser.archiveToCSARInfo(body, Kind.NS);
        } catch (IOException | FailedOperationException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MalformattedElementException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        Nsd nsd = csarInfo.getNsd();
        String nsdId = nsd.getId();

        log.info("Received request to translate & post nsd with id " + nsdId + ".");

        Pair<ServiceCandidate, ServiceSpecification> translation;
        try {
            translation = translationService.translateNsd(nsd, null);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException | SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnCatalogException | MissingEntityOnSourceException | NotExistingEntityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        } catch (MalformattedElementException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        try {
            translatorDescSourceInteractionService.getInfoIdFromDescriptorId(Kind.NS, nsdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            log.info("Posting nsd " + nsdId + " to descriptors source.");
            try {
                translatorDescSourceInteractionService.postOnSource(Kind.NS, csarInfo.getPackagePath());
                log.info("nsd " + nsdId + " posted on descriptors source.");
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
    @RequestMapping(value = "/nsdToTmf/{nsPkgInfoId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateNsdById(@ApiParam(value = "ns package info ID of the ns to be translated.", required = true)
                     @PathVariable("nsPkgInfoId") String nsPkgInfoId,
                     @ApiParam(value = "Type of the service to be translated.")
                     @RequestParam(value = "serviceType", required = false) String serviceType) {

        log.info("Received request to translate & post nsd for ns with ns package info id " + nsPkgInfoId + ".");

        HttpEntity httpEntity;
        try {
            log.info("Retrieving nsd for ns with ns package info id  " + nsPkgInfoId + " from descriptors source.");
            httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.NS, nsPkgInfoId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = "nsd with ns package info id " + nsPkgInfoId + " not found in descriptor source.";
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        String nsdStringFromEntity;
        try {
            nsdStringFromEntity = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error("Error parsing http entity: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Nsd nsd;
        try {
            nsd = objectMapper.readValue(nsdStringFromEntity, Nsd.class);
        } catch (JsonProcessingException e) {
            objectMapper = new ObjectMapper(new YAMLFactory());
            try {
                nsd = objectMapper.readValue(nsdStringFromEntity, Nsd.class);
            } catch (JsonProcessingException jsonProcessingException) {
                String msg = e.getMessage();
                log.error("Error parsing descriptor: \n" + msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
            }
        }

        Pair<ServiceCandidate, ServiceSpecification> translation;
        try {
            translation = translationService.translateNsd(nsd, serviceType);
        } catch (IOException | CatalogException | SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException | MissingEntityOnCatalogException | NotExistingEntityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        } catch (MalformattedElementException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
