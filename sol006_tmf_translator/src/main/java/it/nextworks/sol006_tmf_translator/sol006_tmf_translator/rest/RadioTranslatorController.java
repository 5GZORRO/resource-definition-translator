package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.information_models.commons.SpectrumParameters;
import it.nextworks.sol006_tmf_translator.interfaces.RadioTranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.*;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslationService;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslatorRAPPInteractionService;
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
import javax.validation.Valid;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
public class RadioTranslatorController implements RadioTranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(RadioTranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslatorSliceManagerInteractionService translatorSliceManagerInteractionService;

    private final TranslatorRAPPInteractionService translatorRAPPInteractionService;

    private final TranslationService translationService;

    @Autowired
    public RadioTranslatorController(ObjectMapper objectMapper,
                                     HttpServletRequest request,
                                     TranslatorSliceManagerInteractionService translatorSliceManagerInteractionService,
                                     TranslatorRAPPInteractionService translatorRAPPInteractionService,
                                     TranslationService translationService) {
        this.objectMapper                             = objectMapper;
        this.request                                  = request;
        this.translatorSliceManagerInteractionService = translatorSliceManagerInteractionService;
        this.translatorRAPPInteractionService         = translatorRAPPInteractionService;
        this.translationService                       = translationService;
    }

    @Override
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Pair.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @RequestMapping(value = "/radToTmf/{sliceTypeId}",
            produces = { "application/json;charset=utf-8" },
            method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?>
    translateRadio(@ApiParam(value = "Slice Type ID of the Radio Resource to be translated.", required = true)
                   @PathVariable("sliceTypeId") String sliceTypeId,
                   @ApiParam(value = "Spectrum parameters of the Radio Resource to be translated.", required = true)
                   @Valid @RequestBody SpectrumParameters spectrumParameters) {

        log.info("Received request to translate & post Radio Resource with Slice Type ID {}.", sliceTypeId);

        TranslatorSliceManagerInteractionService.SliceTypeChunks sliceTypeBlueprint;
        try {
            sliceTypeBlueprint = translatorSliceManagerInteractionService.getSliceBlueprint(sliceTypeId);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MissingEntityOnSourceException e) {
            String msg = "Radio Blueprint with id " + sliceTypeId + " not found in Slice Manager.";
            log.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        TranslatorSliceManagerInteractionService.RadioChunk radioChunk =
                sliceTypeBlueprint.getSliceBlueprint().getRadioChunks().get(0);
        TranslatorSliceManagerInteractionService.ChunkTopology chunkTopology = radioChunk.getChunkTopology();
        TranslatorSliceManagerInteractionService.SelectedPhy selectedPhy =
                chunkTopology.getSelectedPhys().stream().filter(sp -> !sp.getType().equals("WIRED_ROOT"))
                        .collect(Collectors.toList()).get(0);
        String radId = selectedPhy.getId();

        TranslatorRAPPInteractionService.RAPPWrapper rappWrapper;
        try {
            rappWrapper = translatorRAPPInteractionService.postRadioRAPP(radId, spectrumParameters);
        } catch (SourceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        }

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translateAndPostRad(rappWrapper, radId, sliceTypeId);
        } catch (IOException | CatalogException | NotExistingEntityException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (MalformattedElementException e) {
            String msg = e.getMessage();
            log.info(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(msg));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
