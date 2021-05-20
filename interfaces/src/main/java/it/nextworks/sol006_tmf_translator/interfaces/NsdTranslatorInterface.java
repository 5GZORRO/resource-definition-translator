package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import org.springframework.http.ResponseEntity;

public interface NsdTranslatorInterface {

    ResponseEntity<?> translateNsd(Nsd nsd);

    ResponseEntity<?> translateNsdById(String nsdId);
}
