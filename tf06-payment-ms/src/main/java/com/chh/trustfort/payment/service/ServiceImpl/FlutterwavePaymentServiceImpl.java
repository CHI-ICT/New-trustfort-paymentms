package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.security.AesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlutterwavePaymentServiceImpl {

    @Value("${flutterwave.secret-key}")
    private String FLW_SECRET_KEY;

    @Value("${flutterwave.initiate-url}")
    private String FLW_PAYMENT_URL;

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final AesService aesService;

    public String initiateFlutterwavePayment(FundWalletRequestPayload request, AppUser appUser) {
        String txRef = "FLW-" + System.currentTimeMillis();
        log.info("🔁 Generated Flutterwave Transaction Reference: {}", txRef);
        String email = appUser.getEmail() != null ? appUser.getEmail() : appUser.getPhoneNumber() + "@chi.com";
// assumed available from AppUser
        String phone = appUser.getPhoneNumber();
        String name = appUser.getUserName() != null ? appUser.getUserName() : "Wallet User";

        Map<String, Object> payload = new HashMap<>();
        payload.put("tx_ref", txRef);
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency());
        payload.put("redirect_url", "https://www.flutterwave.com");

        Map<String, String> customer = new HashMap<>();
        customer.put("email", email);
        customer.put("phonenumber", phone);
        customer.put("name", name);
        payload.put("customer", customer);

        List<Map<String, String>> meta = new ArrayList<>();
        meta.add(Map.of("metaname", "userId", "metavalue", appUser.getUserName()));
        payload.put("meta", meta);

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FLW_SECRET_KEY);

        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(payload), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(FLW_PAYMENT_URL, entity, String.class);

        JsonObject responseBody = JsonParser.parseString(response.getBody()).getAsJsonObject();

        String paymentLink = responseBody.getAsJsonObject("data").get("link").getAsString();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", responseBody.get("status").getAsString());
        responseMap.put("message", responseBody.get("message").getAsString());
        responseMap.put("paymentLink", paymentLink);

        return aesService.encrypt(gson.toJson(responseMap), appUser);
    }
}
