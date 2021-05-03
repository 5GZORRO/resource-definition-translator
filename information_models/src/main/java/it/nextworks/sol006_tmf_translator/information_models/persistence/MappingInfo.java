package it.nextworks.sol006_tmf_translator.information_models.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "mapping_info")
public class MappingInfo {

    @Id
    @JsonProperty("descriptorId")
    @Column(name = "descriptor_id")
    private String descriptorId;

    @JsonProperty("candidateCatalogId")
    @Column(name = "candidate_catalog_id")
    private String candidateCatalogId;

    @JsonProperty("specificationCatalogId")
    @Column(name = "specification_catalog_id")
    private String specificationCatalogId;

    @JsonCreator
    public MappingInfo(@JsonProperty("descriptorId") String descriptorId,
                       @JsonProperty("candidateCatalogId") String candidateCatalogId,
                       @JsonProperty("specificationCatalogId") String specificationCatalogId) {
        this.descriptorId = descriptorId;
        this.candidateCatalogId = candidateCatalogId;
        this.specificationCatalogId = specificationCatalogId;
    }

    public MappingInfo() {}

    public MappingInfo descriptorId(String descriptorId) {
        this.descriptorId = descriptorId;
        return this;
    }

    public void setDescriptorId(String descriptorId) { this.descriptorId = descriptorId; }

    public String getDescriptorId() { return descriptorId; }

    public MappingInfo candidateCatalogId(String candidateCatalogId) {
        this.candidateCatalogId = candidateCatalogId;
        return this;
    }

    public void setCandidateCatalogId(String candidateCatalogId) {
        this.candidateCatalogId = candidateCatalogId;
    }

    public String getCandidateCatalogId() { return candidateCatalogId; }

    public MappingInfo specificationCatalogId(String specificationCatalogId) {
        this.specificationCatalogId = specificationCatalogId;
        return this;
    }

    public void setSpecificationCatalogId(String specificationCatalogId) {
        this.specificationCatalogId = specificationCatalogId;
    }

    public String getSpecificationCatalogId() { return specificationCatalogId; }

    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o)
            return true;

        if(o == null || getClass() != o.getClass())
            return false;

        MappingInfo mappingInfo = (MappingInfo) o;
        return Objects.equals(this.descriptorId, mappingInfo.descriptorId) &&
                Objects.equals(this.candidateCatalogId, mappingInfo.candidateCatalogId) &&
                Objects.equals(this.specificationCatalogId, mappingInfo.specificationCatalogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorId, candidateCatalogId, specificationCatalogId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MappingInfo {\n");

        sb.append("    descriptorId: ").append(toIndentedString(descriptorId)).append("\n");
        sb.append("    candidateCatalogId: ").append(toIndentedString(candidateCatalogId)).append("\n");
        sb.append("    specificationCatalogId: ").append(toIndentedString(specificationCatalogId))
                .append("\n");

        sb.append("}");

        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if(o == null)
            return "null";

        return o.toString().replace("\n", "\n    ");
    }
}
