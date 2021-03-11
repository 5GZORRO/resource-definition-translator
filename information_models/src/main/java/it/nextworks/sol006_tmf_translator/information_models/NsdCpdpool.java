package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * NsdCpdpool
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class NsdCpdpool   {
  @JsonProperty("constituent-cpd-id")
  private NsdConstituentcpdid constituentCpdId = null;

  @JsonProperty("constituent-base-element-id")
  private NsdConstituentBaseElementId constituentBaseElementId = null;

  @JsonProperty("id")
  private String id = null;

  public NsdCpdpool constituentCpdId(NsdConstituentcpdid constituentCpdId) {
    this.constituentCpdId = constituentCpdId;
    return this;
  }

  /**
   * Get constituentCpdId
   * @return constituentCpdId
   **/

  

    public NsdConstituentcpdid getConstituentCpdId() {
    return constituentCpdId;
  }

  public void setConstituentCpdId(NsdConstituentcpdid constituentCpdId) {
    this.constituentCpdId = constituentCpdId;
  }

  public NsdCpdpool constituentBaseElementId(NsdConstituentBaseElementId constituentBaseElementId) {
    this.constituentBaseElementId = constituentBaseElementId;
    return this;
  }

  /**
   * Get constituentBaseElementId
   * @return constituentBaseElementId
   **/

  

    public NsdConstituentBaseElementId getConstituentBaseElementId() {
    return constituentBaseElementId;
  }

  public void setConstituentBaseElementId(NsdConstituentBaseElementId constituentBaseElementId) {
    this.constituentBaseElementId = constituentBaseElementId;
  }

  public NsdCpdpool id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/

  
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
    NsdCpdpool nsdCpdpool = (NsdCpdpool) o;
    return Objects.equals(this.constituentCpdId, nsdCpdpool.constituentCpdId) &&
        Objects.equals(this.constituentBaseElementId, nsdCpdpool.constituentBaseElementId) &&
        Objects.equals(this.id, nsdCpdpool.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(constituentCpdId, constituentBaseElementId, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdCpdpool {\n");
    
    sb.append("    constituentCpdId: ").append(toIndentedString(constituentCpdId)).append("\n");
    sb.append("    constituentBaseElementId: ").append(toIndentedString(constituentBaseElementId)).append("\n");
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
