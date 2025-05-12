package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@Component
@Slf4j
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${email.notification.url}")
    private String emailApiUrl;

    public void sendEmail(EmailDetails emailDetails) {
        try {
            MultiValueMap<String, Object> payload = new LinkedMultiValueMap<>();
            payload.add("recipient", emailDetails.getRecipient());
            payload.add("subject", emailDetails.getSubject());
            payload.add("body", emailDetails.getBody());
            payload.add("isAttachment", "false"); // if no file is attached

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(emailApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email successfully sent to {}", emailDetails.getRecipient());
            } else {
                log.error("❌ Failed to send email to {}. Status: {}", emailDetails.getRecipient(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("❌ Exception occurred while sending email to {}: {}", emailDetails.getRecipient(), e.getMessage());
        }
    }

}
