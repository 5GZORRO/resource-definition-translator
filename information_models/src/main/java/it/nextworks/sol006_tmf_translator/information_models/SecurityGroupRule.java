package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;


/**
 * SecurityGroupRule
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class SecurityGroupRule   {
  @JsonProperty("security-group-rule")

  private List<SecuritygroupruleSecuritygrouprule> securityGroupRule = null;

  public SecurityGroupRule securityGroupRule(List<SecuritygroupruleSecuritygrouprule> securityGroupRule) {
    this.securityGroupRule = securityGroupRule;
    return this;
  }

  public SecurityGroupRule addSecurityGroupRuleItem(SecuritygroupruleSecuritygrouprule securityGroupRuleItem) {
    if (this.securityGroupRule == null) {
      this.securityGroupRule = new ArrayList<SecuritygroupruleSecuritygrouprule>();
    }
    this.securityGroupRule.add(securityGroupRuleItem);
    return this;
  }

  /**
   * Defines security group rules to be used by the VNF.
   * @return securityGroupRule
   **/
  //@Schema(description = "Defines security group rules to be used by the VNF.")

    public List<SecuritygroupruleSecuritygrouprule> getSecurityGroupRule() {
    return securityGroupRule;
  }

  public void setSecurityGroupRule(List<SecuritygroupruleSecuritygrouprule> securityGroupRule) {
    this.securityGroupRule = securityGroupRule;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SecurityGroupRule securityGroupRule = (SecurityGroupRule) o;
    return Objects.equals(this.securityGroupRule, securityGroupRule.securityGroupRule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(securityGroupRule);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SecurityGroupRule {\n");
    
    sb.append("    securityGroupRule: ").append(toIndentedString(securityGroupRule)).append("\n");
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
