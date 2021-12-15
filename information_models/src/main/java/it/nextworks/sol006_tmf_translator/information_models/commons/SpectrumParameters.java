package it.nextworks.sol006_tmf_translator.information_models.commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpectrumParameters {

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("radius")
    private String radius;

    @JsonProperty("name")
    private String name;

    @JsonProperty("locality")
    private String locality;

    @JsonCreator
    public SpectrumParameters(@JsonProperty("city") String city,
                              @JsonProperty("country") String country,
                              @JsonProperty("lat") String lat,
                              @JsonProperty("lon") String lon,
                              @JsonProperty("radius") String radius,
                              @JsonProperty("name") String name,
                              @JsonProperty("locality") String locality) {
        this.city     = city;
        this.country  = country;
        this.lat      = lat;
        this.lon      = lon;
        this.radius   = radius;
        this.name     = name;
        this.locality = locality;
    }

    public String getCity() { return city; }

    public String getCountry() { return country; }

    public String getLat() { return lat; }

    public String getLon() { return lon; }

    public String getRadius() { return radius; }

    public String getName() { return name; }

    public String getLocality() { return locality; }
}
