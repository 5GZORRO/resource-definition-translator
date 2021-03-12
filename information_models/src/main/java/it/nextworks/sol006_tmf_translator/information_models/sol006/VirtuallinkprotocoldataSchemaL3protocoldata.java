package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;


/**
 * Specifies the L3 protocol data for this virtual link. Shall be present when the associatedLayerProtocol attribute indicates a L3 protocol and shall be absent otherwise.
 */
//@Schema(description = "Specifies the L3 protocol data for this virtual link. Shall be present when the associatedLayerProtocol attribute indicates a L3 protocol and shall be absent otherwise.")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VirtuallinkprotocoldataSchemaL3protocoldata   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("ip-allocation-pools")

  private List<String> ipAllocationPools = null;

  /**
   * Specifies IP version of this L3 protocol. Value: • IPV4. • IPV6.
   */
  public enum IpVersionEnum {
    IPV4("ipv4"),
    
    IPV6("ipv6");

    private String value;

    IpVersionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static IpVersionEnum fromValue(String text) {
      for (IpVersionEnum b : IpVersionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("ip-version")
  private IpVersionEnum ipVersion = null;

  @JsonProperty("dhcp-enabled")
  private Boolean dhcpEnabled = null;

  @JsonProperty("cidr")
  private String cidr = null;

  @JsonProperty("gateway-ip")
  private String gatewayIp = null;

  /**
   * Specifies IPv6 address mode. Possible values: • SLAAC. • DHCPV6-STATEFUL. • DHCPV6-STATELESS. May be present when the value of the ipVersion attribute is 'IPV6' and shall be absent otherwise.
   */
  public enum Ipv6AddressModeEnum {
    SLAAC("slaac"),
    
    DHCPV6_STATEFUL("dhcpv6-stateful"),
    
    DHCPV6_STATELESS("dhcpv6-stateless");

    private String value;

    Ipv6AddressModeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static Ipv6AddressModeEnum fromValue(String text) {
      for (Ipv6AddressModeEnum b : Ipv6AddressModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("ipv6-address-mode")
  private Ipv6AddressModeEnum ipv6AddressMode = null;

  public VirtuallinkprotocoldataSchemaL3protocoldata name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Network name associated with this L3 protocol.
   * @return name
   **/
  //@Schema(description = "Network name associated with this L3 protocol.")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata ipAllocationPools(List<String> ipAllocationPools) {
    this.ipAllocationPools = ipAllocationPools;
    return this;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata addIpAllocationPoolsItem(String ipAllocationPoolsItem) {
    if (this.ipAllocationPools == null) {
      this.ipAllocationPools = new ArrayList<String>();
    }
    this.ipAllocationPools.add(ipAllocationPoolsItem);
    return this;
  }

  /**
   * Get ipAllocationPools
   * @return ipAllocationPools
   **/
  //@Schema(description = "")
  
    public List<String> getIpAllocationPools() {
    return ipAllocationPools;
  }

  public void setIpAllocationPools(List<String> ipAllocationPools) {
    this.ipAllocationPools = ipAllocationPools;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata ipVersion(IpVersionEnum ipVersion) {
    this.ipVersion = ipVersion;
    return this;
  }

  /**
   * Specifies IP version of this L3 protocol. Value: • IPV4. • IPV6.
   * @return ipVersion
   **/
  //@Schema(description = "Specifies IP version of this L3 protocol. Value: • IPV4. • IPV6.")
  
    public IpVersionEnum getIpVersion() {
    return ipVersion;
  }

  public void setIpVersion(IpVersionEnum ipVersion) {
    this.ipVersion = ipVersion;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata dhcpEnabled(Boolean dhcpEnabled) {
    this.dhcpEnabled = dhcpEnabled;
    return this;
  }

  /**
   * Indicates whether DHCP (Dynamic Host Configuration Protocol) is enabled or disabled for this L3 protocol.
   * @return dhcpEnabled
   **/
  //@Schema(description = "Indicates whether DHCP (Dynamic Host Configuration Protocol) is enabled or disabled for this L3 protocol.")
  
    public Boolean isDhcpEnabled() {
    return dhcpEnabled;
  }

  public void setDhcpEnabled(Boolean dhcpEnabled) {
    this.dhcpEnabled = dhcpEnabled;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata cidr(String cidr) {
    this.cidr = cidr;
    return this;
  }

  /**
   * Specifies the CIDR (Classless InterDomain Routing) of this L3 protocol.
   * @return cidr
   **/
  //@Schema(description = "Specifies the CIDR (Classless InterDomain Routing) of this L3 protocol.")
  
    public String getCidr() {
    return cidr;
  }

  public void setCidr(String cidr) {
    this.cidr = cidr;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata gatewayIp(String gatewayIp) {
    this.gatewayIp = gatewayIp;
    return this;
  }

  /**
   * Specifies the gateway IP address for this L3 protocol.
   * @return gatewayIp
   **/
  //@Schema(description = "Specifies the gateway IP address for this L3 protocol.")
  
    public String getGatewayIp() {
    return gatewayIp;
  }

  public void setGatewayIp(String gatewayIp) {
    this.gatewayIp = gatewayIp;
  }

  public VirtuallinkprotocoldataSchemaL3protocoldata ipv6AddressMode(Ipv6AddressModeEnum ipv6AddressMode) {
    this.ipv6AddressMode = ipv6AddressMode;
    return this;
  }

  /**
   * Specifies IPv6 address mode. Possible values: • SLAAC. • DHCPV6-STATEFUL. • DHCPV6-STATELESS. May be present when the value of the ipVersion attribute is 'IPV6' and shall be absent otherwise.
   * @return ipv6AddressMode
   **/
  //@Schema(description = "Specifies IPv6 address mode. Possible values: • SLAAC. • DHCPV6-STATEFUL. • DHCPV6-STATELESS. May be present when the value of the ipVersion attribute is 'IPV6' and shall be absent otherwise.")
  
    public Ipv6AddressModeEnum getIpv6AddressMode() {
    return ipv6AddressMode;
  }

  public void setIpv6AddressMode(Ipv6AddressModeEnum ipv6AddressMode) {
    this.ipv6AddressMode = ipv6AddressMode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VirtuallinkprotocoldataSchemaL3protocoldata virtuallinkprotocoldataSchemaL3protocoldata = (VirtuallinkprotocoldataSchemaL3protocoldata) o;
    return Objects.equals(this.name, virtuallinkprotocoldataSchemaL3protocoldata.name) &&
        Objects.equals(this.ipAllocationPools, virtuallinkprotocoldataSchemaL3protocoldata.ipAllocationPools) &&
        Objects.equals(this.ipVersion, virtuallinkprotocoldataSchemaL3protocoldata.ipVersion) &&
        Objects.equals(this.dhcpEnabled, virtuallinkprotocoldataSchemaL3protocoldata.dhcpEnabled) &&
        Objects.equals(this.cidr, virtuallinkprotocoldataSchemaL3protocoldata.cidr) &&
        Objects.equals(this.gatewayIp, virtuallinkprotocoldataSchemaL3protocoldata.gatewayIp) &&
        Objects.equals(this.ipv6AddressMode, virtuallinkprotocoldataSchemaL3protocoldata.ipv6AddressMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, ipAllocationPools, ipVersion, dhcpEnabled, cidr, gatewayIp, ipv6AddressMode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VirtuallinkprotocoldataSchemaL3protocoldata {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    ipAllocationPools: ").append(toIndentedString(ipAllocationPools)).append("\n");
    sb.append("    ipVersion: ").append(toIndentedString(ipVersion)).append("\n");
    sb.append("    dhcpEnabled: ").append(toIndentedString(dhcpEnabled)).append("\n");
    sb.append("    cidr: ").append(toIndentedString(cidr)).append("\n");
    sb.append("    gatewayIp: ").append(toIndentedString(gatewayIp)).append("\n");
    sb.append("    ipv6AddressMode: ").append(toIndentedString(ipv6AddressMode)).append("\n");
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
