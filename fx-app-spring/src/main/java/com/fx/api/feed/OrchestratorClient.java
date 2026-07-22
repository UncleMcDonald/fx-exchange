package com.fx.api.feed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrchestratorClient {

    private static final Logger log = LoggerFactory.getLogger(OrchestratorClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrchestratorClient(RestTemplate restTemplate,
                              @Value("${fx.orchestrator.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void ack(long batchId, String status) {
        try {
            restTemplate.postForEntity(baseUrl + "/api/feed/ack", new AckMessage(batchId, status), Void.class);
            log.info("ACK sent for batch {}: {}", batchId, status);
        } catch (RestClientException ex) {
            log.warn("Could not ACK batch {} as {}: {}", batchId, status, ex.getMessage());
        }
    }
}
