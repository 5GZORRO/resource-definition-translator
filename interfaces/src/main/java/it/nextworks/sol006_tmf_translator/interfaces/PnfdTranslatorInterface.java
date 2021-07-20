package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PnfdTranslatorInterface {

    ResponseEntity<?> translatePnfd(MultipartFile body);

    ResponseEntity<?> translatePnfdById(String pnfdId);
}
