// 3. SERVICE: InstallmentService.java
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.InstallmentRequestDto;
import com.chh.trustfort.accounting.enums.InstallmentStatus;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentInstallment;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentInstallmentRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstallmentService {

    private final PaymentInstallmentRepository installmentRepository;
    private final PayableInvoiceRepository payableInvoiceRepository;
    private final AesService aesService;
    private final Gson gson;

    public String generateInstallments(InstallmentRequestDto request, AppUser user) {
        Long invoiceId = request.getInvoiceId();
        int numberOfInstallments = request.getNumberOfInstallments();

        PayableInvoice invoice = payableInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        boolean alreadyExists = installmentRepository.existsByInvoiceId(invoiceId);
        if (alreadyExists) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Installments already exist for this invoice", "409", "FAIL"), user);
        }

        BigDecimal installmentAmount = invoice.getAmount()
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        LocalDate startDate = invoice.getDueDate();

        List<PaymentInstallment> installments = new ArrayList<>();
        for (int i = 0; i < numberOfInstallments; i++) {
            PaymentInstallment installment = PaymentInstallment.builder()
                    .invoice(invoice)
                    .amount(installmentAmount)
                    .dueDate(startDate.plusMonths(i))
                    .status(InstallmentStatus.PENDING)
                    .build();
            installments.add(installment);
        }

        installmentRepository.saveAll(installments);
        log.info("✅ Generated {} installments for invoice {}", numberOfInstallments, invoice.getInvoiceNumber());

        Map<String, Object> response = new HashMap<>();
        response.put("invoiceId", invoice.getId());
        response.put("installments", numberOfInstallments);
        response.put("invoiceDetails", Map.of(
                "invoiceNumber", invoice.getInvoiceNumber(),
                "vendorName", invoice.getVendorName(),
                "amount", invoice.getAmount(),
                "dueDate", invoice.getDueDate(),
                "status", invoice.getStatus()
        ));

        return aesService.encrypt(SecureResponseUtil.success(
                "Payment installments generated successfully.", response), user);
    }
}