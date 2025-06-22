package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.PayableInvoiceReportDTO;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Payables Report", description = "Generate reports for payables with filters")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PayableReportController {

    private final PayableInvoiceRepository invoiceRepo;


    @GetMapping(value = ApiPath.GET_PAYABLES_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getPayablesReport(
            @RequestParam(required = false) String vendorEmail,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) PayoutCategory payoutCategory
    ) {
        List<PayableInvoiceReportDTO> report = invoiceRepo.fetchFilteredReports(vendorEmail, status, payoutCategory);
        return ResponseEntity.ok(ApiResponse.success("Report fetched successfully", report));
    }
}
