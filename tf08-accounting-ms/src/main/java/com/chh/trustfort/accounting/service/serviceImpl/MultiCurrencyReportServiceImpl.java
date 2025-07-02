package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.MultiCurrencyReportService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultiCurrencyReportServiceImpl implements MultiCurrencyReportService {

    private final ReceiptRepository receiptRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String getAllConvertedReceipts(String baseCurrency, AppUser user) {
        List<Receipt> receipts = receiptRepository.findAll();

        List<MultiCurrencyReportRow> rows = receipts.stream()
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

        log.info("📊 {} converted receipts returned for base currency: {}", rows.size(), baseCurrency);

        return aesService.encrypt(SecureResponseUtil.success("Multi-currency receipts converted successfully", Map.of(
                "baseCurrency", baseCurrency,
                "rows", rows
        )), user);
    }
}