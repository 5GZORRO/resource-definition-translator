package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.enums.Kind;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnSourceException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.SourceException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class TranslatorDescSourceInteractionService {

    private static final Logger log = LoggerFactory.getLogger(TranslatorDescSourceInteractionService.class);

    private static final String protocol = "http://";

    @Value("${descriptors_source.hostname}")
    private String sourceHostname;

    @Value("${descriptors_source.port}")
    private String sourcePort;

    @Value("${descriptors_source.vnfdPath}")
    private String sourceVnfdPath;

    @Value("${descriptors_source.pnfdPath}")
    private String sourcePnfdPath;

    @Value("${descriptors_source.nsdPath}")
    private String sourceNsPath;

    private final ObjectMapper objectMapper;

    @Autowired
    public TranslatorDescSourceInteractionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getRequestPath(Kind kind) {
        switch(kind) {
            case VNF:
                return sourceVnfdPath;

            case PNF:
                return sourcePnfdPath;

            case NS:
                return sourceNsPath;

            default:
                return null;
        }
    }

    public HttpEntity getFromSource(Kind kind, String id)
            throws SourceException, MissingEntityOnSourceException {

        String request = protocol + sourceHostname + ":" + sourcePort + getRequestPath(kind) + "/" + id;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 404)
            throw new MissingEntityOnSourceException();
        else if(statusCode != 200) {
            String msg = "Descriptors Source GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }

        return response.getEntity();
    }

    public void postOnSource(Kind kind, String body) throws UnsupportedEncodingException, SourceException {

        String request = protocol + sourceHostname + ":" + sourcePort + getRequestPath(kind);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(request);

        StringEntity stringEntity = new StringEntity(body);

        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch(IOException e) {
            String msg = "Descriptors Source Unreachable.";
            log.error(msg);
            throw new SourceException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 201) {
            String msg = "Descriptors Source POST request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new SourceException(msg);
        }
    }
}
