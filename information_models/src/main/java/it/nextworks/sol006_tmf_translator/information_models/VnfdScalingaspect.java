package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * VnfdScalingaspect
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdScalingaspect   {
  @JsonProperty("aspect-delta-details")
  private VnfdAspectdeltadetails aspectDeltaDetails = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("max-scale-level")
  private String maxScaleLevel = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("description")
  private String description = null;

  public VnfdScalingaspect aspectDeltaDetails(VnfdAspectdeltadetails aspectDeltaDetails) {
    this.aspectDeltaDetails = aspectDeltaDetails;
    return this;
  }

  /**
   * Get aspectDeltaDetails
   * @return aspectDeltaDetails
   **/
  //@Schema(description = "")
  

    public VnfdAspectdeltadetails getAspectDeltaDetails() {
    return aspectDeltaDetails;
  }

  public void setAspectDeltaDetails(VnfdAspectdeltadetails aspectDeltaDetails) {
    this.aspectDeltaDetails = aspectDeltaDetails;
  }

  public VnfdScalingaspect id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of this aspect in the VNFD.
   * @return id
   **/
  //@Schema(description = "Unique identifier of this aspect in the VNFD.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public VnfdScalingaspect maxScaleLevel(String maxScaleLevel) {
    this.maxScaleLevel = maxScaleLevel;
    return this;
  }

  /**
   * The maximum scaleLevel for total number of scaling steps that can be applied w.r.t. this aspect. The value of this attribute corresponds to the number of scaling steps can be applied to this aspect when scaling it from the minimum scale level (i.e. 0) to the maximum scale level defined by this attribute.
   * @return maxScaleLevel
   **/
  //@Schema(description = "The maximum scaleLevel for total number of scaling steps that can be applied w.r.t. this aspect. The value of this attribute corresponds to the number of scaling steps can be applied to this aspect when scaling it from the minimum scale level (i.e. 0) to the maximum scale level defined by this attribute.")
  
    public String getMaxScaleLevel() {
    return maxScaleLevel;
  }

  public void setMaxScaleLevel(String maxScaleLevel) {
    this.maxScaleLevel = maxScaleLevel;
  }

  public VnfdScalingaspect name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human readable name of the aspect.
   * @return name
   **/
  //@Schema(description = "Human readable name of the aspect.")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VnfdScalingaspect description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Human readable description of the aspect.
   * @return description
   **/
  //@Schema(description = "Human readable description of the aspect.")
  
    public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VnfdScalingaspect vnfdScalingaspect = (VnfdScalingaspect) o;
    return Objects.equals(this.aspectDeltaDetails, vnfdScalingaspect.aspectDeltaDetails) &&
        Objects.equals(this.id, vnfdScalingaspect.id) &&
        Objects.equals(this.maxScaleLevel, vnfdScalingaspect.maxScaleLevel) &&
        Objects.equals(this.name, vnfdScalingaspect.name) &&
        Objects.equals(this.description, vnfdScalingaspect.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aspectDeltaDetails, id, maxScaleLevel, name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfdScalingaspect {\n");
    
    sb.append("    aspectDeltaDetails: ").append(toIndentedString(aspectDeltaDetails)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    maxScaleLevel: ").append(toIndentedString(maxScaleLevel)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
