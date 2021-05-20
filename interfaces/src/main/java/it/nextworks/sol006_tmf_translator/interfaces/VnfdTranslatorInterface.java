package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import org.springframework.http.ResponseEntity;

public interface VnfdTranslatorInterface {

    ResponseEntity<?> translateVnfd(Vnfd vnfd);

    ResponseEntity<?> translateVnfdById(String vnfdId);
}
