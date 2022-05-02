package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class TranslatorSliceManagerInteractionService {

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "attribute_type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = StringAttribute.class, name = "string"),
            @JsonSubTypes.Type(value = DictAttribute.class, name = "dict")
    })
    public static abstract class Attribute {

        @JsonProperty("attribute_name")
        private String attributeName;

        @JsonProperty("attribute_type")
        private String attributeType;

        @JsonProperty("attribute_description")
        private String attributeDescription;

        public String getAttributeName() { return attributeName; }

        public String getAttributeType() { return attributeType; }

        public String getAttributeDescription() { return attributeDescription; }
    }

    public static class StringAttribute extends Attribute {

        @JsonProperty("attribute_value")
        private String attributeValue;

        public StringAttribute(){}

        public String getAttributeValue() { return attributeValue; }
    }

    public static class DictAttribute extends Attribute {

        @JsonProperty("attribute_value")
        private HashMap<String, String> attributeValue;

        public DictAttribute(){}

        public HashMap<String, String> getAttributeValue() { return attributeValue; }
    }

    public static class ConfigurableParameter {

        @JsonProperty("parameter_name")
        private String parameterName;

        @JsonProperty("parameter_type")
        private String parameterType;

        @JsonProperty("parameter_description")
        private String parameterDescription;

        public ConfigurableParameter(){}

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

        public SliceType(){}

        public String getUserId() { return userId; }

        public String getSliceTempId() { return sliceTempId; }

        public String getName() { return name; }

        public String getVersion() { return version; }

        public String getDescription() { return description; }

        public List<Attribute> getAttributes() { return attributes; }

        public List<ConfigurableParameter> getConfigurableParameters() { return configurableParameters; }

        public String getId() { return id; }

        public String getStatus() { return status; }
    }

    public static class RequirementsValue {

        @JsonProperty("required")
        private String required;

        @JsonProperty("units")
        private String units;

        public RequirementsValue(){}

        public String getRequired() { return required; }

        public String getUnits() { return units; }
    }

    public static class ComputeChunkRequirements {

        @JsonProperty("cpus")
        private RequirementsValue cpus;

        @JsonProperty("ram")
        private RequirementsValue ram;

        @JsonProperty("storage")
        private RequirementsValue storage;

        public ComputeChunkRequirements(){}

        public RequirementsValue getCpus() { return cpus; }

        public RequirementsValue getRam() { return ram; }

        public RequirementsValue getStorage() { return storage; }
    }

    public static class ComputeChunk {

        @JsonProperty("compute_id")
        private String computeId;

        @JsonProperty("requirements")
        private ComputeChunkRequirements computeChunkRequirements;

        public ComputeChunk(){}

        public String getComputeId() { return computeId; }

        public ComputeChunkRequirements getComputeChunkRequirements() { return computeChunkRequirements; }
    }

    public static class NetworkChunkRequirements {

        @JsonProperty("bandwidth")
        private RequirementsValue bandwidth;

        public NetworkChunkRequirements(){}

        public RequirementsValue getBandwidth() { return bandwidth; }
    }

    public static class NetworkChunk {

        @JsonProperty("physical_network_id")
        private String physicalNetworkId;

        @JsonProperty("role")
        private String role;

        @JsonProperty("tag")
        private String tag;

        @JsonProperty("requirements")
        private NetworkChunkRequirements networkChunkRequirements;

        public NetworkChunk(){}

        public String getPhysicalNetworkId() { return physicalNetworkId; }

        public String getRole() { return role; }

        public String getTag() { return tag; }

        public NetworkChunkRequirements getNetworkChunkRequirements() { return networkChunkRequirements; }
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

        public SelectedLink(){}

        public String getId() { return id; }

        public String getKey() { return key; }

        public String getSrcPhyId() { return srcPhyId; }

        public String getDstPhyId() { return dstPhyId; }
    }

    public static class Config {

        @JsonProperty("selectedRFPort")
        private String selectedRFPort;

        @JsonProperty("cellId")
        private String cellId;

        @JsonProperty("physicalCellId")
        private String physicalCellId;

        @JsonProperty("type")
        private String type;

        @JsonProperty("earfcnDl")
        private String earfcnDl;

        @JsonProperty("tac")
        private String tac;

        @JsonProperty("rootSeqIndex")
        private String rootSeqIndex;

        @JsonProperty("bandwidth")
        private String bandwidth;

        @JsonProperty("tddConfig")
        private String tddConfig;

        @JsonProperty("cellGain")
        private String cellGain;

        @JsonProperty("cellNSARelationship")
        private List<String> cellNSARelationship;

        public Config() {}

        public String getSelectedRFPort() { return selectedRFPort; }

        public String getCellId() { return cellId; }

        public String getPhysicalCellId() { return physicalCellId; }

        public String getType() { return type; }

        public String getEarfcnDl() { return earfcnDl; }

        public String getTac() { return tac; }

        public String getRootSeqIndex() { return rootSeqIndex; }

        public String getBandwidth() { return bandwidth; }

        public String getTddConfig() { return tddConfig; }

        public String getCellGain() { return cellGain; }

        public List<String> getCellNSARelationship() { return cellNSARelationship; }
    }

    public static class SelectedPhy {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("friendlyName")
        private String friendlyName;

        @JsonProperty("type")
        private String type;

        @JsonProperty("config")
        private Config config;

        @JsonProperty("box_name")
        private String boxName;

        public SelectedPhy(){}

        public String getId() { return id; }

        public String getName() { return name; }

        public String getFriendlyName() { return friendlyName; }

        public String getType() { return type; }

        public Config getConfig() { return config; }

        public String getBoxName() { return boxName; }
    }

    public static class ChunkTopology {

        @JsonProperty("selectedLinks")
        private List<SelectedLink> selectedLinks;

        @JsonProperty("selectedPhys")
        private List<SelectedPhy> selectedPhys;

        public ChunkTopology(){}

        public List<SelectedLink> getSelectedLinks() { return selectedLinks; }

        public List<SelectedPhy> getSelectedPhys() { return selectedPhys; }
    }

    public static class RadioChunk {

        @JsonProperty("ran_infra_id")
        private String ranInfraId;

        @JsonProperty("chunk_topology")
        private ChunkTopology chunkTopology;

        public RadioChunk(){}

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

        public SliceBlueprint(){}

        public List<ComputeChunk> getComputeChunks() { return computeChunks; }

        public List<NetworkChunk> getNetworkChunks() { return networkChunks; }

        public List<RadioChunk> getRadioChunks() { return radioChunks; }
    }

    public static class SliceTypeChunks {

        @JsonProperty("id")
        private String id;

        @JsonProperty("slice_blueprint")
        private SliceBlueprint sliceBlueprint;

        public SliceTypeChunks(){}

        public String getId() { return id; }

        public SliceBlueprint getSliceBlueprint() { return sliceBlueprint; }
    }

    private static final Logger log = LoggerFactory.getLogger(TranslatorSliceManagerInteractionService.class);

    private final ObjectMapper objectMapper;

    private static final String protocol = "http://";

    @Value("${slice_manager_url}")
    private String sliceManagerURL;

    @Autowired
    public TranslatorSliceManagerInteractionService(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    private static String getFromSliceManager(String request)
            throws SourceException, MissingEntityOnSourceException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "Slice Manager Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 404)
            throw new MissingEntityOnSourceException();
        else if(statusCode != 200) {
            String msg = "Slice Manager GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        return EntityUtils.toString(response.getEntity());
    }

    public SliceType getSliceType(String sliceTypeId)
            throws SourceException, MissingEntityOnSourceException, IOException {
        String request = protocol + sliceManagerURL + "/api/v1.0/slic3_type/" + sliceTypeId;
        return objectMapper.readValue(getFromSliceManager(request), SliceType.class);
    }

    public SliceTypeChunks getSliceBlueprint(String sliceTypeId)
            throws SourceException, IOException, MissingEntityOnSourceException {
        String request = protocol + sliceManagerURL + "/api/v1.0/slic3_type/" + sliceTypeId + "/slice_blueprint";
        return objectMapper.readValue(getFromSliceManager(request), SliceTypeChunks.class);
    }
}
