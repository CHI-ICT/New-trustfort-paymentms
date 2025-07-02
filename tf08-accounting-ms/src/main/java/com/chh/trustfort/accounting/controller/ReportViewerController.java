package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReportViewerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Report Viewer", description = "Generate and preview financial reports")
public class ReportViewerController {

    private final ReportViewerService reportViewerService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.REPORT_VIEWER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReportData(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_VIEWER.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        JsonObject json = JsonParser.parseString(request.payload).getAsJsonObject();
        String reportType = json.get("reportType").getAsString();
        StatementFilterDTO filter = gson.fromJson(json.get("filter"), StatementFilterDTO.class);

        log.info("📊 Generating report viewer data for type: {}", reportType);
        List<ReportViewerResponse> result = reportViewerService.getReportData(reportType, filter);

        return ResponseEntity.ok(aesService.encrypt(gson.toJson(result), request.appUser));
    }
}

