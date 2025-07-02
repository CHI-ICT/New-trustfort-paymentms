package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtorReportDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtorAuditReportService {

    private final ReceivableRepository receivableRepository;
    private final AesService aesService;
    private final Gson gson;

    public String generateDebtorAuditReport(AppUser appUser) {
        List<Receivable> all = receivableRepository.findAll();
        Map<String, DebtorReportDTO> grouped = new HashMap<>();

        for (Receivable r : all) {
            if (r.getPayerEmail() == null) continue;

            String key = r.getPayerEmail();
            DebtorReportDTO dto = grouped.getOrDefault(key, new DebtorReportDTO());
            dto.setCustomerEmail(r.getPayerEmail());
            dto.setCustomerName(r.getCustomerName());
            dto.setCurrency(r.getCurrency());
            dto.setTotalInvoiced(dto.getTotalInvoiced() == null ? r.getAmount() : dto.getTotalInvoiced().add(r.getAmount()));
            dto.setTotalPaid(dto.getTotalPaid() == null ? r.getAmountPaid() : dto.getTotalPaid().add(r.getAmountPaid()));
            dto.setOutstandingAmount(dto.getTotalInvoiced().subtract(dto.getTotalPaid()));
            dto.setAgingBucket(determineAgingBucket(r.getDueDate()));

            grouped.put(key, dto);
        }

        List<DebtorReportDTO> responseList = new ArrayList<>(grouped.values());
        return aesService.encrypt(gson.toJson(responseList), appUser);
    }

    private String determineAgingBucket(LocalDate dueDate) {
        long days = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (days <= 30) return "0–30 Days";
        else if (days <= 60) return "31–60 Days";
        else if (days <= 90) return "61–90 Days";
        else return "90+ Days";
    }
}

