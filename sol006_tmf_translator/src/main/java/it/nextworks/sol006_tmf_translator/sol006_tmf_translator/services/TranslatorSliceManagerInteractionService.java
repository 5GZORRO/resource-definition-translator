package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslatorSliceManagerInteractionService {

    public static class Attribute {

        @JsonProperty("attribute_name")
        private String attributeName;

        @JsonProperty("attribute_type")
        private String attributeType;

        @JsonProperty("attribute_value")
        private String attributeValue;

        @JsonProperty("attribute_description")
        private String attributeDescription;

        @JsonCreator
        public Attribute(@JsonProperty("attribute_name") String attributeName,
                         @JsonProperty("attribute_type") String attributeType,
                         @JsonProperty("attribute_value") String attributeValue,
                         @JsonProperty("attribute_description") String attributeDescription) {
            this.attributeName        = attributeName;
            this.attributeType        = attributeType;
            this.attributeValue       = attributeValue;
            this.attributeDescription = attributeDescription;
        }

        public String getAttributeName() { return attributeName; }

        public String getAttributeType() { return attributeType; }

        public String getAttributeValue() { return attributeValue; }

        public String getAttributeDescription() { return attributeDescription; }
    }

    public static class ConfigurableParameter {

        @JsonProperty("parameter_name")
        private String parameterName;

        @JsonProperty("parameter_type")
        private String parameterType;

        @JsonProperty("parameter_description")
        private String parameterDescription;

        @JsonCreator
        public ConfigurableParameter(@JsonProperty("parameter_name") String parameterName,
                                     @JsonProperty("parameter_type") String parameterType,
                                     @JsonProperty("parameter_description") String parameterDescription) {
            this.parameterName        = parameterName;
            this.parameterType        = parameterType;
            this.parameterDescription = parameterDescription;
        }

        public String getParameterName() { return parameterName; }

        public String getParameterType() { return parameterType; }

        public String getParameterDescription() { return parameterDescription; }
    }

    public static class SliceType {

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("slic3_temp_id")
        private String sliceTempId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("version")
        private String version;

        @JsonProperty("description")
        private String description;

        @JsonProperty("attributes")
        private List<Attribute> attributes;

        @JsonProperty("configurable_parameters")
        private List<ConfigurableParameter> configurableParameters;

        @JsonProperty("id")
        private String id;

        @JsonProperty("status")
        private String status;

        @JsonCreator
        public SliceType(@JsonProperty("user_id") String userId,
                         @JsonProperty("slic3_temp_id") String sliceTempId,
                         @JsonProperty("name") String name,
                         @JsonProperty("version") String version,
                         @JsonProperty("description") String description,
                         @JsonProperty("attributes") List<Attribute> attributes,
                         @JsonProperty("configurable_parameters") List<ConfigurableParameter> configurableParameters,
                         @JsonProperty("id") String id,
                         @JsonProperty("status") String status) {
            this.userId                 = userId;
            this.sliceTempId            = sliceTempId;
            this.name                   = name;
            this.version                = version;
            this.description            = description;
            this.attributes             = attributes;
            this.configurableParameters = configurableParameters;
            this.id                     = id;
            this.status                 = status;
        }
    }

    public static class ComputeChunk {

        @JsonProperty("compute_id")
        private String computeId;

        @JsonCreator
        public ComputeChunk(@JsonProperty("compute_id") String computeId) {
            this.computeId = computeId;
        }

        public String getComputeId() { return computeId; }
    }

    public static class NetworkChunk {

        @JsonProperty("physical_network_id")
        private String physicalNetworkId;

        @JsonProperty("role")
        private String role;

        @JsonCreator
        public NetworkChunk(@JsonProperty("physical_network_id") String physicalNetworkId,
                            @JsonProperty("role") String role) {
            this.physicalNetworkId = physicalNetworkId;
            this.role              = role;
        }

        public String getPhysicalNetworkId() { return physicalNetworkId; }

        public String getRole() { return role; }
    }

    public static class SelectedLink {

        @JsonProperty("id")
        private String id;

        @JsonProperty("key")
        private String key;

        @JsonProperty("srcPhyId")
        private String srcPhyId;

        @JsonProperty("dstPhyId")
        private String dstPhyId;

        @JsonCreator
        public SelectedLink(@JsonProperty("id") String id,
                            @JsonProperty("key") String key,
                            @JsonProperty("srcPhyId") String srcPhyId,
                            @JsonProperty("dstPhyId") String dstPhyId) {
            this.id       = id;
            this.key      = key;
            this.srcPhyId = srcPhyId;
            this.dstPhyId = dstPhyId;
        }

        public String getId() { return id; }

        public String getKey() { return key; }

        public String getSrcPhyId() { return srcPhyId; }

        public String getDstPhyId() { return dstPhyId; }
    }

    public static class Config {}

    public static class SelectedPhy {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("config")
        private Config config;

        @JsonProperty("box_name")
        private String boxName;

        @JsonCreator
        public SelectedPhy(@JsonProperty("id") String id,
                           @JsonProperty("name") String name,
                           @JsonProperty("type") String type,
                           @JsonProperty("config") Config config,
                           @JsonProperty("box_name") String boxName) {
            this.id      = id;
            this.name    = name;
            this.type    = type;
            this.config  = config;
            this.boxName = boxName;
        }

        public String getId() { return id; }

        public String getName() { return name; }

        public String getType() { return type; }

        public Config getConfig() { return config; }

        public String getBoxName() { return boxName; }
    }

    public static class ChunkTopology {

        @JsonProperty("selectedLinks")
        private List<SelectedLink> selectedLinks;

        @JsonProperty("selectedPhys")
        private List<SelectedPhy> selectedPhys;

        @JsonCreator
        public ChunkTopology(@JsonProperty("selectedLinks") List<SelectedLink> selectedLinks,
                             @JsonProperty("selectedPhys") List<SelectedPhy> selectedPhys) {
            this.selectedLinks = selectedLinks;
            this.selectedPhys  = selectedPhys;
        }

        public List<SelectedLink> getSelectedLinks() { return selectedLinks; }

        public List<SelectedPhy> getSelectedPhys() { return selectedPhys; }
    }

    public static class RadioChunk {

        @JsonProperty("ran_infra_id")
        private String ranInfraId;

        @JsonProperty("chunk_topology")
        private ChunkTopology chunkTopology;

        @JsonCreator
        public RadioChunk(@JsonProperty("ran_infra_id") String ranInfraId,
                          @JsonProperty("chunk_topology") ChunkTopology chunkTopology) {
            this.ranInfraId    = ranInfraId;
            this.chunkTopology = chunkTopology;
        }

        public String getRanInfraId() { return ranInfraId; }

        public ChunkTopology getChunkTopology() { return chunkTopology; }
    }

    public static class SliceBlueprint {

        @JsonProperty("compute_chunk")
        private List<ComputeChunk> computeChunks;

        @JsonProperty("network_chunk")
        private List<NetworkChunk> networkChunks;

        @JsonProperty("radio_chunk")
        private List<RadioChunk> radioChunks;

        @JsonCreator
        public SliceBlueprint(@JsonProperty("compute_chunk") List<ComputeChunk> computeChunks,
                              @JsonProperty("network_chunk") List<NetworkChunk> networkChunks,
                              @JsonProperty("radio_chunk") List<RadioChunk> radioChunks) {
            this.computeChunks = computeChunks;
            this.networkChunks = networkChunks;
            this.radioChunks   = radioChunks;
        }
    }

    public static class SliceTypeChunks {

        @JsonProperty("id")
        private String id;

        @JsonProperty("slice_blueprint")
        private SliceBlueprint sliceBlueprint;

        @JsonCreator
        public SliceTypeChunks(@JsonProperty("id") String id,
                               @JsonProperty("slice_blueprint") SliceBlueprint sliceBlueprint) {
            this.id             = id;
            this.sliceBlueprint = sliceBlueprint;
        }

        public String getId() { return id; }

        public SliceBlueprint getSliceBlueprint() { return sliceBlueprint; }
    }
}
