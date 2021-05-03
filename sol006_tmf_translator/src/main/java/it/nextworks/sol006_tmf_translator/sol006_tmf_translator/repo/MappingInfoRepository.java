package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo;

import it.nextworks.sol006_tmf_translator.information_models.persistence.MappingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MappingInfoRepository extends JpaRepository<MappingInfo, String> {
    Optional<MappingInfo> findById(String id);
}
