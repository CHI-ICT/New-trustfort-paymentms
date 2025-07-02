package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.MultiCurrencyReportService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;





@RestController
@Slf4j
@Tag(name = "Multicurrency Reports", description = "Generate financial reports and integrity checks")
@RequiredArgsConstructor
public class MultiCurrencyReportController {

    private final MultiCurrencyReportService multiCurrencyReportService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @GetMapping(value = ApiPath.MULTI_CURRENCY_RECEIPTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllConvertedReceipts(
            @RequestParam String idToken,
            @RequestParam(defaultValue = "NGN") String baseCurrency,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_RECEIPTS.getValue(), null, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        String response = multiCurrencyReportService.getAllConvertedReceipts(baseCurrency.toUpperCase(), request.appUser);
        return ResponseEntity.ok(response);
    }
}
