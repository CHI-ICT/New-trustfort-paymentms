package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.ScheduleType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentSchedule;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentScheduleRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSchedulerService {

    private final PayableInvoiceRepository invoiceRepository;
    private final PaymentScheduleRepository scheduleRepository;
    private final AesService aesService;
    private final Gson gson;

    @Transactional
    public String scheduleInvoicePayment(Long invoiceId, int numberOfInstallments, AppUser appUser) {
        log.info("📅 Scheduling {} installments for invoice ID: {}", numberOfInstallments, invoiceId);

        PayableInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        if (!invoice.getStatus().equals(InvoiceStatus.APPROVED)) {
            throw new IllegalStateException("Only approved invoices can be scheduled for payment");
        }

        List<PaymentSchedule> existingSchedules = scheduleRepository.findByInvoiceId(invoiceId);
        if (!existingSchedules.isEmpty()) {
            log.warn("⚠️ Schedules already exist for invoice {}", invoiceId);
            throw new IllegalStateException("Schedules already exist for this invoice.");
        }

        BigDecimal installmentAmount = invoice.getAmount()
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, BigDecimal.ROUND_HALF_UP);

        for (int i = 0; i < numberOfInstallments; i++) {
            PaymentSchedule schedule = PaymentSchedule.builder()
                    .invoice(invoice)
                    .amount(installmentAmount)
                    .dueDate(invoice.getDueDate().plusWeeks(i))
                    .type(ScheduleType.INSTALLMENT)
                    .approvalStatus(ApprovalStatus.PENDING_EXECUTIVE_APPROVAL)
                    .paid(false)
                    .build();
            scheduleRepository.save(schedule);
        }

        List<PaymentSchedule> result = scheduleRepository.findByInvoiceId(invoiceId);
        log.info("✅ Successfully created {} installment(s) for invoice {}", result.size(), invoiceId);

        return aesService.encrypt(
                SecureResponseUtil.success("Installment schedule created successfully", result),
                appUser
        );
    }
}