package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;



/**
 * MonitoringParameter
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "performance-metric", visible = true )
@JsonSubTypes({
        @JsonSubTypes.Type(value = VnfdDfMonitoringParameterItem.class, name = "AllOfvnfd_dfMonitoringParameterItems"),
        @JsonSubTypes.Type(value = VnfdVduMonitoringParameterItem.class, name = "AllOfvnfd_vduMonitoringParameterItems"),
})


public class MonitoringParameter   {
  @JsonProperty("collection-period")
  private String collectionPeriod = null;

  @JsonProperty("name")
  private String name = null;

  @JsonTypeId
  private String performanceMetric = null;

  public MonitoringParameter collectionPeriod(String collectionPeriod) {
    this.collectionPeriod = collectionPeriod;
    return this;
  }

  /**
   * An attribute that describes the recommended periodicity at which to collect the performance information. VNFM determines if this parameter is considered.  The vendor may provide this information as a guidance for creating PmJobs if needed.
   * @return collectionPeriod
   **/
  //@Schema(description = "An attribute that describes the recommended periodicity at which to collect the performance information. VNFM determines if this parameter is considered.  The vendor may provide this information as a guidance for creating PmJobs if needed.")
  
    public String getCollectionPeriod() {
    return collectionPeriod;
  }

  public void setCollectionPeriod(String collectionPeriod) {
    this.collectionPeriod = collectionPeriod;
  }

  public MonitoringParameter name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human readable name of the monitoring parameter.
   * @return name
   **/
  //@Schema(description = "Human readable name of the monitoring parameter.")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MonitoringParameter performanceMetric(String performanceMetric) {
    this.performanceMetric = performanceMetric;
    return this;
  }

  /**
   * Performance metric that is monitored. This attribute shall contain the related 'Measurement Name' value as defined in clause 7.2 of ETSI GS NFV-IFA 027
   * @return performanceMetric
   **/
  //@Schema(description = "Performance metric that is monitored. This attribute shall contain the related 'Measurement Name' value as defined in clause 7.2 of ETSI GS NFV-IFA 027")
  
    public String getPerformanceMetric() {
    return performanceMetric;
  }

  public void setPerformanceMetric(String performanceMetric) {
    this.performanceMetric = performanceMetric;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MonitoringParameter monitoringParameter = (MonitoringParameter) o;
    return Objects.equals(this.collectionPeriod, monitoringParameter.collectionPeriod) &&
        Objects.equals(this.name, monitoringParameter.name) &&
        Objects.equals(this.performanceMetric, monitoringParameter.performanceMetric);
  }

  @Override
  public int hashCode() {
    return Objects.hash(collectionPeriod, name, performanceMetric);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MonitoringParameter {\n");
    
    sb.append("    collectionPeriod: ").append(toIndentedString(collectionPeriod)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    performanceMetric: ").append(toIndentedString(performanceMetric)).append("\n");
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
