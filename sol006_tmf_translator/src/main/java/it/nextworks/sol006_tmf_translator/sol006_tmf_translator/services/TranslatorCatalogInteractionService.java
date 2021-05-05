package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.nextworks.sol006_tmf_translator.information_models.commons.ResourceSpecificationRef;
import it.nextworks.sol006_tmf_translator.information_models.commons.ServiceSpecificationRef;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceCandidate;
import it.nextworks.sol006_tmf_translator.information_models.resource.ResourceSpecification;
import it.nextworks.sol006_tmf_translator.information_models.service.ServiceCandidate;
import it.nextworks.sol006_tmf_translator.information_models.service.ServiceSpecification;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.config.CustomOffsetDateTimeSerializer;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.CatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.MissingEntityOnCatalogException;
import it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception.ResourceMismatchException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Service
public class TranslatorCatalogInteractionService {

    private static final Logger log = LoggerFactory.getLogger(TranslatorCatalogInteractionService.class);

    private static final String protocol = "http://";

    @Value("${offer_catalog.hostname}")
    private String catalogHostname;

    @Value("${offer_catalog.port}")
    private String catalogPort;

    @Value("${offer_catalog.contextPath}")
    private String contextPath;

    private final ObjectMapper objectMapper;

    @Autowired
    public TranslatorCatalogInteractionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        this.objectMapper.registerModule(module);
    }

    public HttpEntity getFromCatalog(String requestPath, String id)
            throws CatalogException, MissingEntityOnCatalogException {

        String request = protocol + catalogHostname + ":" + catalogPort + contextPath +
                requestPath + id;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch(IOException e) {
            String msg = "Offer Catalog Unreachable.";
            log.error(msg);
            throw new CatalogException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 404)
            throw new MissingEntityOnCatalogException();
        else if(statusCode != 200) {
            String msg = "Offer Catalog GET request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new CatalogException(msg);
        }

        return response.getEntity();
    }

    public ResourceSpecificationRef isResourcePresent(String resourceCandidateCatalogId, String resourceSpecificationCatalogId)
            throws IOException, CatalogException, MissingEntityOnCatalogException, ResourceMismatchException {

        log.info("Checking if Resource Candidate " + resourceCandidateCatalogId + " exist in the Offer Catalog.");

        HttpEntity rcEntity;
        try {
            rcEntity = getFromCatalog("/resourceCatalogManagement/v2/resourceCandidate/", resourceCandidateCatalogId);
        } catch(MissingEntityOnCatalogException e) {
            String msg = "Resource Candidate " + resourceCandidateCatalogId + " not found in Offer Catalog.";
            log.info(msg);
            throw new MissingEntityOnCatalogException(msg);
        }

        ResourceCandidate rc = Arrays.asList(objectMapper.readValue(EntityUtils.toString(rcEntity),
                ResourceCandidate[].class)).get(0);

        log.info("Checking if Resource Specification " + resourceSpecificationCatalogId + " exist in the Offer Catalog.");

        HttpEntity rsEntity;
        try {
            rsEntity = getFromCatalog("/resourceCatalogManagement/v2/resourceSpecification/",
                    resourceSpecificationCatalogId);
        } catch(MissingEntityOnCatalogException e) {
            String msg = "Resource Specification " + resourceSpecificationCatalogId + " not found in Offer Catalog.";
            log.info(msg);
            throw new MissingEntityOnCatalogException(msg);
        }

        ResourceSpecification rs = Arrays.asList(objectMapper.readValue(EntityUtils.toString(rsEntity),
                ResourceSpecification[].class)).get(0);

        ResourceSpecificationRef rsr = rc.getResourceSpecification();
        if(rsr == null)
            throw new CatalogException("Null Resource Specification Ref in Resource Candidate: abort.");

        if(!rsr.getHref().equals(rs.getHref()) || !rsr.getId().equals(rs.getId())) {
            String msg = "Mismatch between Resource Candidate and Resource Specification: " +
                    "not coupled.";
            log.info(msg);
            throw new ResourceMismatchException(msg);
        }

        return rsr;
    }

    public ServiceSpecificationRef isServicePresent(String serviceCandidateCatalogId, String serviceSpecificationCatalogId)
            throws CatalogException, IOException, MissingEntityOnCatalogException, ResourceMismatchException {

        log.info("Checking if Service Candidate " + serviceCandidateCatalogId + " exist in the Offer Catalog.");

        HttpEntity scEntity;
        try {
            scEntity = getFromCatalog("/serviceCatalogManagement/v4/serviceCandidate/", serviceCandidateCatalogId);
        } catch (MissingEntityOnCatalogException e) {
            String msg = "Service Candidate " + serviceCandidateCatalogId + " not found in Offer Catalog.";
            log.info(msg);
            throw new MissingEntityOnCatalogException(msg);
        }

        ServiceCandidate sc = objectMapper.readValue(EntityUtils.toString(scEntity), ServiceCandidate.class);

        log.info("Checking if Service Specification " + serviceSpecificationCatalogId + " exist in the Offer Catalog.");

        HttpEntity ssEntity;
        try {
            ssEntity = getFromCatalog("/serviceCatalogManagement/v4/serviceSpecification/",
                    serviceSpecificationCatalogId);
        } catch (MissingEntityOnCatalogException e) {
            String msg = "Service Specification " + serviceSpecificationCatalogId + " not found in Offer Catalog.";
            log.info(msg);
            throw new MissingEntityOnCatalogException(msg);
        }

        ServiceSpecification ss = objectMapper.readValue(EntityUtils.toString(ssEntity), ServiceSpecification.class);

        ServiceSpecificationRef ssr = sc.getServiceSpecification();
        if(ssr == null)
            throw new CatalogException("Null Service Specification Ref in Service Candidate: abort.");

        if(!ssr.getHref().equals(ss.getHref()) || !ssr.getId().equals(ss.getId())) {
            String msg = "Mismatch between Service Candidate and Service Specification: not coupled.";
            log.info(msg);
            throw new ResourceMismatchException(msg);
        }

        return ssr;
    }

    public HttpEntity post(String body, String requestPath) throws UnsupportedEncodingException, CatalogException {

        String request = protocol + catalogHostname + ":" + catalogPort + contextPath + requestPath;
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
            String msg = "Offer Catalog Unreachable.";
            log.error(msg);
            throw new CatalogException(msg);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 201) {
            String msg = "Offer Catalog POST request failed, status code: " + statusCode + ".";
            log.error(msg);
            throw new CatalogException(msg);
        }

        return response.getEntity();
    }
}
