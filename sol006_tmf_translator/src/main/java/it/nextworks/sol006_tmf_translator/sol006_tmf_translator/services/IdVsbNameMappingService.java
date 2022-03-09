package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import it.nextworks.sol006_tmf_translator.information_models.persistence.IdVsbNameMapping;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.IdVsbNameMappingExistsException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.NotExistingEntityException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo.IdVsbNameMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IdVsbNameMappingService {

    private static final Logger log = LoggerFactory.getLogger(IdVsbNameMappingService.class);

    private final IdVsbNameMappingRepository idVsbNameMappingRepository;

    @Autowired
    public IdVsbNameMappingService(IdVsbNameMappingRepository idVsbNameMappingRepository) {
        this.idVsbNameMappingRepository = idVsbNameMappingRepository;
    }

    public void save(IdVsbNameMapping idVsbNameMapping) throws IdVsbNameMappingExistsException {
        String id = idVsbNameMapping.getId();
        String vsbName = idVsbNameMapping.getVsbName();

        Optional<IdVsbNameMapping> optionalIdVsbNameMapping =
                idVsbNameMappingRepository.findById(id);
        if(optionalIdVsbNameMapping.isPresent()) {
            String msg = "VSB Name mapping already exists for ID " + id + ".";
            log.info(msg);
            throw new IdVsbNameMappingExistsException(msg);
        }

        log.info("Saving ID {} and VSB Name {} mapping.", id, vsbName);

        idVsbNameMappingRepository.save(idVsbNameMapping);

        log.info("ID {} and VSB Name {} mapping saved.", id, vsbName);
    }

    public List<IdVsbNameMapping> list() {
        log.info("Retrieving ID - VSB Name mappings.");

        List<IdVsbNameMapping> idVsbNameMappings = idVsbNameMappingRepository.findAll();

        log.info("ID - VSB Name mappings retrieved.");

        return idVsbNameMappings;
    }

    public IdVsbNameMapping getById(String id) throws NotExistingEntityException {
        log.info("Retrieving ID - VSB Name mapping for ID {}.", id);

        Optional<IdVsbNameMapping> optionalIdVsbNameMapping =
                idVsbNameMappingRepository.findById(id);
        if(!optionalIdVsbNameMapping.isPresent()) {
            String msg = "VSB Name mapping does not exist for ID " + id + ".";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        log.info("Mapping for ID {} retrieved.", id);

        return optionalIdVsbNameMapping.get();
    }

    public List<IdVsbNameMapping> getByVsbName(String vsbName) {
        log.info("Retrieving ID - VSB Name mappings using VSB Name {}.", vsbName);

        List<IdVsbNameMapping> idVsbNameMappings = idVsbNameMappingRepository.findByVsbName(vsbName);

        log.info("ID - VSB Name mappings retrieved with VSB Name {}.", vsbName);

        return idVsbNameMappings;
    }

    public void delete(IdVsbNameMapping idVsbNameMapping) throws NotExistingEntityException {
        String id = idVsbNameMapping.getId();
        String vsbName = idVsbNameMapping.getVsbName();

        Optional<IdVsbNameMapping> optionalIdVsbNameMapping =
                idVsbNameMappingRepository.findByIdAndVsbName(id, vsbName);
        if(!optionalIdVsbNameMapping.isPresent()) {
            String msg = "ID " + id + " and VSB Name " + vsbName + " mapping not Found.";
            log.info(msg);
            throw new NotExistingEntityException(msg);
        }

        log.info("Removing mapping for ID {} and VSB Name {}.", id, vsbName);

        idVsbNameMappingRepository.delete(idVsbNameMapping);

        log.info("Mapping for ID {} and VSB Name {} removed.", id, vsbName);
    }
}
