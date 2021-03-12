package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;



/**
 * VnfdAspectdeltadetailsDeltas
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdAspectdeltadetailsDeltas   {
  @JsonProperty("virtual-link-bit-rate-delta")

  private List<VnfdAspectdeltadetailsVirtuallinkbitratedelta> virtualLinkBitRateDelta = null;

  @JsonProperty("vdu-delta")

  private List<VnfdAspectdeltadetailsVdudelta> vduDelta = null;

  @JsonProperty("id")
  private String id = null;

  public VnfdAspectdeltadetailsDeltas virtualLinkBitRateDelta(List<VnfdAspectdeltadetailsVirtuallinkbitratedelta> virtualLinkBitRateDelta) {
    this.virtualLinkBitRateDelta = virtualLinkBitRateDelta;
    return this;
  }

  public VnfdAspectdeltadetailsDeltas addVirtualLinkBitRateDeltaItem(VnfdAspectdeltadetailsVirtuallinkbitratedelta virtualLinkBitRateDeltaItem) {
    if (this.virtualLinkBitRateDelta == null) {
      this.virtualLinkBitRateDelta = new ArrayList<VnfdAspectdeltadetailsVirtuallinkbitratedelta>();
    }
    this.virtualLinkBitRateDelta.add(virtualLinkBitRateDeltaItem);
    return this;
  }

  /**
   * The bitrate to be added or removed to virtual links created from particular virtual link descriptors.
   * @return virtualLinkBitRateDelta
   **/
  //@Schema(description = "The bitrate to be added or removed to virtual links created from particular virtual link descriptors.")

    public List<VnfdAspectdeltadetailsVirtuallinkbitratedelta> getVirtualLinkBitRateDelta() {
    return virtualLinkBitRateDelta;
  }

  public void setVirtualLinkBitRateDelta(List<VnfdAspectdeltadetailsVirtuallinkbitratedelta> virtualLinkBitRateDelta) {
    this.virtualLinkBitRateDelta = virtualLinkBitRateDelta;
  }

  public VnfdAspectdeltadetailsDeltas vduDelta(List<VnfdAspectdeltadetailsVdudelta> vduDelta) {
    this.vduDelta = vduDelta;
    return this;
  }

  public VnfdAspectdeltadetailsDeltas addVduDeltaItem(VnfdAspectdeltadetailsVdudelta vduDeltaItem) {
    if (this.vduDelta == null) {
      this.vduDelta = new ArrayList<VnfdAspectdeltadetailsVdudelta>();
    }
    this.vduDelta.add(vduDeltaItem);
    return this;
  }

  /**
   * The number of VNFC instances based on particular VDUs to be created or removed.
   * @return vduDelta
   **/
  //@Schema(description = "The number of VNFC instances based on particular VDUs to be created or removed.")

    public List<VnfdAspectdeltadetailsVdudelta> getVduDelta() {
    return vduDelta;
  }

  public void setVduDelta(List<VnfdAspectdeltadetailsVdudelta> vduDelta) {
    this.vduDelta = vduDelta;
  }

  public VnfdAspectdeltadetailsDeltas id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of this scaling delta.
   * @return id
   **/
  //@Schema(description = "Identifier of this scaling delta.")
  
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
    VnfdAspectdeltadetailsDeltas vnfdAspectdeltadetailsDeltas = (VnfdAspectdeltadetailsDeltas) o;
    return Objects.equals(this.virtualLinkBitRateDelta, vnfdAspectdeltadetailsDeltas.virtualLinkBitRateDelta) &&
        Objects.equals(this.vduDelta, vnfdAspectdeltadetailsDeltas.vduDelta) &&
        Objects.equals(this.id, vnfdAspectdeltadetailsDeltas.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(virtualLinkBitRateDelta, vduDelta, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfdAspectdeltadetailsDeltas {\n");
    
    sb.append("    virtualLinkBitRateDelta: ").append(toIndentedString(virtualLinkBitRateDelta)).append("\n");
    sb.append("    vduDelta: ").append(toIndentedString(vduDelta)).append("\n");
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
