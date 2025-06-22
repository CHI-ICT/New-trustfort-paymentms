package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Util.InsecureRestTemplateBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CustomEmailService {

    private final RestTemplate restTemplate = new RestTemplate();
//private final RestTemplate restTemplate = InsecureRestTemplateBuilder.build();

    @Value("${email.notification.url}")
    private String emailBaseUrl; // Injected from application.properties

    public void sendEmail(String recipient, String subject, String bodyHtml, boolean isAttachment) {
        try {
            MultiValueMap<String, Object> payload = new LinkedMultiValueMap<>();
            payload.add("recipient", recipient);
            payload.add("subject", subject);
            payload.add("body", bodyHtml);
            payload.add("isAttachment", String.valueOf(isAttachment));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(emailBaseUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email sent successfully to {}", recipient);
            } else {
                log.error("❌ Failed to send email to {}. Status: {}", recipient, response.getStatusCode());
            }

        } catch (Exception ex) {
            log.error("❌ Exception while sending email to {}: {}", recipient, ex.getMessage(), ex);
        }
    }
}
