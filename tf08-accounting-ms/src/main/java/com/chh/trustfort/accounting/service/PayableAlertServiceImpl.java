package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayableAlertServiceImpl implements PayableAlertService {

    private final PayableInvoiceRepository payableInvoiceRepository;


    public List<String> generateAlerts() {
        List<String> alerts = new ArrayList<>();

        // Pending invoices due today
        List<PayableInvoice> dueToday = payableInvoiceRepository.findByDueDateAndStatusNot(
                LocalDate.now(), InvoiceStatus.PAID);
        for (PayableInvoice invoice : dueToday) {
            alerts.add("INVOICE " + invoice.getInvoiceNumber() + " is due today.");
        }

        // Overdue invoices
        List<PayableInvoice> overdue = payableInvoiceRepository.findByDueDateBeforeAndStatusNot(
                LocalDate.now(), InvoiceStatus.PAID);
        for (PayableInvoice invoice : overdue) {
            alerts.add("INVOICE " + invoice.getInvoiceNumber() + " is overdue!");
        }

        return alerts;
    }
} 
