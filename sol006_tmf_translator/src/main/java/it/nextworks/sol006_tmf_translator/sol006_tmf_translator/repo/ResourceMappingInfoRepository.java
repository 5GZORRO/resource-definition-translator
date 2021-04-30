package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo;

import it.nextworks.sol006_tmf_translator.information_models.persistence.ResourceMappingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceMappingInfoRepository extends JpaRepository<ResourceMappingInfo, String> {
    Optional<ResourceMappingInfo> findById(String id);
}
