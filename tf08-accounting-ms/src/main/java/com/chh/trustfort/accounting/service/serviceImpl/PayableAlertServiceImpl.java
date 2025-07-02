package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PayableAlertService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayableAlertServiceImpl implements PayableAlertService {

    private final PayableInvoiceRepository payableInvoiceRepository;
    private final AesService aesService;
    private final Gson gson;

    public String generateAlerts(AppUser user) {
        List<String> alerts = new ArrayList<>();

        List<PayableInvoice> dueToday = payableInvoiceRepository.findByDueDateAndStatusNot(
                LocalDate.now(), InvoiceStatus.PAID);
        for (PayableInvoice invoice : dueToday) {
            alerts.add("INVOICE " + invoice.getInvoiceNumber() + " is due today.");
        }

        List<PayableInvoice> overdue = payableInvoiceRepository.findByDueDateBeforeAndStatusNot(
                LocalDate.now(), InvoiceStatus.PAID);
        for (PayableInvoice invoice : overdue) {
            alerts.add("INVOICE " + invoice.getInvoiceNumber() + " is overdue!");
        }

        log.info("🛎️ Total alerts generated: {}", alerts.size());

        return aesService.encrypt(SecureResponseUtil.success("Alerts generated successfully", Map.of(
                "count", alerts.size(),
                "alerts", alerts
        )), user);
    }
}