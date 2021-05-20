package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import org.springframework.http.ResponseEntity;

public interface PnfdTranslatorInterface {

    ResponseEntity<?> translatePnfd(Pnfd pnfd);

    ResponseEntity<?> translatePnfdById(String pnfdId);
}
