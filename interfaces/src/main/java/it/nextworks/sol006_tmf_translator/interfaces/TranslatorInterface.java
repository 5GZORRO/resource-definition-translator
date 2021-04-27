package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.sol006_tmf_translator.information_models.sol006.Pnfd;
import it.nextworks.sol006_tmf_translator.information_models.sol006.Vnfd;
import org.springframework.http.ResponseEntity;

public interface TranslatorInterface {

    ResponseEntity<?> translateVnfd(Vnfd vnfd);

    ResponseEntity<?> translatePnfd(Pnfd pnfd);
}
