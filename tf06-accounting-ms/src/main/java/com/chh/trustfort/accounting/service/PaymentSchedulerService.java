package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.ScheduleType;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentSchedule;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSchedulerService {

    private final PayableInvoiceRepository invoiceRepository;
    private final PaymentScheduleRepository scheduleRepository;

    public List<PaymentSchedule> scheduleInvoicePayment(Long invoiceId, int numberOfInstallments) {
        PayableInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        if (!invoice.getStatus().equals(InvoiceStatus.APPROVED)) {
            throw new IllegalStateException("Only approved invoices can be scheduled for payment");
        }

        // ✅ Prevent duplicates
        List<PaymentSchedule> existingSchedules = scheduleRepository.findByInvoiceId(invoiceId);
        if (!existingSchedules.isEmpty()) {
            log.warn("Schedules already exist for invoice {}", invoiceId);
            throw new IllegalStateException("Schedules already exist for this invoice.");
        }

        BigDecimal installmentAmount = invoice.getAmount().divide(BigDecimal.valueOf(numberOfInstallments), 2, BigDecimal.ROUND_HALF_UP);

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

        log.info("Created {} installment schedules for invoice {}", numberOfInstallments, invoiceId);
        return scheduleRepository.findByInvoiceId(invoiceId);
    }
}
