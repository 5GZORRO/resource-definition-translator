package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceCandidate;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceSpecification;
import it.nextworks.sol006_tmf_translator.information_models.sol006.Pnfd;
import it.nextworks.sol006_tmf_translator.information_models.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.interfaces.TranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogPostException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslatorEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class TranslatorController implements TranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(TranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslatorEngine translatorEngine;

    @Autowired
    public TranslatorController(ObjectMapper objectMapper,
                                HttpServletRequest request,
                                TranslatorEngine translatorEngine) {
        this.objectMapper     = objectMapper;
        this.request          = request;
        this.translatorEngine = translatorEngine;
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

        Pair<ResourceCandidate, ResourceSpecification> translatedVnfd;
        try {
            translatedVnfd = translatorEngine.translateVNFD(vnfd);
        } catch (IOException | CatalogPostException e) {
            log.error("Web-Server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translatedVnfd);
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/pnfdToTmf",
            produces = { "application/json;charset=utf-8" },
            consumes = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translatePnfd(@ApiParam(value = "The PNFD to be translated.", required = true) @Valid @RequestBody Pnfd pnfd) {

        Pair<ResourceCandidate, ResourceSpecification> translatedVnfd;
        try {
            translatedVnfd = translatorEngine.translatePNFD(pnfd);
        } catch (IOException | CatalogPostException e) {
            log.error("Web-Server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translatedVnfd);
    }
}
