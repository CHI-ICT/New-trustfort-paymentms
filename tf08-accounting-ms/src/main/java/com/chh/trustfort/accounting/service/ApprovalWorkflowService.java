package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ApprovalWorkflow;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.payload.ApproveInvoiceRequestPayload;
import com.chh.trustfort.accounting.payload.ApproveInvoiceResponsePayload;
import com.chh.trustfort.accounting.repository.ApprovalWorkflowRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalWorkflowService {

    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final PayableInvoiceRepository payableInvoiceRepository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    public String approveInvoice(ApproveInvoiceRequestPayload request, AppUser appUser) {
        ApproveInvoiceResponsePayload response = new ApproveInvoiceResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage("Approval failed");

        if (request == null || request.getInvoiceId() == null || request.getApproverEmail() == null || request.getApproverRole() == null) {
            log.warn("❌ Missing required fields in approval request");
            response.setResponseMessage("Missing required fields");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        PayableInvoice invoice = payableInvoiceRepository.findById(request.getInvoiceId())
                .orElse(null);

        if (invoice == null) {
            log.warn("❌ Invoice not found for ID: {}", request.getInvoiceId());
            response.setResponseMessage("Invoice not found");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        invoice.setStatus(com.chh.trustfort.accounting.enums.InvoiceStatus.APPROVED);
        payableInvoiceRepository.save(invoice);

        ApprovalWorkflow workflow = ApprovalWorkflow.builder()
                .invoice(invoice)
                .approverEmail(request.getApproverEmail())
                .approverRole(request.getApproverRole())
                .status(ApprovalStatus.APPROVED)
                .comments(request.getComment())
                .actionedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy(request.getApproverEmail())
                .build();

        approvalWorkflowRepository.save(workflow);

        response.setResponseCode("00");
        response.setResponseMessage(messageSource.getMessage("approval.success", null, Locale.ENGLISH));
        response.setInvoiceId(invoice.getId());
        response.setStatus(invoice.getStatus().name());

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
