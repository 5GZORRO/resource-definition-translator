package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.sol006_tmf_translator.information_models.persistence.IdVsbNameMapping;
import org.springframework.http.ResponseEntity;

public interface IdVsbNameMappingInterface {
    ResponseEntity<?> save(IdVsbNameMapping idVsbNameMapping);
    ResponseEntity<?> list();
    ResponseEntity<?> getById(String id);
    ResponseEntity<?> getByVsbName(String vsbName);
    ResponseEntity<?> delete(IdVsbNameMapping idVsbNameMapping);
}
