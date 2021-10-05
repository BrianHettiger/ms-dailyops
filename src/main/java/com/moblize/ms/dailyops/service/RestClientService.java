package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.service.dto.DPVAData;
import com.moblize.ms.dailyops.service.dto.DPVAResult;
import com.moblize.ms.dailyops.service.dto.ProcessPerFeetRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class RestClientService {
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${rest.wellformationetl.url}")
    private String wellformationetlUrl;
    @Value("${rest.wellformationetl.user}")
    private String wellformationetlUser;
    @Value("${rest.wellformationetl.pwd}")
    private String wellformationetlPassword;
    @Value("${rest.nodedrilling.url}")
    private String nodedrillingUrl;
    @Value("${rest.nodedrilling.user}")
    private String nodedrillingUser;
    @Value("${rest.nodedrilling.pwd}")
    private String nodedrillingPassword;
    private String processPerformanceMapPath = "performance/well";

    @Value("${rest.analytics.service.url}")
    private String analyticsServiceUrl;
    @Value("${rest.analytics.service.user}")
    private String analyticsServiceUser;
    @Value("${rest.analytics.service.pwd}")
    private String analyticsServicePassword;
    @Value("${rest.analytics.service.processPerFeetData}")
    private String processPerFeetData;

    private String nodedrillingStomp = "stomp/";
    @Value("${CODE}")
    private String customer;

    public ResponseEntity processWell(final MongoWell well) {
        final Long startIndex = System.currentTimeMillis();
        final String resetUrl = wellformationetlUrl + processPerformanceMapPath;
        final HttpEntity<MongoWell> request = new HttpEntity<MongoWell>(well, createHeaders(wellformationetlUser, wellformationetlPassword));
        return restTemplate.exchange(resetUrl, HttpMethod.POST, request, MongoWell.class);
    }
    @Async
    public ResponseEntity sendMessage(String topic, String message) {
        final String stompUrl = nodedrillingUrl + nodedrillingStomp + topic;
        final HttpEntity<String> request = new HttpEntity<String>(message, createHeaders(nodedrillingUser, nodedrillingPassword));
        return restTemplate.exchange(stompUrl, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity sendDataToNodeSocket(DPVAResult dpvaResult) {
        final String stompUrl = nodedrillingUrl + nodedrillingStomp+"dpvaData" ;
        final HttpEntity<DPVAData> request = new HttpEntity<>(dpvaResult.getPrimaryWellDPVAData(), createHeaders(nodedrillingUser, nodedrillingPassword));
        return restTemplate.exchange(stompUrl, HttpMethod.POST, request, String.class);
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
            set("Content-Type", "application/json");
        }};
    }

    @Async
    public ResponseEntity processPerFeetData(ProcessPerFeetRequestDTO processPerFeetRequestDTO){
       final Long startIndex = System.currentTimeMillis();
        ResponseEntity responseEntity = null;
        try {
            final String resetUrl = analyticsServiceUrl + processPerFeetData;
            final HttpEntity<ProcessPerFeetRequestDTO> request = new HttpEntity<ProcessPerFeetRequestDTO>(processPerFeetRequestDTO, createHeaders(analyticsServiceUser, analyticsServicePassword));
            responseEntity = restTemplate.exchange(resetUrl, HttpMethod.POST, request, String.class);
            log.info("Process per feet data API took {}",System.currentTimeMillis()-startIndex);
        } catch (RestClientException e) {
            log.error("Error occur in processPerFeetData API call {}", processPerFeetRequestDTO.getWellUid(), e);
        }
        return responseEntity;
    }
}
