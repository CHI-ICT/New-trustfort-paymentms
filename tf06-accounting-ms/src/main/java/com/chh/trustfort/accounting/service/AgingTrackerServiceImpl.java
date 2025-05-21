package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgingTrackerServiceImpl implements AgingTrackerService {

    private final ReceivableRepository receivableRepository;

    @Override
    public List<DebtAgingSummaryRow> generateAgingSummary() {
        List<Receivable> receivables = receivableRepository.findByStatusNot(ReceivableStatus.PAID);

        Map<String, DebtAgingSummaryRow> summaryMap = new HashMap<>();

        for (Receivable rec : receivables) {
            String customer = rec.getCustomerName();
            BigDecimal amount = rec.getOutstandingAmount(); // assuming this field reflects unpaid portion
            LocalDate dueDate = LocalDate.from(rec.getDueDate());

            long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

            DebtAgingSummaryRow row = summaryMap.getOrDefault(customer, new DebtAgingSummaryRow(customer, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

            if (daysOverdue <= 0) {
                row.setCurrent(row.getCurrent().add(amount));
            } else if (daysOverdue <= 30) {
                row.setDays30(row.getDays30().add(amount));
            } else if (daysOverdue <= 60) {
                row.setDays60(row.getDays60().add(amount));
            } else if (daysOverdue <= 90) {
                row.setDays90(row.getDays90().add(amount));
            } else {
                row.setOver90(row.getOver90().add(amount));
            }

            summaryMap.put(customer, row);
        }

        return new ArrayList<>(summaryMap.values());
    }
}
