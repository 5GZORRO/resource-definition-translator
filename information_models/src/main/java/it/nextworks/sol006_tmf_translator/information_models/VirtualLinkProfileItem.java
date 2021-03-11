package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;


/**
 * AllOfnsdDfVirtualLinkProfileItems
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VirtualLinkProfileItem extends LocalAffinityOrAntiAffinityRule  {
  @JsonProperty("flavour-id")
  private String flavourId = null;

  @JsonProperty("min-bitrate-requirements")
  private LinkBitrateRequirements minBitrateRequirements = null;

  @JsonProperty("affinity-or-anti-affinity-group")

  private List<Object> affinityOrAntiAffinityGroup = null;

  @JsonProperty("virtual-link-desc-id")
  private String virtualLinkDescId = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("max-bitrate-requirements")
  private LinkBitrateRequirements maxBitrateRequirements = null;

  public VirtualLinkProfileItem flavourId(String flavourId) {
    this.flavourId = flavourId;
    return this;
  }

  /**
   * Identifies a flavour within the VLD.
   * @return flavourId
   **/
  //@Schema(description = "Identifies a flavour within the VLD.")
  
    public String getFlavourId() {
    return flavourId;
  }

  public void setFlavourId(String flavourId) {
    this.flavourId = flavourId;
  }

  public VirtualLinkProfileItem minBitrateRequirements(LinkBitrateRequirements minBitrateRequirements) {
    this.minBitrateRequirements = minBitrateRequirements;
    return this;
  }

  /**
   * Get minBitrateRequirements
   * @return minBitrateRequirements
   **/
  //@Schema(description = "")
  

    public LinkBitrateRequirements getMinBitrateRequirements() {
    return minBitrateRequirements;
  }

  public void setMinBitrateRequirements(LinkBitrateRequirements minBitrateRequirements) {
    this.minBitrateRequirements = minBitrateRequirements;
  }

  public VirtualLinkProfileItem affinityOrAntiAffinityGroup(List<Object> affinityOrAntiAffinityGroup) {
    this.affinityOrAntiAffinityGroup = affinityOrAntiAffinityGroup;
    return this;
  }

  public VirtualLinkProfileItem addAffinityOrAntiAffinityGroupItem(Object affinityOrAntiAffinityGroupItem) {
    if (this.affinityOrAntiAffinityGroup == null) {
      this.affinityOrAntiAffinityGroup = new ArrayList<Object>();
    }
    this.affinityOrAntiAffinityGroup.add(affinityOrAntiAffinityGroupItem);
    return this;
  }

  /**
   * Identifies an affinity or anti-affinity group the VLs instantiated according to the VlProfile belong to.
   * @return affinityOrAntiAffinityGroup
   **/
  //@Schema(description = "Identifies an affinity or anti-affinity group the VLs instantiated according to the VlProfile belong to.")
  
    public List<Object> getAffinityOrAntiAffinityGroup() {
    return affinityOrAntiAffinityGroup;
  }

  public void setAffinityOrAntiAffinityGroup(List<Object> affinityOrAntiAffinityGroup) {
    this.affinityOrAntiAffinityGroup = affinityOrAntiAffinityGroup;
  }

  public VirtualLinkProfileItem virtualLinkDescId(String virtualLinkDescId) {
    this.virtualLinkDescId = virtualLinkDescId;
    return this;
  }

  /**
   * Uniquely references a VLD.
   * @return virtualLinkDescId
   **/
  //@Schema(description = "Uniquely references a VLD.")
  
    public String getVirtualLinkDescId() {
    return virtualLinkDescId;
  }

  public void setVirtualLinkDescId(String virtualLinkDescId) {
    this.virtualLinkDescId = virtualLinkDescId;
  }

  public VirtualLinkProfileItem id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Uniquely identifies this VirtualLinkProfile information element.
   * @return id
   **/
  //@Schema(description = "Uniquely identifies this VirtualLinkProfile information element.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public VirtualLinkProfileItem maxBitrateRequirements(LinkBitrateRequirements maxBitrateRequirements) {
    this.maxBitrateRequirements = maxBitrateRequirements;
    return this;
  }

  /**
   * Get maxBitrateRequirements
   * @return maxBitrateRequirements
   **/
  //@Schema(description = "")


    public LinkBitrateRequirements getMaxBitrateRequirements() {
    return maxBitrateRequirements;
  }

  public void setMaxBitrateRequirements(LinkBitrateRequirements maxBitrateRequirements) {
    this.maxBitrateRequirements = maxBitrateRequirements;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VirtualLinkProfileItem allOfnsdDfVirtualLinkProfileItems = (VirtualLinkProfileItem) o;
    return Objects.equals(this.flavourId, allOfnsdDfVirtualLinkProfileItems.flavourId) &&
        Objects.equals(this.minBitrateRequirements, allOfnsdDfVirtualLinkProfileItems.minBitrateRequirements) &&
        Objects.equals(this.affinityOrAntiAffinityGroup, allOfnsdDfVirtualLinkProfileItems.affinityOrAntiAffinityGroup) &&
        Objects.equals(this.virtualLinkDescId, allOfnsdDfVirtualLinkProfileItems.virtualLinkDescId) &&
        Objects.equals(this.id, allOfnsdDfVirtualLinkProfileItems.id) &&
        Objects.equals(this.maxBitrateRequirements, allOfnsdDfVirtualLinkProfileItems.maxBitrateRequirements) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flavourId, minBitrateRequirements, affinityOrAntiAffinityGroup, virtualLinkDescId, id, maxBitrateRequirements, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllOfnsdDfVirtualLinkProfileItems {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    flavourId: ").append(toIndentedString(flavourId)).append("\n");
    sb.append("    minBitrateRequirements: ").append(toIndentedString(minBitrateRequirements)).append("\n");
    sb.append("    affinityOrAntiAffinityGroup: ").append(toIndentedString(affinityOrAntiAffinityGroup)).append("\n");
    sb.append("    virtualLinkDescId: ").append(toIndentedString(virtualLinkDescId)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    maxBitrateRequirements: ").append(toIndentedString(maxBitrateRequirements)).append("\n");
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
