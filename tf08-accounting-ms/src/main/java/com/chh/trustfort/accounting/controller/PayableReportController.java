package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.PayableInvoiceReportDTO;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;



import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payables Report", description = "Generate reports for payables with filters")
@Slf4j
public class PayableReportController {

    private final PayableInvoiceRepository invoiceRepo;
    private final AesService aesService;
    private final RequestManager requestManager;
    private final Gson gson;

    @GetMapping(value = ApiPath.GET_PAYABLES_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPayablesReport(
            @RequestParam String idToken,
            @RequestParam(required = false) String vendorEmail,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) PayoutCategory payoutCategory,
            HttpServletRequest request
    ) {
        Quintuple<Boolean, String, String, AppUser, String> validation = requestManager.validateRequest(
                Role.VIEW_PAYABLE_REPORT.getValue(), null, request, idToken
        );

        if (validation.isError) {
            OmniResponsePayload error = gson.fromJson(validation.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        List<PayableInvoiceReportDTO> report = invoiceRepo.fetchFilteredReports(vendorEmail, status, payoutCategory);

        String encryptedResponse = aesService.encrypt(
                SecureResponseUtil.success("Report fetched successfully", report),
                validation.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }
}
