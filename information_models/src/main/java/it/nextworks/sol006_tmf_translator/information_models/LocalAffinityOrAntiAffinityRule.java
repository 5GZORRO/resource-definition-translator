package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;


/**
 * LocalAffinityOrAntiAffinityRule
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class LocalAffinityOrAntiAffinityRule   {
  @JsonProperty("local-affinity-or-anti-affinity-rule")

  private List<LocalAffinityOrAntiaffinityRuleItem> localAffinityOrAntiAffinityRule = null;

  public LocalAffinityOrAntiAffinityRule localAffinityOrAntiAffinityRule(List<LocalAffinityOrAntiaffinityRuleItem> localAffinityOrAntiAffinityRule) {
    this.localAffinityOrAntiAffinityRule = localAffinityOrAntiAffinityRule;
    return this;
  }

  public LocalAffinityOrAntiAffinityRule addLocalAffinityOrAntiAffinityRuleItem(LocalAffinityOrAntiaffinityRuleItem localAffinityOrAntiAffinityRuleItem) {
    if (this.localAffinityOrAntiAffinityRule == null) {
      this.localAffinityOrAntiAffinityRule = new ArrayList<LocalAffinityOrAntiaffinityRuleItem>();
    }
    this.localAffinityOrAntiAffinityRule.add(localAffinityOrAntiAffinityRuleItem);
    return this;
  }

  /**
   * Get localAffinityOrAntiAffinityRule
   * @return localAffinityOrAntiAffinityRule
   **/
  //@Schema(description = "")

    public List<LocalAffinityOrAntiaffinityRuleItem> getLocalAffinityOrAntiAffinityRule() {
    return localAffinityOrAntiAffinityRule;
  }

  public void setLocalAffinityOrAntiAffinityRule(List<LocalAffinityOrAntiaffinityRuleItem> localAffinityOrAntiAffinityRule) {
    this.localAffinityOrAntiAffinityRule = localAffinityOrAntiAffinityRule;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalAffinityOrAntiAffinityRule localAffinityOrAntiAffinityRule = (LocalAffinityOrAntiAffinityRule) o;
    return Objects.equals(this.localAffinityOrAntiAffinityRule, localAffinityOrAntiAffinityRule.localAffinityOrAntiAffinityRule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localAffinityOrAntiAffinityRule);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocalAffinityOrAntiAffinityRule {\n");
    
    sb.append("    localAffinityOrAntiAffinityRule: ").append(toIndentedString(localAffinityOrAntiAffinityRule)).append("\n");
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
