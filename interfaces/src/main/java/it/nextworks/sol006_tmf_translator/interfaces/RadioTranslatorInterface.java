package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.sol006_tmf_translator.information_models.commons.SpectrumParameters;
import org.springframework.http.ResponseEntity;

public interface RadioTranslatorInterface {
    ResponseEntity<?> translateRadio(String radId, SpectrumParameters spectrumParameters);
}
