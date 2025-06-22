package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultiCurrencyReportServiceImpl implements MultiCurrencyReportService {

    private final ReceiptRepository receiptRepository;

    @Override
    public List<MultiCurrencyReportRow> getAllConvertedReceipts(String baseCurrency) {
        List<Receipt> receipts = receiptRepository.findAll();

        return receipts.stream()
                .map(receipt -> {
                    BigDecimal rate = receipt.getExchangeRate();
                    BigDecimal baseAmount = receipt.getAmount().multiply(rate);

                    return MultiCurrencyReportRow.builder()
                            .receiptNumber(receipt.getReceiptNumber())
                            .payerName(receipt.getPayerName())
                            .currency(receipt.getCurrency())
                            .originalAmount(receipt.getAmount())
                            .exchangeRate(rate)
                            .baseAmount(baseAmount)
                            .receiptDate(receipt.getReceiptDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
