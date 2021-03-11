package it.nextworks.sol006_tmf_translator.information_models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import org.springframework.validation.annotation.Validated;

/**
 * AffinityOrAntiAffinityGroupSchema
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class AffinityOrAntiAffinityGroupSchema   {
  @JsonProperty("affinity-or-anti-affinity-group_id")
  private String affinityOrAntiAffinityGroupId = null;

  /**
   * Specifies the type of relationship that the members of the group have: 'affinity' or 'anti-affinity.'
   */
  public enum TypeEnum {
    AFFINITY("affinity"),
    
    ANTI_AFFINITY("anti-affinity");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("type")
  private TypeEnum type = null;

  /**
   * Specifies the scope of the affinity or anti-affinity relationship e.g. a NFVI node, an NFVI PoP, etc.
   */
  public enum ScopeEnum {
    NFVI_NODE("nfvi-node"),
    
    ZONE_GROUP("zone-group"),
    
    ZONE("zone"),
    
    NFVI_POP("nfvi-pop");

    private String value;

    ScopeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ScopeEnum fromValue(String text) {
      for (ScopeEnum b : ScopeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("scope")
  private ScopeEnum scope = null;

  public AffinityOrAntiAffinityGroupSchema affinityOrAntiAffinityGroupId(String affinityOrAntiAffinityGroupId) {
    this.affinityOrAntiAffinityGroupId = affinityOrAntiAffinityGroupId;
    return this;
  }

  /**
   * Identifier of Identifier of this AffinityOrAntiAffinityGroup information element.
   * @return affinityOrAntiAffinityGroupId
   **/
  //@Schema(description = "Identifier of Identifier of this AffinityOrAntiAffinityGroup information element.")
  
    public String getAffinityOrAntiAffinityGroupId() {
    return affinityOrAntiAffinityGroupId;
  }

  public void setAffinityOrAntiAffinityGroupId(String affinityOrAntiAffinityGroupId) {
    this.affinityOrAntiAffinityGroupId = affinityOrAntiAffinityGroupId;
  }

  public AffinityOrAntiAffinityGroupSchema type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Specifies the type of relationship that the members of the group have: 'affinity' or 'anti-affinity.'
   * @return type
   **/
  //@Schema(description = "Specifies the type of relationship that the members of the group have: 'affinity' or 'anti-affinity.'")
  
    public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public AffinityOrAntiAffinityGroupSchema scope(ScopeEnum scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Specifies the scope of the affinity or anti-affinity relationship e.g. a NFVI node, an NFVI PoP, etc.
   * @return scope
   **/
  //@Schema(description = "Specifies the scope of the affinity or anti-affinity relationship e.g. a NFVI node, an NFVI PoP, etc.")
  
    public ScopeEnum getScope() {
    return scope;
  }

  public void setScope(ScopeEnum scope) {
    this.scope = scope;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AffinityOrAntiAffinityGroupSchema affinityOrAntiAffinityGroupSchema = (AffinityOrAntiAffinityGroupSchema) o;
    return Objects.equals(this.affinityOrAntiAffinityGroupId, affinityOrAntiAffinityGroupSchema.affinityOrAntiAffinityGroupId) &&
        Objects.equals(this.type, affinityOrAntiAffinityGroupSchema.type) &&
        Objects.equals(this.scope, affinityOrAntiAffinityGroupSchema.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(affinityOrAntiAffinityGroupId, type, scope);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AffinityOrAntiAffinityGroupSchema {\n");
    
    sb.append("    affinityOrAntiAffinityGroupId: ").append(toIndentedString(affinityOrAntiAffinityGroupId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
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
