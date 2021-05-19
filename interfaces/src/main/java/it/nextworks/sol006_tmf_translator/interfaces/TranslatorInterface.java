package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import org.springframework.http.ResponseEntity;

public interface TranslatorInterface {

    ResponseEntity<?> translateVnfd(Vnfd vnfd);

    ResponseEntity<?> translatePnfd(Pnfd pnfd);

    ResponseEntity<?> translateNsd(Nsd nsd);
}
