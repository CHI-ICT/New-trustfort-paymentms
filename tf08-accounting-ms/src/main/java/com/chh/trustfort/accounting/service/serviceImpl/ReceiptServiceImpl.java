package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Utility.GLPostingUtil;
import com.chh.trustfort.accounting.enums.GLPostingType;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ExchangeRateService;
import com.chh.trustfort.accounting.service.ReceiptService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ExchangeRateService exchangeRateService;
    private final GLPostingUtil glPostingUtil;
    private final ChartOfAccountAccountRepository chartOfAccountRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String generateReceipt(ReceiptGenerationRequest request, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        try {
            String currency = request.getCurrency() != null ? request.getCurrency() : "NGN";
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currency, "NGN");
            BigDecimal baseAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

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

            response.setResponseCode("00");
            response.setResponseMessage("Receipt generated successfully");
            response.setData(saved);
        } catch (Exception e) {
            log.error("❌ Error generating receipt: {}", e.getMessage(), e);
            response.setResponseCode("06");
            response.setResponseMessage("Failed to generate receipt: " + e.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
