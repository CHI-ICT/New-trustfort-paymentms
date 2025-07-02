package com.chh.trustfort.accounting.controller.statements;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.EquityStatementService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equity Statement", description = "APIs for generating Statement of Equity reports")
public class EquityStatementController {

    private final EquityStatementService equityStatementService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.EQUITY_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEquityStatement(@RequestParam String idToken,
                                                @RequestBody String requestPayload,
                                                HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_EQUITY_STATEMENT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(
                    SecureResponseUtil.error(errorResponse.getResponseMessage(), errorResponse.getResponseCode(), "fail"),
                    request.appUser
            ));
        }

        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
        EquityStatementResponse response = equityStatementService.generateStatement(filter);

        return ResponseEntity.ok(aesService.encrypt(
                SecureResponseUtil.success("Equity Statement generated successfully", response),
                request.appUser
        ));
    }
}


