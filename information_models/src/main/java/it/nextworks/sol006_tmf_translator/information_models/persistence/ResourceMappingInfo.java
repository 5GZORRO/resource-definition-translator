package it.nextworks.sol006_tmf_translator.information_models.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "resource_mapping_info")
public class ResourceMappingInfo {

    @Id
    @JsonProperty("descriptorId")
    @Column(name = "descriptor_id")
    private String descriptorId;

    @JsonProperty("resourceCandidateCatalogId")
    @Column(name = "resource_candidate_catalog_id")
    private String resourceCandidateCatalogId;

    @JsonProperty("resourceSpecificationCatalogId")
    @Column(name = "resource_specification_catalog_id")
    private String resourceSpecificationCatalogId;

    @JsonCreator
    public ResourceMappingInfo(@JsonProperty("descriptorId") String descriptorId,
                               @JsonProperty("resourceCandidateCatalogId") String resourceCandidateCatalogId,
                               @JsonProperty("resourceSpecificationCatalogId") String resourceSpecificationCatalogId) {
        this.descriptorId = descriptorId;
        this.resourceCandidateCatalogId = resourceCandidateCatalogId;
        this.resourceSpecificationCatalogId = resourceSpecificationCatalogId;
    }

    public ResourceMappingInfo() {}

    public ResourceMappingInfo descriptorId(String descriptorId) {
        this.descriptorId = descriptorId;
        return this;
    }

    public void setDescriptorId(String descriptorId) { this.descriptorId = descriptorId; }

    public String getDescriptorId() { return descriptorId; }

    public ResourceMappingInfo resourceCandidateCatalogId(String resourceCandidateCatalogId) {
        this.resourceCandidateCatalogId = resourceCandidateCatalogId;
        return this;
    }

    public void setResourceCandidateCatalogId(String resourceCandidateCatalogId) {
        this.resourceCandidateCatalogId = resourceCandidateCatalogId;
    }

    public String getResourceCandidateCatalogId() { return resourceCandidateCatalogId; }

    public ResourceMappingInfo resourceSpecificationCatalogId(String resourceSpecificationCatalogId) {
        this.resourceSpecificationCatalogId = resourceSpecificationCatalogId;
        return this;
    }

    public void setResourceSpecificationCatalogId(String resourceSpecificationCatalogId) {
        this.resourceSpecificationCatalogId = resourceSpecificationCatalogId;
    }

    public String getResourceSpecificationCatalogId() { return resourceSpecificationCatalogId; }

    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o)
            return true;

        if(o == null || getClass() != o.getClass())
            return false;

        ResourceMappingInfo resourceMappingInfo = (ResourceMappingInfo) o;
        return Objects.equals(this.descriptorId, resourceMappingInfo.descriptorId) &&
                Objects.equals(this.resourceCandidateCatalogId, resourceMappingInfo.resourceCandidateCatalogId) &&
                Objects.equals(this.resourceSpecificationCatalogId, resourceMappingInfo.resourceSpecificationCatalogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorId, resourceCandidateCatalogId, resourceSpecificationCatalogId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceMappingInfo {\n");

        sb.append("    descriptorId: ").append(toIndentedString(descriptorId)).append("\n");
        sb.append("    resourceCandidateCatalogId: ").append(toIndentedString(resourceCandidateCatalogId)).append("\n");
        sb.append("    resourceSpecificationCatalogId: ").append(toIndentedString(resourceSpecificationCatalogId))
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
