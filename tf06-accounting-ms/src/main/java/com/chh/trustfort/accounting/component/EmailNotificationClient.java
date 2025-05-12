package com.chh.trustfort.accounting.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class EmailNotificationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${email.notification.url}")
    private String emailBaseUrl;

    public void sendEmail(String recipient, String subject, String body, boolean isAttachment) {
        try {
            MultiValueMap<String, Object> payload = new LinkedMultiValueMap<>();
            payload.add("recipient", recipient);
            payload.add("subject", subject);
            payload.add("body", body);
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

        } catch (Exception e) {
            log.error("❌ Error sending email: {}", e.getMessage(), e);
        }
    }
}
