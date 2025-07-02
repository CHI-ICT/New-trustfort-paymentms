package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Responses.DebtAgingSummaryResponse;
import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.AgingTrackerService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgingTrackerServiceImpl implements AgingTrackerService {

    private final ReceivableRepository receivableRepository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    @Override
    public String generateDebtAgingSummary(AppUser appUser) {
        DebtAgingSummaryResponse response = new DebtAgingSummaryResponse();
        response.setResponseCode("06");
        response.setResponseMessage("No data found");

        List<Receivable> receivables = receivableRepository.findByStatusNot(ReceivableStatus.PAID);
        if (receivables.isEmpty()) {
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        Map<String, DebtAgingSummaryRow> summaryMap = new HashMap<>();

        for (Receivable rec : receivables) {
            String customer = rec.getCustomerName();
            BigDecimal amount = rec.getOutstandingAmount();
            LocalDate dueDate = LocalDate.from(rec.getDueDate());

            long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

            DebtAgingSummaryRow row = summaryMap.getOrDefault(
                    customer,
                    new DebtAgingSummaryRow(customer, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
            );

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

        response.setResponseCode("00");
        response.setResponseMessage(messageSource.getMessage("debt.aging.success", null, Locale.ENGLISH));
        response.setRows(new ArrayList<>(summaryMap.values()));

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
