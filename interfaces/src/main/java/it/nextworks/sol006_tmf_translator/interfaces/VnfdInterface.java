package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.sol006_tmf_translator.information_models.sol006.Vnfd;
import org.springframework.http.ResponseEntity;

public interface VnfdInterface {

    ResponseEntity<?> translateVnfd(Vnfd vnfd);
}
