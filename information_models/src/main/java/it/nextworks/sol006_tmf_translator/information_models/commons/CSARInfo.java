package it.nextworks.sol006_tmf_translator.information_models.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.libs.descriptors.sol006.Nsd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Pnfd;
import it.nextworks.nfvmano.libs.descriptors.sol006.Vnfd;

public class CSARInfo {

    @JsonIgnore
    private Vnfd vnfd;

    @JsonIgnore
    private Pnfd pnfd;

    @JsonIgnore
    private Nsd nsd;

    @JsonProperty("packagePath")
    private String packagePath;

    @JsonProperty("descriptorFilename")
    private String descriptorFilename;

    @JsonProperty("metaFilename")
    private String metaFilename;

    @JsonProperty("mfFilename")
    private String mfFilename;

    public CSARInfo() {
    }

    public Vnfd getVnfd() { return vnfd; }

    public void setVnfd(Vnfd vnfd) { this.vnfd = vnfd; }

    public Pnfd getPnfd() { return pnfd; }

    public void setPnfd(Pnfd pnfd) { this.pnfd = pnfd; }

    public Nsd getNsd() { return nsd; }

    public void setNsd(Nsd nsd) { this.nsd = nsd; }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getDescriptorFilename() {
        return descriptorFilename;
    }

    public void setDescriptorFilename(String descriptorFilename) {
        this.descriptorFilename = descriptorFilename;
    }

    public String getMetaFilename() {
        return metaFilename;
    }

    public void setMetaFilename(String metaFilename) {
        this.metaFilename = metaFilename;
    }

    public String getMfFilename() {
        return mfFilename;
    }

    public void setMfFilename(String mfFilename) {
        this.mfFilename = mfFilename;
    }
}
