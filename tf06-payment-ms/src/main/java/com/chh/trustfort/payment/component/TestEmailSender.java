package com.chh.trustfort.payment.component;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class TestEmailSender {

    public void sendEmailManually() {
        String url = "https://test.chiplc.com:8443/trustfort/api/v1/adminService/send-email?htmlStatus=true";

        // Construct the payload (form-data format)
        Map<String, Object> formData = new HashMap<>();
        formData.put("recipient", "tobiajayi60@gmail.com");
        formData.put("subject", "📧 Direct Email Test");
        formData.put("body", "This is a direct test to check if recipient is overridden.");
        formData.put("isAttachment", "false");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Use MultiValueMap for form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        formData.forEach(body::add);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("✅ Response Status: " + response.getStatusCode());
            System.out.println("✅ Response Body: " + response.getBody());
        } catch (Exception ex) {
            System.out.println("❌ Error occurred: " + ex.getMessage());
        }
    }
}
