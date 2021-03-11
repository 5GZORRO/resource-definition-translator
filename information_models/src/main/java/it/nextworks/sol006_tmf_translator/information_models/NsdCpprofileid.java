package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;


/**
 * NsdCpprofileid
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class NsdCpprofileid   {
  @JsonProperty("constituent-profile-elements")

  private List<NsdConstituentprofileelements> constituentProfileElements = null;

  @JsonProperty("id")
  private String id = null;

  public NsdCpprofileid constituentProfileElements(List<NsdConstituentprofileelements> constituentProfileElements) {
    this.constituentProfileElements = constituentProfileElements;
    return this;
  }

  public NsdCpprofileid addConstituentProfileElementsItem(NsdConstituentprofileelements constituentProfileElementsItem) {
    if (this.constituentProfileElements == null) {
      this.constituentProfileElements = new ArrayList<NsdConstituentprofileelements>();
    }
    this.constituentProfileElements.add(constituentProfileElementsItem);
    return this;
  }

  /**
   * Specifies the constituents of the CpProfile.
   * @return constituentProfileElements
   **/
  //@Schema(description = "Specifies the constituents of the CpProfile.")

    public List<NsdConstituentprofileelements> getConstituentProfileElements() {
    return constituentProfileElements;
  }

  public void setConstituentProfileElements(List<NsdConstituentprofileelements> constituentProfileElements) {
    this.constituentProfileElements = constituentProfileElements;
  }

  public NsdCpprofileid id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of this CpProfile information element. It uniquely identifies a CpProfile.
   * @return id
   **/
  //@Schema(description = "Identifier of this CpProfile information element. It uniquely identifies a CpProfile.")
  
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
    NsdCpprofileid nsdCpprofileid = (NsdCpprofileid) o;
    return Objects.equals(this.constituentProfileElements, nsdCpprofileid.constituentProfileElements) &&
        Objects.equals(this.id, nsdCpprofileid.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(constituentProfileElements, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdCpprofileid {\n");
    
    sb.append("    constituentProfileElements: ").append(toIndentedString(constituentProfileElements)).append("\n");
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
