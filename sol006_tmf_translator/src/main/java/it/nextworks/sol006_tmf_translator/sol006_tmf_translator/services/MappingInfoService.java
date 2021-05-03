package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import it.nextworks.sol006_tmf_translator.information_models.persistence.MappingInfo;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo.MappingInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MappingInfoService {

    private static final Logger log = LoggerFactory.getLogger(MappingInfoService.class);

    private final MappingInfoRepository mappingInfoRepository;

    @Autowired
    public MappingInfoService(MappingInfoRepository mappingInfoRepository) {
        this.mappingInfoRepository = mappingInfoRepository;
    }

    public void save(MappingInfo mappingInfo) {
        log.info("Creating mapping info for descriptor with id " + mappingInfo.getDescriptorId() + ".");
        mappingInfoRepository.save(mappingInfo);
    }

    public void delete(String descriptorId) throws NotExistingEntityException {
        log.info("Deleting mapping info for descriptor with id " + descriptorId + ".");

        Optional<MappingInfo> toDelete = mappingInfoRepository.findById(descriptorId);
        if(!toDelete.isPresent()) {
            String msg = "Mapping info for descriptor with id " + descriptorId + " not found in DB.";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        mappingInfoRepository.delete(toDelete.get());

        log.info("Mapping info for descriptor with id " + descriptorId + " deleted.");
    }

    public MappingInfo get(String descriptorId) throws NotExistingEntityException {
        log.info("Retrieving mapping info for descriptor with id " + descriptorId + ".");

        Optional<MappingInfo> toRetrieve = mappingInfoRepository.findById(descriptorId);
        if(!toRetrieve.isPresent()) {
            String msg = "Mapping info for descriptor with id " + descriptorId + " not found in DB.";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        log.info("Mapping info for descriptor with id " + descriptorId + " retrieved.");

        return toRetrieve.get();
    }
}
