package com.chh.trustfort.accounting.component;

import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayableAlertScheduler {

    private final PayableInvoiceRepository payableInvoiceRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void runDailyPayableAlerts() {
        LocalDate today = LocalDate.now();

        List<PayableInvoice> pending = payableInvoiceRepository.findByStatus(InvoiceStatus.PENDING_APPROVAL);
        List<PayableInvoice> dueToday = payableInvoiceRepository.findByDueDateAndStatusNot(today, InvoiceStatus.PAID);
        List<PayableInvoice> overdue = payableInvoiceRepository.findByDueDateBeforeAndStatusNot(today, InvoiceStatus.PAID);

        log.info("[Payable Alerts] Pending Invoices: {}", pending.size());
        log.info("[Payable Alerts] Due Today: {}", dueToday.size());
        log.info("[Payable Alerts] Overdue Invoices: {}", overdue.size());
    }
}
