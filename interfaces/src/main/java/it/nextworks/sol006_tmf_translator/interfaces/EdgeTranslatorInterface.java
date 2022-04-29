package it.nextworks.sol006_tmf_translator.interfaces;

import org.springframework.http.ResponseEntity;

public interface EdgeTranslatorInterface {
    ResponseEntity<?> translateEdge(String sliceTypeId);
}
