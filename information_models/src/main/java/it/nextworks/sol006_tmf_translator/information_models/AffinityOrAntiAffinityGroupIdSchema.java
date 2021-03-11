package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.validation.annotation.Validated;

/**
 * AffinityOrAntiAffinityGroupIdSchema
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class AffinityOrAntiAffinityGroupIdSchema   {
  @JsonProperty("affinity-or-anti-affinity-group-id_id")
  private String affinityOrAntiAffinityGroupIdId = null;

  public AffinityOrAntiAffinityGroupIdSchema affinityOrAntiAffinityGroupIdId(String affinityOrAntiAffinityGroupIdId) {
    this.affinityOrAntiAffinityGroupIdId = affinityOrAntiAffinityGroupIdId;
    return this;
  }

  /**
   * Get affinityOrAntiAffinityGroupIdId
   * @return affinityOrAntiAffinityGroupIdId
   **/
  //@Schema(description = "")
  
    public String getAffinityOrAntiAffinityGroupIdId() {
    return affinityOrAntiAffinityGroupIdId;
  }

  public void setAffinityOrAntiAffinityGroupIdId(String affinityOrAntiAffinityGroupIdId) {
    this.affinityOrAntiAffinityGroupIdId = affinityOrAntiAffinityGroupIdId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AffinityOrAntiAffinityGroupIdSchema affinityOrAntiAffinityGroupIdSchema = (AffinityOrAntiAffinityGroupIdSchema) o;
    return Objects.equals(this.affinityOrAntiAffinityGroupIdId, affinityOrAntiAffinityGroupIdSchema.affinityOrAntiAffinityGroupIdId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(affinityOrAntiAffinityGroupIdId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AffinityOrAntiAffinityGroupIdSchema {\n");
    
    sb.append("    affinityOrAntiAffinityGroupIdId: ").append(toIndentedString(affinityOrAntiAffinityGroupIdId)).append("\n");
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
