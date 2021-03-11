package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;


/**
 * VnfdVdu
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdVdu   {
  @JsonProperty("virtual-storage-desc")

  private List<String> virtualStorageDesc = null;

  @JsonProperty("boot-data")
  private String bootData = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("virtual-compute-desc")
  private String virtualComputeDesc = null;

  @JsonProperty("int-cpd")

  private List<VnfdVduIntCpdItem> intCpd = null;

  @JsonProperty("configurable-properties")

  private List<VnfdVdustoragerequirements> configurableProperties = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("boot-order")

  private List<VnfdBootorder> bootOrder = null;

  @JsonProperty("nfvi-constraint")

  private List<String> nfviConstraint = null;

  @JsonProperty("monitoring-parameter")

  private List<VnfdVduMonitoringParameterItem> monitoringParameter = null;

  @JsonProperty("sw-image-desc")
  private String swImageDesc = null;

  @JsonProperty("name")
  private String name = null;

  public VnfdVdu virtualStorageDesc(List<String> virtualStorageDesc) {
    this.virtualStorageDesc = virtualStorageDesc;
    return this;
  }

  public VnfdVdu addVirtualStorageDescItem(String virtualStorageDescItem) {
    if (this.virtualStorageDesc == null) {
      this.virtualStorageDesc = new ArrayList<String>();
    }
    this.virtualStorageDesc.add(virtualStorageDescItem);
    return this;
  }

  /**
   * Get virtualStorageDesc
   * @return virtualStorageDesc
   **/
  //@Schema(description = "")
  
    public List<String> getVirtualStorageDesc() {
    return virtualStorageDesc;
  }

  public void setVirtualStorageDesc(List<String> virtualStorageDesc) {
    this.virtualStorageDesc = virtualStorageDesc;
  }

  public VnfdVdu bootData(String bootData) {
    this.bootData = bootData;
    return this;
  }

  /**
   * Contains a string or a URL to a file contained in the VNF package used to customize a virtualised compute resource at boot time. The bootData may contain variable parts that are replaced by deployment specific values before being sent to the VIM.
   * @return bootData
   **/
  //@Schema(description = "Contains a string or a URL to a file contained in the VNF package used to customize a virtualised compute resource at boot time. The bootData may contain variable parts that are replaced by deployment specific values before being sent to the VIM.")
  
    public String getBootData() {
    return bootData;
  }

  public void setBootData(String bootData) {
    this.bootData = bootData;
  }

  public VnfdVdu description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Human readable description of the VDU.
   * @return description
   **/
  //@Schema(description = "Human readable description of the VDU.")
  
    public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public VnfdVdu virtualComputeDesc(String virtualComputeDesc) {
    this.virtualComputeDesc = virtualComputeDesc;
    return this;
  }

  /**
   * Describes CPU, Memory and acceleration requirements of the Virtualisation Container realizing this VDU.
   * @return virtualComputeDesc
   **/
  //@Schema(description = "Describes CPU, Memory and acceleration requirements of the Virtualisation Container realizing this VDU.")
  
    public String getVirtualComputeDesc() {
    return virtualComputeDesc;
  }

  public void setVirtualComputeDesc(String virtualComputeDesc) {
    this.virtualComputeDesc = virtualComputeDesc;
  }

  public VnfdVdu intCpd(List<VnfdVduIntCpdItem> intCpd) {
    this.intCpd = intCpd;
    return this;
  }

  public VnfdVdu addIntCpdItem(VnfdVduIntCpdItem intCpdItem) {
    if (this.intCpd == null) {
      this.intCpd = new ArrayList<VnfdVduIntCpdItem>();
    }
    this.intCpd.add(intCpdItem);
    return this;
  }

  /**
   * A internal-connection-point element is a type of connection point and describes network connectivity between a VDU instance and an internal Virtual Link or an external connection point.
   * @return intCpd
   **/
  //@Schema(description = "A internal-connection-point element is a type of connection point and describes network connectivity between a VDU instance and an internal Virtual Link or an external connection point.")
  
    public List<VnfdVduIntCpdItem> getIntCpd() {
    return intCpd;
  }

  public void setIntCpd(List<VnfdVduIntCpdItem> intCpd) {
    this.intCpd = intCpd;
  }

  public VnfdVdu configurableProperties(List<VnfdVdustoragerequirements> configurableProperties) {
    this.configurableProperties = configurableProperties;
    return this;
  }

  public VnfdVdu addConfigurablePropertiesItem(VnfdVdustoragerequirements configurablePropertiesItem) {
    if (this.configurableProperties == null) {
      this.configurableProperties = new ArrayList<VnfdVdustoragerequirements>();
    }
    this.configurableProperties.add(configurablePropertiesItem);
    return this;
  }

  /**
   * It provides VNFC configurable properties that can be modified using the ModifyVnfInfo operation.
   * @return configurableProperties
   **/
  //@Schema(description = "It provides VNFC configurable properties that can be modified using the ModifyVnfInfo operation.")

    public List<VnfdVdustoragerequirements> getConfigurableProperties() {
    return configurableProperties;
  }

  public void setConfigurableProperties(List<VnfdVdustoragerequirements> configurableProperties) {
    this.configurableProperties = configurableProperties;
  }

  public VnfdVdu id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of this VDU in VNFD.
   * @return id
   **/
  //@Schema(description = "Unique identifier of this VDU in VNFD.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public VnfdVdu bootOrder(List<VnfdBootorder> bootOrder) {
    this.bootOrder = bootOrder;
    return this;
  }

  public VnfdVdu addBootOrderItem(VnfdBootorder bootOrderItem) {
    if (this.bootOrder == null) {
      this.bootOrder = new ArrayList<VnfdBootorder>();
    }
    this.bootOrder.add(bootOrderItem);
    return this;
  }

  /**
   * The key indicates the boot index (lowest index defines highest boot priority). The Value references a descriptor from which a valid boot device is created e.g. VirtualStorageDesc from which a VirtualStorage instance is created.  Editor's note: The boot-order node requires further study.
   * @return bootOrder
   **/
  //@Schema(description = "The key indicates the boot index (lowest index defines highest boot priority). The Value references a descriptor from which a valid boot device is created e.g. VirtualStorageDesc from which a VirtualStorage instance is created.  Editor's note: The boot-order node requires further study.")

    public List<VnfdBootorder> getBootOrder() {
    return bootOrder;
  }

  public void setBootOrder(List<VnfdBootorder> bootOrder) {
    this.bootOrder = bootOrder;
  }

  public VnfdVdu nfviConstraint(List<String> nfviConstraint) {
    this.nfviConstraint = nfviConstraint;
    return this;
  }

  public VnfdVdu addNfviConstraintItem(String nfviConstraintItem) {
    if (this.nfviConstraint == null) {
      this.nfviConstraint = new ArrayList<String>();
    }
    this.nfviConstraint.add(nfviConstraintItem);
    return this;
  }

  /**
   * Get nfviConstraint
   * @return nfviConstraint
   **/
  //@Schema(description = "")
  
    public List<String> getNfviConstraint() {
    return nfviConstraint;
  }

  public void setNfviConstraint(List<String> nfviConstraint) {
    this.nfviConstraint = nfviConstraint;
  }

  public VnfdVdu monitoringParameter(List<VnfdVduMonitoringParameterItem> monitoringParameter) {
    this.monitoringParameter = monitoringParameter;
    return this;
  }

  public VnfdVdu addMonitoringParameterItem(VnfdVduMonitoringParameterItem monitoringParameterItem) {
    if (this.monitoringParameter == null) {
      this.monitoringParameter = new ArrayList<VnfdVduMonitoringParameterItem>();
    }
    this.monitoringParameter.add(monitoringParameterItem);
    return this;
  }

  /**
   * Get monitoringParameter
   * @return monitoringParameter
   **/
  //@Schema(description = "")
  
    public List<VnfdVduMonitoringParameterItem> getMonitoringParameter() {
    return monitoringParameter;
  }

  public void setMonitoringParameter(List<VnfdVduMonitoringParameterItem> monitoringParameter) {
    this.monitoringParameter = monitoringParameter;
  }

  public VnfdVdu swImageDesc(String swImageDesc) {
    this.swImageDesc = swImageDesc;
    return this;
  }

  /**
   * Describes the software image which is directly loaded on the virtualisation container realising this Vdu.
   * @return swImageDesc
   **/
  //@Schema(description = "Describes the software image which is directly loaded on the virtualisation container realising this Vdu.")
  
    public String getSwImageDesc() {
    return swImageDesc;
  }

  public void setSwImageDesc(String swImageDesc) {
    this.swImageDesc = swImageDesc;
  }

  public VnfdVdu name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Human readable name of the VDU.
   * @return name
   **/
  //@Schema(description = "Human readable name of the VDU.")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VnfdVdu vnfdVdu = (VnfdVdu) o;
    return Objects.equals(this.virtualStorageDesc, vnfdVdu.virtualStorageDesc) &&
        Objects.equals(this.bootData, vnfdVdu.bootData) &&
        Objects.equals(this.description, vnfdVdu.description) &&
        Objects.equals(this.virtualComputeDesc, vnfdVdu.virtualComputeDesc) &&
        Objects.equals(this.intCpd, vnfdVdu.intCpd) &&
        Objects.equals(this.configurableProperties, vnfdVdu.configurableProperties) &&
        Objects.equals(this.id, vnfdVdu.id) &&
        Objects.equals(this.bootOrder, vnfdVdu.bootOrder) &&
        Objects.equals(this.nfviConstraint, vnfdVdu.nfviConstraint) &&
        Objects.equals(this.monitoringParameter, vnfdVdu.monitoringParameter) &&
        Objects.equals(this.swImageDesc, vnfdVdu.swImageDesc) &&
        Objects.equals(this.name, vnfdVdu.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(virtualStorageDesc, bootData, description, virtualComputeDesc, intCpd, configurableProperties, id, bootOrder, nfviConstraint, monitoringParameter, swImageDesc, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfdVdu {\n");
    
    sb.append("    virtualStorageDesc: ").append(toIndentedString(virtualStorageDesc)).append("\n");
    sb.append("    bootData: ").append(toIndentedString(bootData)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    virtualComputeDesc: ").append(toIndentedString(virtualComputeDesc)).append("\n");
    sb.append("    intCpd: ").append(toIndentedString(intCpd)).append("\n");
    sb.append("    configurableProperties: ").append(toIndentedString(configurableProperties)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    bootOrder: ").append(toIndentedString(bootOrder)).append("\n");
    sb.append("    nfviConstraint: ").append(toIndentedString(nfviConstraint)).append("\n");
    sb.append("    monitoringParameter: ").append(toIndentedString(monitoringParameter)).append("\n");
    sb.append("    swImageDesc: ").append(toIndentedString(swImageDesc)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
