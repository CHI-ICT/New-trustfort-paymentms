// --- DebtorReportServiceImpl.java ---
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtorReportRow;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtorReportServiceImpl implements DebtorReportService {

    private final ReceivableRepository receivableRepository;

    @Override
    public List<DebtorReportRow> generateDebtorReport() {
        List<Receivable> overdueReceivables = receivableRepository
                .findOverdueReceivables(ReceivableStatus.PAID, java.time.LocalDate.now());

        return overdueReceivables.stream()
                .map(receivable -> DebtorReportRow.builder()
                        .customerName(receivable.getCustomerName())
                        .customerEmail(receivable.getCustomerEmail())
                        .totalAmount(receivable.getAmount())
                        .outstandingAmount(receivable.getBalance())
                        .dueDate(LocalDate.from(receivable.getDueDate()))
                        .status(receivable.getStatus().name())
                        .currency(receivable.getCurrency())
                        .build())
                .collect(Collectors.toList());
    }
}

