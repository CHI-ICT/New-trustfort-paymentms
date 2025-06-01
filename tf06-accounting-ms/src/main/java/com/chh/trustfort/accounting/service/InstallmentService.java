// 3. SERVICE: InstallmentService.java
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.InstallmentStatus;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentInstallment;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentInstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstallmentService {

    private final PaymentInstallmentRepository installmentRepository;

    private final PayableInvoiceRepository payableInvoiceRepository;
    private final PaymentInstallmentRepository paymentInstallmentRepository;

    public void generateInstallments(Long invoiceId, int numberOfInstallments) {
        PayableInvoice invoice = payableInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        // Check if installments already exist for this invoice
        boolean alreadyExists = paymentInstallmentRepository.existsByInvoiceId(invoiceId);
        if (alreadyExists) {
            throw new IllegalStateException("Installments have already been generated for this invoice.");
        }

        BigDecimal installmentAmount = invoice.getAmount().divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        LocalDate startDate = invoice.getDueDate(); // or LocalDate.now()

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

        paymentInstallmentRepository.saveAll(installments);
        log.info("Generated {} installments for invoice {}", numberOfInstallments, invoice.getInvoiceNumber());
    }

    public List<PaymentInstallment> getInstallmentsByInvoice(Long invoiceId) {
        return installmentRepository.findByInvoiceId(invoiceId);
    }
}