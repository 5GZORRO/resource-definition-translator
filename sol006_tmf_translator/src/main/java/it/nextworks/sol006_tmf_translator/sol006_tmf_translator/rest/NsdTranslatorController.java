package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.NsdTranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnCatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@RestController
public class NsdTranslatorController implements NsdTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(NsdTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslationService translationService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    @Autowired
    public NsdTranslatorController(ObjectMapper objectMapper,
                                    HttpServletRequest request,
                                    TranslationService translationService,
                                    TranslatorDescSourceInteractionService translatorDescSourceInteractionService) {
        this.objectMapper       = objectMapper;
        this.request            = request;
        this.translationService = translationService;
        this.translatorDescSourceInteractionService = translatorDescSourceInteractionService;
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/nsdToTmf",
            produces = { "application/json;charset=utf-8" },
            consumes = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateNsd(@ApiParam(value = "The NSD to be translated.", required = true) @Valid @RequestBody Nsd nsd) {

        if(nsd == null) {
            log.info("Received a translation request with null nsd.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrMsg("received a translation request with null nsd."));
        }

        String nsdId = nsd.getId();
        if(nsdId == null) {
            nsdId = UUID.randomUUID().toString();
            nsd.setId(nsdId);
            log.info("Received request to translate & post nsd without id, generated: " + nsdId + ".");
        }
        else
            log.info("Received request to translate & post nsd with id " + nsdId + ".");

        Pair<ServiceCandidate, ServiceSpecification> translation;
        try {
            translation = translationService.translateNsd(nsd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException | SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnCatalogException | MissingEntityOnSourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        try {
            translatorDescSourceInteractionService.getFromSource(Kind.NSD, nsdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            log.info("Posting nsd " + nsdId + " to descriptors source.");
            try {
                translatorDescSourceInteractionService.postOnSource(Kind.NSD, objectMapper.writeValueAsString(nsd));
                log.info("nsd " + nsd + " posted on descriptors source.");
            } catch (UnsupportedEncodingException | SourceException | JsonProcessingException ee) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(ee.getMessage()));
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/nsdToTmf/{nsdId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateNsdById(@ApiParam(value = "The NSD to be translated.", required = true) @PathVariable("nsdId") String nsdId) {

        log.info("Received request to translate & post nsd with id " + nsdId + ".");

        HttpEntity httpEntity;
        try {
            log.info("Retrieving nsd " + nsdId + " from descriptors source.");
            httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.NSD, nsdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = e.getMessage();
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        Pair<ServiceCandidate, ServiceSpecification> translation;
        try {
            translation = translationService.translateNsd(objectMapper.readValue(EntityUtils.toString(httpEntity),
                    Nsd.class));
        } catch (IOException | CatalogException | SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException | MissingEntityOnCatalogException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
