package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.ReportViewerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EncryptResponse
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API )
@Slf4j
@Tag(name = "Report Viewer", description = "Generate and preview financial reports")
public class ReportViewerController {

    private final ReportViewerService reportViewerService;

    /**
     * Preview report data for a given report type and filter
     */
    @PostMapping(value = ApiPath.REPORT_VIEWER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReportViewerResponse>> getReportData(
            @RequestParam String reportType,
            @RequestBody StatementFilterDTO filter
    ) {
        log.info("Fetching viewer data for reportType: {}", reportType);
        List<ReportViewerResponse> response = reportViewerService.getReportData(reportType, filter);
        return ResponseEntity.ok(response);
    }
}
