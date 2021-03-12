package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * VnfdAspectdeltadetailsVirtuallinkbitratedelta
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdAspectdeltadetailsVirtuallinkbitratedelta   {
  @JsonProperty("bit-rate-requirements")
  private VnfdAspectdeltadetailsBitraterequirements bitRateRequirements = null;

  @JsonProperty("id")
  private String id = null;

  public VnfdAspectdeltadetailsVirtuallinkbitratedelta bitRateRequirements(VnfdAspectdeltadetailsBitraterequirements bitRateRequirements) {
    this.bitRateRequirements = bitRateRequirements;
    return this;
  }

  /**
   * Get bitRateRequirements
   * @return bitRateRequirements
   **/
  //@Schema(description = "")
  

    public VnfdAspectdeltadetailsBitraterequirements getBitRateRequirements() {
    return bitRateRequirements;
  }

  public void setBitRateRequirements(VnfdAspectdeltadetailsBitraterequirements bitRateRequirements) {
    this.bitRateRequirements = bitRateRequirements;
  }

  public VnfdAspectdeltadetailsVirtuallinkbitratedelta id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Uniquely identifies a VnfVirtualLinkDesc.
   * @return id
   **/
  //@Schema(description = "Uniquely identifies a VnfVirtualLinkDesc.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VnfdAspectdeltadetailsVirtuallinkbitratedelta vnfdAspectdeltadetailsVirtuallinkbitratedelta = (VnfdAspectdeltadetailsVirtuallinkbitratedelta) o;
    return Objects.equals(this.bitRateRequirements, vnfdAspectdeltadetailsVirtuallinkbitratedelta.bitRateRequirements) &&
        Objects.equals(this.id, vnfdAspectdeltadetailsVirtuallinkbitratedelta.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bitRateRequirements, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfdAspectdeltadetailsVirtuallinkbitratedelta {\n");
    
    sb.append("    bitRateRequirements: ").append(toIndentedString(bitRateRequirements)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
