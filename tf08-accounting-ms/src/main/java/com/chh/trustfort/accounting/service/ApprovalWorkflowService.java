package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.model.ApprovalWorkflow;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.ApprovalWorkflowRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalWorkflowService {

    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final PayableInvoiceRepository payableInvoiceRepository;

    public ApprovalWorkflow approveInvoice(Long invoiceId, String approverEmail, String approverRole, String comment) {
        PayableInvoice invoice = payableInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        ApprovalWorkflow workflow = ApprovalWorkflow.builder()
                .invoice(invoice)
                .approverEmail(approverEmail)
                .approverRole(approverRole)
                .status(ApprovalStatus.APPROVED)
                .comments(comment)
                .actionedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy(approverEmail)
                .build();

        invoice.setStatus(com.chh.trustfort.accounting.enums.InvoiceStatus.APPROVED);
        payableInvoiceRepository.save(invoice);

        return approvalWorkflowRepository.save(workflow);
    }
}