package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.EdgeTranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslationService;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslatorSliceManagerInteractionService;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class EdgeTranslatorController implements EdgeTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(EdgeTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslatorSliceManagerInteractionService translatorSliceManagerInteractionService;

    private final TranslationService translationService;

    @Autowired
    public EdgeTranslatorController(ObjectMapper objectMapper,
                                    HttpServletRequest request,
                                    TranslatorSliceManagerInteractionService translatorSliceManagerInteractionService,
                                    TranslationService translationService) {
        this.objectMapper                             = objectMapper;
        this.request                                  = request;
        this.translatorSliceManagerInteractionService = translatorSliceManagerInteractionService;
        this.translationService                       = translationService;
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/edgeToTmf/{sliceTypeId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateEdge(@ApiParam(value = "Slice Type ID of the Edge Resource to be translated.", required = true)
                  @PathVariable("sliceTypeId") String sliceTypeId) {

        log.info("Received request to translate & post Edge Resource with Slice Type ID {}.", sliceTypeId);

        TranslatorSliceManagerInteractionService.SliceType sliceType;
        try {
            sliceType = translatorSliceManagerInteractionService.getSliceType(sliceTypeId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = "Edge Resource with id " + sliceTypeId + " not found in Slice Manager.";
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        TranslatorSliceManagerInteractionService.SliceTypeChunks sliceTypeBlueprint;
        try {
            sliceTypeBlueprint = translatorSliceManagerInteractionService.getSliceBlueprint(sliceTypeId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = "Edge Blueprint with id " + sliceTypeId + " not found in Slice Manager.";
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translateAndPostEdge(sliceType, sliceTypeBlueprint);
        } catch (NotExistingEntityException e) {
            String msg = e.getMessage();
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        } catch (IOException | CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
