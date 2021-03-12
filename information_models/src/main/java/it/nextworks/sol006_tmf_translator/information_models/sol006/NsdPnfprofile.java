package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * NsdPnfprofile
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class NsdPnfprofile   {
  @JsonProperty("pnfd-id")
  private String pnfdId = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("virtual-link-connectivity")

  private List<VirtualLinkConnectivitySchema> virtualLinkConnectivity = null;

  public NsdPnfprofile pnfdId(String pnfdId) {
    this.pnfdId = pnfdId;
    return this;
  }

  /**
   * References a PNFD.
   * @return pnfdId
   **/
  //@Schema(description = "References a PNFD.")
  
    public String getPnfdId() {
    return pnfdId;
  }

  public void setPnfdId(String pnfdId) {
    this.pnfdId = pnfdId;
  }

  public NsdPnfprofile id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of this PnfProfile information element. It uniquely identifies a PnfProfile.
   * @return id
   **/
  //@Schema(description = "Identifier of this PnfProfile information element. It uniquely identifies a PnfProfile.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NsdPnfprofile virtualLinkConnectivity(List<VirtualLinkConnectivitySchema> virtualLinkConnectivity) {
    this.virtualLinkConnectivity = virtualLinkConnectivity;
    return this;
  }

  public NsdPnfprofile addVirtualLinkConnectivityItem(VirtualLinkConnectivitySchema virtualLinkConnectivityItem) {
    if (this.virtualLinkConnectivity == null) {
      this.virtualLinkConnectivity = new ArrayList<VirtualLinkConnectivitySchema>();
    }
    this.virtualLinkConnectivity.add(virtualLinkConnectivityItem);
    return this;
  }

  /**
   * Defines the connection information of the PNF, it contains connection relationship between a PNF connection point and a NS Virtual Link.
   * @return virtualLinkConnectivity
   **/
  //@Schema(description = "Defines the connection information of the PNF, it contains connection relationship between a PNF connection point and a NS Virtual Link.")

    public List<VirtualLinkConnectivitySchema> getVirtualLinkConnectivity() {
    return virtualLinkConnectivity;
  }

  public void setVirtualLinkConnectivity(List<VirtualLinkConnectivitySchema> virtualLinkConnectivity) {
    this.virtualLinkConnectivity = virtualLinkConnectivity;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NsdPnfprofile nsdPnfprofile = (NsdPnfprofile) o;
    return Objects.equals(this.pnfdId, nsdPnfprofile.pnfdId) &&
        Objects.equals(this.id, nsdPnfprofile.id) &&
        Objects.equals(this.virtualLinkConnectivity, nsdPnfprofile.virtualLinkConnectivity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pnfdId, id, virtualLinkConnectivity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdPnfprofile {\n");
    
    sb.append("    pnfdId: ").append(toIndentedString(pnfdId)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    virtualLinkConnectivity: ").append(toIndentedString(virtualLinkConnectivity)).append("\n");
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
