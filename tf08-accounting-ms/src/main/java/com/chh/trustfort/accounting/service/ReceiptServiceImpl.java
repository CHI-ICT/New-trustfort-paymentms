package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Utility.GLPostingUtil;
import com.chh.trustfort.accounting.enums.GLPostingType;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
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
    private final GLPostingUtil glPostingUtil;
    private final ChartOfAccountAccountRepository chartOfAccountRepository;


    @Override
    public Receipt generateReceipt(ReceiptGenerationRequest request) {
        String currency = request.getCurrency() != null ? request.getCurrency() : "NGN";
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currency, "NGN");
        BigDecimal baseAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        // ✅ Lookup Chart of Account
        ChartOfAccount account = chartOfAccountRepository.findByAccountCode(request.getAccountCode())
                .orElseThrow(() -> new RuntimeException("Chart of Account not found for code: " + request.getAccountCode()));

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber("RCT-" + UUID.randomUUID());
        receipt.setPayerName(request.getPayerName());
        receipt.setPayerEmail(request.getPayerEmail());
        receipt.setAmount(request.getAmount());
        receipt.setCurrencyCode(currency);
        receipt.setCurrency(currency);
        receipt.setExchangeRate(exchangeRate);
        receipt.setBaseAmount(baseAmount);
        receipt.setPaymentReference(request.getPaymentReference());
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setSource(request.getSource());
        receipt.setStatus(ReceiptStatus.CONFIRMED);
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setCreatedBy(request.getCreatedBy());
        receipt.setBusinessUnit(request.getBusinessUnit());
        receipt.setDepartment(request.getDepartment());

        Receipt saved = receiptRepository.save(receipt);

        // ✅ Post to GL using the resolved account code
        glPostingUtil.post(
                account.getAccountCode(),
                baseAmount,
                TransactionType.DEBIT,
                GLPostingType.RECEIPT_POSTING,
                saved.getPaymentReference(),
                "Auto-post from receipt: " + saved.getReceiptNumber(),
                saved.getBusinessUnit(),
                saved.getDepartment(),
                saved.getReceiptDate().toLocalDate()
        );

        return saved;
    }
}
