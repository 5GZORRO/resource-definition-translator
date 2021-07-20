package it.nextworks.sol006_tmf_translator.interfaces;

import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface VnfdTranslatorInterface {

    ResponseEntity<?> translateVnfd(MultipartFile body);

    ResponseEntity<?> translateVnfdById(String vnfdId);
}
