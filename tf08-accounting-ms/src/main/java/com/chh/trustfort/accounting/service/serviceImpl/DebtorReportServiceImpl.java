// --- DebtorReportServiceImpl.java ---
package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.DebtorReportRow;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.DebtorReportService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtorReportServiceImpl implements DebtorReportService {

    private final ReceivableRepository receivableRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String generateDebtorReport(AppUser appUser) {
        List<Receivable> overdueReceivables = receivableRepository
                .findOverdueReceivables(ReceivableStatus.PAID, LocalDate.now());

        List<DebtorReportRow> report = overdueReceivables.stream()
                .map(receivable -> DebtorReportRow.builder()
                        .customerName(receivable.getCustomerName())
                        .payerEmail(receivable.getPayerEmail())
                        .totalAmount(receivable.getAmount())
                        .outstandingAmount(receivable.getBalance())
                        .dueDate(receivable.getDueDate())
                        .status(receivable.getStatus().name())
                        .currency(receivable.getCurrency())
                        .build())
                .collect(Collectors.toList());

        return aesService.encrypt(gson.toJson(report), appUser);
    }
}

