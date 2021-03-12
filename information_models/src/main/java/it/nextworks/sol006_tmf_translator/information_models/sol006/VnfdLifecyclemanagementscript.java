package it.nextworks.sol006_tmf_translator.information_models.sol006;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;


/**
 * VnfdLifecyclemanagementscript
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T19:09:38.446+01:00[Europe/Rome]")


public class VnfdLifecyclemanagementscript   {
  @JsonProperty("lcm-transition-event")

  private List<String> lcmTransitionEvent = null;

  @JsonProperty("script-input")

  private List<VnfdScriptinput> scriptInput = null;

  @JsonProperty("script")
  private String script = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("script-dsl")
  private String scriptDsl = null;

  /**
   * Describes VNF lifecycle event(s) or an external stimulus detected on a VNFM reference point.
   */
  public enum EventEnum {
    START_INSTANTIATION("start-instantiation"),
    
    END_INSTANTIATION("end-instantiation"),
    
    START_SCALING("start-scaling"),
    
    END_SCALING("end-scaling"),
    
    START_HEALING("start-healing"),
    
    END_HEALING("end-healing"),
    
    START_TERMINATION("start-termination"),
    
    END_TERMINATION("end-termination"),
    
    START_VNF_FLAVOUR_CHANGE("start-vnf-flavour-change"),
    
    END_VNF_FLAVOUR_CHANGE("end-vnf-flavour-change"),
    
    START_VNF_OPERATION_CHANGE("start-vnf-operation-change"),
    
    END_VNF_OPERATION_CHANGE("end-vnf-operation-change"),
    
    START_VNF_EXT_CONN_CHANGE("start-vnf-ext-conn-change"),
    
    END_VNF_EXT_CONN_CHANGE("end-vnf-ext-conn-change"),
    
    START_VNFINFO_MODIFICATION("start-vnfinfo-modification"),
    
    END_VNFINFO_MODIFICATION("end-vnfinfo-modification");

    private String value;

    EventEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EventEnum fromValue(String text) {
      for (EventEnum b : EventEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("event")

  private List<EventEnum> event = null;

  public VnfdLifecyclemanagementscript lcmTransitionEvent(List<String> lcmTransitionEvent) {
    this.lcmTransitionEvent = lcmTransitionEvent;
    return this;
  }

  public VnfdLifecyclemanagementscript addLcmTransitionEventItem(String lcmTransitionEventItem) {
    if (this.lcmTransitionEvent == null) {
      this.lcmTransitionEvent = new ArrayList<String>();
    }
    this.lcmTransitionEvent.add(lcmTransitionEventItem);
    return this;
  }

  /**
   * Get lcmTransitionEvent
   * @return lcmTransitionEvent
   **/
  //@Schema(description = "")
  
    public List<String> getLcmTransitionEvent() {
    return lcmTransitionEvent;
  }

  public void setLcmTransitionEvent(List<String> lcmTransitionEvent) {
    this.lcmTransitionEvent = lcmTransitionEvent;
  }

  public VnfdLifecyclemanagementscript scriptInput(List<VnfdScriptinput> scriptInput) {
    this.scriptInput = scriptInput;
    return this;
  }

  public VnfdLifecyclemanagementscript addScriptInputItem(VnfdScriptinput scriptInputItem) {
    if (this.scriptInput == null) {
      this.scriptInput = new ArrayList<VnfdScriptinput>();
    }
    this.scriptInput.add(scriptInputItem);
    return this;
  }

  /**
   * Array of KVP requirements with the key as the parameter name and the value as the parameter that need to be passed as an input to the script.
   * @return scriptInput
   **/
  //@Schema(description = "Array of KVP requirements with the key as the parameter name and the value as the parameter that need to be passed as an input to the script.")

    public List<VnfdScriptinput> getScriptInput() {
    return scriptInput;
  }

  public void setScriptInput(List<VnfdScriptinput> scriptInput) {
    this.scriptInput = scriptInput;
  }

  public VnfdLifecyclemanagementscript script(String script) {
    this.script = script;
    return this;
  }

  /**
   * Includes a VNF LCM script (e.g. written in a DSL as specified in requirement VNF_PACK.LCM.001) triggered to react to one of the events listed in the event attribute.
   * @return script
   **/
  //@Schema(description = "Includes a VNF LCM script (e.g. written in a DSL as specified in requirement VNF_PACK.LCM.001) triggered to react to one of the events listed in the event attribute.")
  
    public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public VnfdLifecyclemanagementscript id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A unique string that identfies the script in question.
   * @return id
   **/
  //@Schema(description = "A unique string that identfies the script in question.")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public VnfdLifecyclemanagementscript scriptDsl(String scriptDsl) {
    this.scriptDsl = scriptDsl;
    return this;
  }

  /**
   * Defines the domain specific language (i.e. the type) of script that is provided. Types of scripts could include bash, python, etc.
   * @return scriptDsl
   **/
  //@Schema(description = "Defines the domain specific language (i.e. the type) of script that is provided. Types of scripts could include bash, python, etc.")
  
    public String getScriptDsl() {
    return scriptDsl;
  }

  public void setScriptDsl(String scriptDsl) {
    this.scriptDsl = scriptDsl;
  }

  public VnfdLifecyclemanagementscript event(List<EventEnum> event) {
    this.event = event;
    return this;
  }

  public VnfdLifecyclemanagementscript addEventItem(EventEnum eventItem) {
    if (this.event == null) {
      this.event = new ArrayList<EventEnum>();
    }
    this.event.add(eventItem);
    return this;
  }

  /**
   * Get event
   * @return event
   **/
  //@Schema(description = "")
  
    public List<EventEnum> getEvent() {
    return event;
  }

  public void setEvent(List<EventEnum> event) {
    this.event = event;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VnfdLifecyclemanagementscript vnfdLifecyclemanagementscript = (VnfdLifecyclemanagementscript) o;
    return Objects.equals(this.lcmTransitionEvent, vnfdLifecyclemanagementscript.lcmTransitionEvent) &&
        Objects.equals(this.scriptInput, vnfdLifecyclemanagementscript.scriptInput) &&
        Objects.equals(this.script, vnfdLifecyclemanagementscript.script) &&
        Objects.equals(this.id, vnfdLifecyclemanagementscript.id) &&
        Objects.equals(this.scriptDsl, vnfdLifecyclemanagementscript.scriptDsl) &&
        Objects.equals(this.event, vnfdLifecyclemanagementscript.event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lcmTransitionEvent, scriptInput, script, id, scriptDsl, event);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfdLifecyclemanagementscript {\n");
    
    sb.append("    lcmTransitionEvent: ").append(toIndentedString(lcmTransitionEvent)).append("\n");
    sb.append("    scriptInput: ").append(toIndentedString(scriptInput)).append("\n");
    sb.append("    script: ").append(toIndentedString(script)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    scriptDsl: ").append(toIndentedString(scriptDsl)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
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
