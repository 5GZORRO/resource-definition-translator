package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;



/**
 * VirtualNetworkInterfaceRequirementSchema
 */


public class VirtualNetworkInterfaceRequirementSchema   {
  @JsonProperty("network-interface-requirements")

  private List<VnfdVdustoragerequirements> networkInterfaceRequirements = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("support-mandatory")
  private Boolean supportMandatory = null;

  @JsonProperty("description")
  private String description = null;

  public VirtualNetworkInterfaceRequirementSchema networkInterfaceRequirements(List<VnfdVdustoragerequirements> networkInterfaceRequirements) {
    this.networkInterfaceRequirements = networkInterfaceRequirements;
    return this;
  }

  public VirtualNetworkInterfaceRequirementSchema addNetworkInterfaceRequirementsItem(VnfdVdustoragerequirements networkInterfaceRequirementsItem) {
    if (this.networkInterfaceRequirements == null) {
      this.networkInterfaceRequirements = new ArrayList<VnfdVdustoragerequirements>();
    }
    this.networkInterfaceRequirements.add(networkInterfaceRequirementsItem);
    return this;
  }

  /**
   * The network interface requirements. An element from an array of key-value pairs that articulate the network interface deployment requirements.
   * @return networkInterfaceRequirements
   **/

    public List<VnfdVdustoragerequirements> getNetworkInterfaceRequirements() {
    return networkInterfaceRequirements;
  }

  public void setNetworkInterfaceRequirements(List<VnfdVdustoragerequirements> networkInterfaceRequirements) {
    this.networkInterfaceRequirements = networkInterfaceRequirements;
  }

  public VirtualNetworkInterfaceRequirementSchema name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Provides a human readable name for the requirement.
   * @return name
   **/

  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VirtualNetworkInterfaceRequirementSchema supportMandatory(Boolean supportMandatory) {
    this.supportMandatory = supportMandatory;
    return this;
  }

  /**
   * Indicates whether fulfilling the constraint is mandatory (true) for successful operation or desirable (false).
   * @return supportMandatory
   **/

  
    public Boolean isSupportMandatory() {
    return supportMandatory;
  }

  public void setSupportMandatory(Boolean supportMandatory) {
    this.supportMandatory = supportMandatory;
  }

  public VirtualNetworkInterfaceRequirementSchema description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Provides a human readable description of the requirement.
   * @return description
   **/

  
    public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VirtualNetworkInterfaceRequirementSchema virtualNetworkInterfaceRequirementSchema = (VirtualNetworkInterfaceRequirementSchema) o;
    return Objects.equals(this.networkInterfaceRequirements, virtualNetworkInterfaceRequirementSchema.networkInterfaceRequirements) &&
        Objects.equals(this.name, virtualNetworkInterfaceRequirementSchema.name) &&
        Objects.equals(this.supportMandatory, virtualNetworkInterfaceRequirementSchema.supportMandatory) &&
        Objects.equals(this.description, virtualNetworkInterfaceRequirementSchema.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(networkInterfaceRequirements, name, supportMandatory, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VirtualNetworkInterfaceRequirementSchema {\n");
    
    sb.append("    networkInterfaceRequirements: ").append(toIndentedString(networkInterfaceRequirements)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    supportMandatory: ").append(toIndentedString(supportMandatory)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
