package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * AllOfvnfdDfVduProfileItems
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdDfVduProfileItem extends LocalAffinityOrAntiAffinityRule  {
  @JsonProperty("min-number-of-instances")
  private String minNumberOfInstances = null;

  @JsonProperty("affinity-or-anti-affinity-group")

  private List<Object> affinityOrAntiAffinityGroup = null;

  @JsonProperty("max-number-of-instances")
  private String maxNumberOfInstances = null;

  @JsonProperty("id")
  private String id = null;

  public VnfdDfVduProfileItem minNumberOfInstances(String minNumberOfInstances) {
    this.minNumberOfInstances = minNumberOfInstances;
    return this;
  }

  /**
   * Minimum number of instances of the VNFC based on this VDU that is permitted to exist for this flavour.
   * @return minNumberOfInstances
   **/
  //@Schema(description = "Minimum number of instances of the VNFC based on this VDU that is permitted to exist for this flavour.")
  
    public String getMinNumberOfInstances() {
    return minNumberOfInstances;
  }

  public void setMinNumberOfInstances(String minNumberOfInstances) {
    this.minNumberOfInstances = minNumberOfInstances;
  }

  public VnfdDfVduProfileItem affinityOrAntiAffinityGroup(List<Object> affinityOrAntiAffinityGroup) {
    this.affinityOrAntiAffinityGroup = affinityOrAntiAffinityGroup;
    return this;
  }

  public VnfdDfVduProfileItem addAffinityOrAntiAffinityGroupItem(Object affinityOrAntiAffinityGroupItem) {
    if (this.affinityOrAntiAffinityGroup == null) {
      this.affinityOrAntiAffinityGroup = new ArrayList<Object>();
    }
    this.affinityOrAntiAffinityGroup.add(affinityOrAntiAffinityGroupItem);
    return this;
  }

  /**
   * Identifier(s) of the affinity or anti-affinity group(s) the VDU belongs to.
   * @return affinityOrAntiAffinityGroup
   **/
  //@Schema(description = "Identifier(s) of the affinity or anti-affinity group(s) the VDU belongs to.")
  
    public List<Object> getAffinityOrAntiAffinityGroup() {
    return affinityOrAntiAffinityGroup;
  }

  public void setAffinityOrAntiAffinityGroup(List<Object> affinityOrAntiAffinityGroup) {
    this.affinityOrAntiAffinityGroup = affinityOrAntiAffinityGroup;
  }

  public VnfdDfVduProfileItem maxNumberOfInstances(String maxNumberOfInstances) {
    this.maxNumberOfInstances = maxNumberOfInstances;
    return this;
  }

  /**
   * Get maxNumberOfInstances
   * @return maxNumberOfInstances
   **/
  //@Schema(description = "")
  
    public String getMaxNumberOfInstances() {
    return maxNumberOfInstances;
  }

  public void setMaxNumberOfInstances(String maxNumberOfInstances) {
    this.maxNumberOfInstances = maxNumberOfInstances;
  }

  public VnfdDfVduProfileItem id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  //@Schema(description = "")
  
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
    VnfdDfVduProfileItem allOfvnfdDfVduProfileItems = (VnfdDfVduProfileItem) o;
    return Objects.equals(this.minNumberOfInstances, allOfvnfdDfVduProfileItems.minNumberOfInstances) &&
        Objects.equals(this.affinityOrAntiAffinityGroup, allOfvnfdDfVduProfileItems.affinityOrAntiAffinityGroup) &&
        Objects.equals(this.maxNumberOfInstances, allOfvnfdDfVduProfileItems.maxNumberOfInstances) &&
        Objects.equals(this.id, allOfvnfdDfVduProfileItems.id) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minNumberOfInstances, affinityOrAntiAffinityGroup, maxNumberOfInstances, id, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllOfvnfdDfVduProfileItems {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    minNumberOfInstances: ").append(toIndentedString(minNumberOfInstances)).append("\n");
    sb.append("    affinityOrAntiAffinityGroup: ").append(toIndentedString(affinityOrAntiAffinityGroup)).append("\n");
    sb.append("    maxNumberOfInstances: ").append(toIndentedString(maxNumberOfInstances)).append("\n");
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
