package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import it.nextworks.sol006_tmf_translator.information_models.persistence.ResourceMappingInfo;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo.ResourceMappingInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResourceMappingInfoService {

    private static final Logger log = LoggerFactory.getLogger(ResourceMappingInfoService.class);

    private ResourceMappingInfoRepository resourceMappingInfoRepository;

    @Autowired
    public ResourceMappingInfoService(ResourceMappingInfoRepository resourceMappingInfoRepository) {
        this.resourceMappingInfoRepository = resourceMappingInfoRepository;
    }

    public void save(ResourceMappingInfo resourceMappingInfo) {
        log.info("Creating mapping info for resource with descriptor id " + resourceMappingInfo.getDescriptorId() + ".");
        resourceMappingInfoRepository.save(resourceMappingInfo);
    }

    public void delete(String descriptorId) throws NotExistingEntityException {
        log.info("Deleting mapping info for resource with descriptor id " + descriptorId + ".");

        Optional<ResourceMappingInfo> toDelete = resourceMappingInfoRepository.findById(descriptorId);
        if(!toDelete.isPresent()) {
            String msg = "Mapping info for resource with descriptor id " + descriptorId + " not found in DB.";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        resourceMappingInfoRepository.delete(toDelete.get());

        log.info("Mapping info for resource with descriptor id " + descriptorId + " deleted.");
    }

    public ResourceMappingInfo get(String descriptorId) throws NotExistingEntityException {
        log.info("Retrieving mapping info for resource with descriptor id " + descriptorId + ".");

        Optional<ResourceMappingInfo> toRetrieve = resourceMappingInfoRepository.findById(descriptorId);
        if(!toRetrieve.isPresent()) {
            String msg = "Mapping info for resource with descriptor id " + descriptorId + " not found in DB.";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        log.info("Mapping info for resource with descriptor id " + descriptorId + " retrieved.");

        return toRetrieve.get();
    }
}
