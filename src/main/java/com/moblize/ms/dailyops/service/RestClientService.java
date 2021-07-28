package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private String processPerformanceMapPath = "performance/well";
    @Value("${CODE}")
    private String customer;

    public ResponseEntity processWell(final MongoWell well) {
        final Long startIndex = System.currentTimeMillis();
        final String resetUrl = wellformationetlUrl + processPerformanceMapPath;
        final HttpEntity<MongoWell> request = new HttpEntity<MongoWell>(well, createHeaders(wellformationetlUser, wellformationetlPassword));
        return restTemplate.exchange(resetUrl, HttpMethod.POST, request, MongoWell.class);
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
}
