package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import it.nextworks.sol006_tmf_translator.information_models.commons.Pair;
import it.nextworks.sol006_tmf_translator.interfaces.TranslatorInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.DescriptorAlreadyTranslatedException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnCatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.TranslationService;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecification;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceCandidate;
import it.nextworks.tmf_offering_catalog.information_models.service.ServiceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
public class TranslatorController implements TranslatorInterface {

    private final static Logger log = LoggerFactory.getLogger(TranslatorController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final TranslationService translationService;

    @Autowired
    public TranslatorController(ObjectMapper objectMapper,
                                HttpServletRequest request,
                                TranslationService translationService) {
        this.objectMapper       = objectMapper;
        this.request            = request;
        this.translationService = translationService;
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
            log.info("Web-Server: received a translation request with null vnfd.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrMsg("received a translation request with null vnfd."));
        }

        String vnfdId = vnfd.getId();
        if(vnfdId == null) {
            vnfdId = UUID.randomUUID().toString();
            vnfd.setId(vnfdId);
            log.info("Web-Server: received request to translate & post vnfd without id, generated: " + vnfdId + ".");
        }
        else
            log.info("Web-Server: received request to translate & post vnfd with id " + vnfdId + ".");

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translateVnfd(vnfd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error("Web-Server: " + msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (DescriptorAlreadyTranslatedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        log.info("Web-Server: vnfd " + vnfdId + " translated & posted.");

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
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

        if(pnfd == null) {
            log.info("Web-Server: received a translation request with null pnfd.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrMsg("received a translation request with null pnfd."));
        }

        String pnfdId = pnfd.getId();
        if(pnfdId == null) {
            pnfdId = UUID.randomUUID().toString();
            pnfd.setId(pnfdId);
            log.info("Web-Server: received request to translate & post pnfd without id, generated: " + pnfdId + ".");
        }
        else
            log.info("Web-Server: received request to translate & post pnfd with id " + pnfdId + ".");

        Pair<ResourceCandidate, ResourceSpecification> translation;
        try {
            translation = translationService.translatePnfd(pnfd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error("Web-Server: " + msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (DescriptorAlreadyTranslatedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        log.info("Web-Server: pnfd " + pnfdId + " translated & posted.");

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
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
            log.info("Web-Server: received a translation request with null nsd.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrMsg("received a translation request with null nsd."));
        }

        String nsdId = nsd.getId();
        if(nsdId == null) {
            nsdId = UUID.randomUUID().toString();
            nsd.setId(nsdId);
            log.info("Web-Server: received request to translate & post nsd without id, generated: " + nsdId + ".");
        }
        else
            log.info("Web-Server: received request to translate & post nsd with id " + nsdId + ".");

        Pair<ServiceCandidate, ServiceSpecification> translation;
        try {
            translation = translationService.translateNsd(nsd);
        } catch (IOException e) {
            String msg = e.getMessage();
            log.error("Web-Server: " + msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(msg));
        } catch (CatalogException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrMsg(e.getMessage()));
        } catch (DescriptorAlreadyTranslatedException | MissingEntityOnCatalogException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        log.info("Web-Server: nsd " + nsdId + " translated & posted.");

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }
}
