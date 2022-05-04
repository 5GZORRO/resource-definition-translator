package it.nextworks.sol006_tmf_translator.interfaces;

import org.springframework.http.ResponseEntity;

public interface CloudTranslatorInterface {
    ResponseEntity<?> translateCloud(String sliceTypeId);
}
