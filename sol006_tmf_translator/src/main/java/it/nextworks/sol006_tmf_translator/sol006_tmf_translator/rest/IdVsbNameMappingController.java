package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.sol006_tmf_translator.information_models.persistence.IdVsbNameMapping;
import it.nextworks.sol006_tmf_translator.interfaces.IdVsbNameMappingInterface;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.IdVsbNameMappingExistsException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services.IdVsbNameMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/idVsbName")
public class IdVsbNameMappingController implements IdVsbNameMappingInterface {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final IdVsbNameMappingService idVsbNameMappingService;

    @Autowired
    public IdVsbNameMappingController(ObjectMapper objectMapper,
                                      HttpServletRequest request,
                                      IdVsbNameMappingService idVsbNameMappingService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.idVsbNameMappingService = idVsbNameMappingService;
    }

    @Override
    @ApiOperation(value = "Create ID - VSB Name mapping.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = { "application/json;charset=utf-8" })
    public ResponseEntity<?>
    save(@ApiParam(value = "ID - VSB Name mapping.", required = true)
         @RequestBody @Valid IdVsbNameMapping idVsbNameMapping) {
        try {
            idVsbNameMappingService.save(idVsbNameMapping);
        } catch (IdVsbNameMappingExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @ApiOperation(value = "List ID - VSB Name mappings.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = IdVsbNameMapping.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @GetMapping(produces = { "application/json;charset=utf-8" })
    public ResponseEntity<?> list() {
        return ResponseEntity.status(HttpStatus.OK).body(idVsbNameMappingService.list());
    }

    @Override
    @ApiOperation(value = "Get ID - VSB Name mapping by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = IdVsbNameMapping.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @GetMapping(value = "/getById/{id}", produces = { "application/json;charset=utf-8" })
    public ResponseEntity<?>
    getById(@ApiParam(value = "ID to filter ID - VSB Name mappings.", required = true)
            @PathVariable String id) {
        IdVsbNameMapping idVsbNameMapping;
        try {
            idVsbNameMapping = idVsbNameMappingService.getById(id);
        } catch (NotExistingEntityException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(idVsbNameMapping);
    }

    @Override
    @ApiOperation(value = "Get ID - VSB Names mappings by VSB Name.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = IdVsbNameMapping.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @GetMapping(value = "/getByVsbName/{vsbName}", produces = { "application/json;charset=utf-8" })
    public ResponseEntity<?>
    getByVsbName(@ApiParam(value = "VSB Name to filter ID - VSB Name mappings.", required = true)
                 @PathVariable String vsbName) {
        return ResponseEntity.status(HttpStatus.OK).body(idVsbNameMappingService.getByVsbName(vsbName));
    }

    @Override
    @ApiOperation(value = "Remove ID - VSB Name mapping.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted"),
            @ApiResponse(code = 404, message = "Not Found", response = ErrMsg.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrMsg.class)
    })
    @DeleteMapping(consumes = { "application/json;charset=utf-8" })
    public ResponseEntity<?>
    delete(@ApiParam(value = "ID - VSB Name mapping to be removed.", required = true)
           @RequestBody @Valid IdVsbNameMapping idVsbNameMapping) {
        try {
            idVsbNameMappingService.delete(idVsbNameMapping);
        } catch (NotExistingEntityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrMsg(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
