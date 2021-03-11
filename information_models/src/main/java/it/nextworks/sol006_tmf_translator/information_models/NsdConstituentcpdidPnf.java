package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * NsdConstituentcpdidPnf
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class NsdConstituentcpdidPnf   {
  @JsonProperty("pnf")
  private NsdConstituentcpdidPnfPnf pnf = null;

  public NsdConstituentcpdidPnf pnf(NsdConstituentcpdidPnfPnf pnf) {
    this.pnf = pnf;
    return this;
  }

  /**
   * Get pnf
   * @return pnf
   **/
  //@Schema(description = "")
  

    public NsdConstituentcpdidPnfPnf getPnf() {
    return pnf;
  }

  public void setPnf(NsdConstituentcpdidPnfPnf pnf) {
    this.pnf = pnf;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NsdConstituentcpdidPnf nsdConstituentcpdidPnf = (NsdConstituentcpdidPnf) o;
    return Objects.equals(this.pnf, nsdConstituentcpdidPnf.pnf);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pnf);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdConstituentcpdidPnf {\n");
    
    sb.append("    pnf: ").append(toIndentedString(pnf)).append("\n");
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
