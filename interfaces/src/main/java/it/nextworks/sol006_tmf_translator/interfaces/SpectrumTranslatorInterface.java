package it.nextworks.sol006_tmf_translator.interfaces;

import org.springframework.http.ResponseEntity;

public interface SpectrumTranslatorInterface {
    ResponseEntity<?> translateSpectrum(String spcId);
}
