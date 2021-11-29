package it.nextworks.sol006_tmf_translator.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface NsdTranslatorInterface {

    ResponseEntity<?> translateNsd(MultipartFile body);
    ResponseEntity<?> translateNsdById(String nsdId, String serviceType);
}
