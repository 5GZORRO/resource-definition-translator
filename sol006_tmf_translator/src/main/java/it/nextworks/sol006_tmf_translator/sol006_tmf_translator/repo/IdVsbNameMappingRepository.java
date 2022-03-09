package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.repo;

import it.nextworks.sol006_tmf_translator.information_models.persistence.IdVsbNameMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface IdVsbNameMappingRepository extends JpaRepository<IdVsbNameMapping, String> {
    Optional<IdVsbNameMapping> findByIdAndVsbName(@NonNull String id, @NonNull String vsbName);
    List<IdVsbNameMapping> findByVsbName(@NonNull String vsbName);
}
