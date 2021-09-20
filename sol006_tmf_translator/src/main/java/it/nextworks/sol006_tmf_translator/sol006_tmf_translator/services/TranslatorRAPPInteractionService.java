package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.sol006_tmf_translator.information_models.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
import it.nextworks.tmf_offering_catalog.information_models.resource.ResourceSpecificationCreate;
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

@Service
public class TranslatorRAPPInteractionService {

    private static final Logger log = LoggerFactory.getLogger(TranslatorRAPPInteractionService.class);

    private static final String protocol = "http://";

    @Value("${rapp.url}")
    private String rappUrl;

    private final ObjectMapper objectMapper;

    @Autowired
    public TranslatorRAPPInteractionService(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    private String getRequestPath(Kind kind, String id) {
        switch(kind) {
            case SPC:
                return "/SpectrumWallet/translateSpectrumResource/" + id;

            case RAD:
                return "/RadioResources/translateRadioResource/" + id;

            default:
                return null;
        }
    }

    public ResourceSpecificationCreate getFromRAPP(Kind kind, String id)
            throws SourceException, MissingEntityOnSourceException, IOException {

        String request = protocol + rappUrl + getRequestPath(kind, id);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "RAPP Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 404)
            throw new MissingEntityOnSourceException();
        else if(statusCode != 200) {
            String msg = "RAPP GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        return objectMapper.readValue(EntityUtils.toString(response.getEntity()), ResourceSpecificationCreate.class);
    }
}
