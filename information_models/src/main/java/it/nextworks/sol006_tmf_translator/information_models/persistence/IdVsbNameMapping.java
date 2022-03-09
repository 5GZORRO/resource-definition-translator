package it.nextworks.sol006_tmf_translator.information_models.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "id_vsb_name_mappings", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "vsb_name"}))
public class IdVsbNameMapping {

    @Id
    @JsonProperty("id")
    @Column(name = "id")
    private String id;

    @JsonProperty("vsbName")
    @Column(name = "vsb_name")
    private String vsbName;

    @JsonCreator
    public IdVsbNameMapping(@JsonProperty("id") String id,
                            @JsonProperty("vsbName") String vsbName) {
        this.id = id;
        this.vsbName = vsbName;
    }

    public IdVsbNameMapping() {}

    public IdVsbNameMapping id(String id) {
        this.id = id;
        return this;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public IdVsbNameMapping vsbName(String vsbName) {
        this.vsbName = vsbName;
        return this;
    }

    public String getVsbName() { return vsbName; }

    public void setVsbName(String vsbName) { this.vsbName = vsbName; }

    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o)
            return true;

        if(o == null || getClass() != o.getClass())
            return false;

        IdVsbNameMapping idVsbNameMapping = (IdVsbNameMapping) o;
        return Objects.equals(this.id, idVsbNameMapping.id) &&
                Objects.equals(this.vsbName, idVsbNameMapping.vsbName);
    }

    @Override
    public int hashCode() { return Objects.hash(id, vsbName); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("class IdVsbNameMapping {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    vsbName: ").append(toIndentedString(vsbName)).append("\n");
        sb.append("}");

        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if(o == null)
            return "null";

        return o.toString().replace("\n", "\n    ");
    }
}
