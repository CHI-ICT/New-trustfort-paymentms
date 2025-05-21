package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ExchangeRateService exchangeRateService;

    @Override
    public Receipt generateReceipt(ReceiptGenerationRequest request) {
        String currency = request.getCurrency() != null ? request.getCurrency() : "NGN";
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(
                request.getCurrency() != null ? request.getCurrency() : "NGN", "NGN"
        );

        BigDecimal baseAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);


        Receipt receipt = new Receipt();
        receipt.setReceiptNumber("RCT-" + UUID.randomUUID());
        receipt.setPayerName(request.getPayerName());
        receipt.setPayerEmail(request.getPayerEmail());
        receipt.setAmount(request.getAmount());
        receipt.setCurrencyCode(currency);
        receipt.setCurrency(request.getCurrency() != null ? request.getCurrency() : "NGN");
        receipt.setExchangeRate(exchangeRate);
        receipt.setBaseAmount(baseAmount);
        receipt.setPaymentReference(request.getPaymentReference());
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setSource(request.getSource());
        receipt.setStatus(ReceiptStatus.CONFIRMED);
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setCreatedBy(request.getCreatedBy());

        return receiptRepository.save(receipt);
    }
}
