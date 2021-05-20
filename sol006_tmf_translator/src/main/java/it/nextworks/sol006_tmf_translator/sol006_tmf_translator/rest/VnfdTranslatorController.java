package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.VnfdTranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@RestController
public class VnfdTranslatorController implements VnfdTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(VnfdTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslationService translationService;

    private final TranslatorDescSourceInteractionService translatorDescSourceInteractionService;

    @Autowired
    public VnfdTranslatorController(ObjectMapper objectMapper,
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
    @RequestMapping(value = "/vnfdToTmf",
            produces = { "application/json;charset=utf-8" },
            consumes = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateVnfd(@ApiParam(value = "The VNFD to be translated.", required = true) @Valid @RequestBody Vnfd vnfd) {

        if(vnfd == null) {
            log.info("Received a translation request with null vnfd.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrMsg("received a translation request with null vnfd."));
        }

        String vnfdId = vnfd.getId();
        if(vnfdId == null) {
            vnfdId = UUID.randomUUID().toString();
            vnfd.setId(vnfdId);
            log.info("Received request to translate & post vnfd without id, generated: " + vnfdId + ".");
        }
        else
            log.info("Received request to translate & post vnfd with id " + vnfdId + ".");

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translateVnfd(vnfd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        try {
            translatorDescSourceInteractionService.getFromSource(Kind.VNFD, vnfdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            log.info("Posting vnfd " + vnfdId + " to descriptors source.");
            try {
                translatorDescSourceInteractionService.postOnSource(Kind.VNFD, objectMapper.writeValueAsString(vnfd));
                log.info("vnfd " + vnfdId + " posted on descriptors source.");
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
    @RequestMapping(value = "/vnfdToTmf/{vnfdId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateVnfdById(@ApiParam(value = "The VNFD to be translated.", required = true) @PathVariable("vnfdId") String vnfdId) {

        log.info("Received request to translate & post vnfd with id " + vnfdId + ".");

        HttpEntity httpEntity;
        try {
            log.info("Retrieving vnfd " + vnfdId + " from descriptors source.");
            httpEntity = translatorDescSourceInteractionService.getFromSource(Kind.VNFD, vnfdId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = e.getMessage();
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translateVnfd(objectMapper.readValue(EntityUtils.toString(httpEntity),
                    Vnfd.class));
        } catch (IOException | CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
