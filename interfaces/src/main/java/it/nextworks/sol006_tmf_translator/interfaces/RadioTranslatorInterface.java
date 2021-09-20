package it.nextworks.sol006_tmf_translator.interfaces;

import org.springframework.http.ResponseEntity;

public interface RadioTranslatorInterface {
    ResponseEntity<?> translateRadio(String radId);
}
