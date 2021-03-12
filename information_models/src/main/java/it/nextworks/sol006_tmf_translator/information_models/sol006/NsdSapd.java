package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * NsdSapd
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class NsdSapd   {
  @JsonProperty("address-assignment")
  private Boolean addressAssignment = null;

  @JsonProperty("id")
  private String id = null;

  //@JsonProperty("virtual-link")
  //private NsdCpdorvirtuallink cpdOrVirtualLink = null;

  @JsonProperty("virtual-link-desc")
  private String virtualLinkDesc = null;

  public String getVirtualLinkDesc() {
    return virtualLinkDesc;
  }

  public void setVirtualLinkDesc(String virtualLinkDesc) {
    this.virtualLinkDesc = virtualLinkDesc;
  }

  @JsonProperty("vnf")
  private VnfAssociatedCpdId vnf = null;

  @JsonProperty("ns")
  private NsAssociatedCpdId ns = null;

  @JsonProperty("pnf")
  private PnfAssociatedCpdId pnf = null;

  public PnfAssociatedCpdId getPnf() {
    return pnf;
  }

  public void setPnf(PnfAssociatedCpdId pnf) {
    this.pnf = pnf;
  }

  public NsAssociatedCpdId getNs() {
    return ns;
  }

  public void setNs(NsAssociatedCpdId ns) {
    this.ns = ns;
  }

  public VnfAssociatedCpdId getVnf() {
    return vnf;
  }

  public void setVnf(VnfAssociatedCpdId vnf) {
    this.vnf = vnf;
  }

  public NsdSapd addressAssignment(Boolean addressAssignment) {
    this.addressAssignment = addressAssignment;
    return this;
  }

  /**
   * Specify whether the SAP address assignment is under the responsibility of management and orchestration functions or not. If it is set to True, management and orchestration functions are responsible for assigning addresses to the access points instantiated from this SAPD.
   * @return addressAssignment
   **/
  //@Schema(description = "Specify whether the SAP address assignment is under the responsibility of management and orchestration functions or not. If it is set to True, management and orchestration functions are responsible for assigning addresses to the access points instantiated from this SAPD.")
  
    public Boolean isAddressAssignment() {
    return addressAssignment;
  }

  public void setAddressAssignment(Boolean addressAssignment) {
    this.addressAssignment = addressAssignment;
  }

  public NsdSapd id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of this Cpd information element.
   * @return id
   **/
  //@Schema(description = "Identifier of this Cpd information element.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /*
  public NsdSapd cpdOrVirtualLink(NsdCpdorvirtuallink cpdOrVirtualLink) {
    this.cpdOrVirtualLink = cpdOrVirtualLink;
    return this;
  }
*/
  /**
   * Get cpdOrVirtualLink
   * @return cpdOrVirtualLink
   **/
  //@Schema(description = "")

  /*

    public NsdCpdorvirtuallink getCpdOrVirtualLink() {
    return cpdOrVirtualLink;
  }

  public void setCpdOrVirtualLink(NsdCpdorvirtuallink cpdOrVirtualLink) {
    this.cpdOrVirtualLink = cpdOrVirtualLink;
  }
*/

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NsdSapd nsdSapd = (NsdSapd) o;
    return Objects.equals(this.addressAssignment, nsdSapd.addressAssignment) &&
        Objects.equals(this.id, nsdSapd.id);
        //Objects.equals(this.cpdOrVirtualLink, nsdSapd.cpdOrVirtualLink);
  }

  @Override
  public int hashCode() {
    //return Objects.hash(addressAssignment, id, cpdOrVirtualLink);
    return Objects.hash(addressAssignment, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdSapd {\n");
    
    sb.append("    addressAssignment: ").append(toIndentedString(addressAssignment)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    //sb.append("    cpdOrVirtualLink: ").append(toIndentedString(cpdOrVirtualLink)).append("\n");
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
