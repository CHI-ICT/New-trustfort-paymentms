package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.serviceImpl.PayableAlertServiceImpl;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payable Alerts", description = "Trigger alerts for due or overdue invoices")
@Slf4j
public class PayableAlertTestController {

    private final PayableAlertServiceImpl payableAlertService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @GetMapping(value = ApiPath.TEST_ALERTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> testAlerts(@RequestParam String idToken, HttpServletRequest request) {
        Quintuple<Boolean, String, String, AppUser, String> token = requestManager.validateRequest(
                Role.VIEW_PAYABLE_ALERTS.getValue(), null, request, idToken
        );

        if (token.isError) {
            OmniResponsePayload error = gson.fromJson(token.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        String response = payableAlertService.generateAlerts(token.appUser);
        return ResponseEntity.ok(response);
    }
}
