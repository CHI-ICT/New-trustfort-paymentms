package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.CashflowForecastRequest;
import com.chh.trustfort.accounting.dto.CashflowForecastResponse;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.service.CashflowForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashflowForecastServiceImpl implements CashflowForecastService {

    private final PayableInvoiceRepository payableInvoiceRepository;
    private final ReceivableRepository receivableRepository;

    @Override
    public List<CashflowForecastResponse> generateForecast(CashflowForecastRequest request) {
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        log.info("📊 Generating cashflow forecast between {} and {}", start, end);

        List<Receivable> receivables = receivableRepository.findByDueDateBetweenAndStatusNot(start, end, ReceivableStatus.PAID);
        List<PayableInvoice> payables = payableInvoiceRepository.findByDueDateBetweenAndPaidFalse(start, end);

        Map<LocalDate, BigDecimal> receiptMap = receivables.stream()
                .collect(Collectors.groupingBy(
                        Receivable::getDueDate,
                        Collectors.reducing(BigDecimal.ZERO, Receivable::getAmount, BigDecimal::add)
                ));

        Map<LocalDate, BigDecimal> payableMap = payables.stream()
                .collect(Collectors.groupingBy(
                        PayableInvoice::getDueDate,
                        Collectors.reducing(BigDecimal.ZERO, PayableInvoice::getAmount, BigDecimal::add)
                ));

        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(receiptMap.keySet());
        allDates.addAll(payableMap.keySet());

        List<CashflowForecastResponse> forecastList = new ArrayList<>();

        for (LocalDate date : allDates) {
            BigDecimal expectedInflow = receiptMap.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal expectedOutflow = payableMap.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal net = expectedInflow.subtract(expectedOutflow);

            forecastList.add(CashflowForecastResponse.builder()
                    .date(date)
                    .expectedReceipt(expectedInflow)
                    .expectedPayable(expectedOutflow)
                    .netCashflow(net)
                    .build());
        }

        log.info("✅ Forecast generation completed with {} data points", forecastList.size());
        return forecastList;
    }
}
