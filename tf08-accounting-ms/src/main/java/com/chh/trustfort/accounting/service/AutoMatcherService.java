package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.MatchedPairDTO;
import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.payload.AutoMatchRequestPayload;
import com.chh.trustfort.accounting.payload.AutoMatchResponsePayload;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoMatcherService {

    private final ReceiptRepository receiptRepository;
    private final ReceivableRepository receivableRepository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    public String matchOpenReceivables(AutoMatchRequestPayload request, AppUser appUser) {
        AutoMatchResponsePayload response = new AutoMatchResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage("Matching failed");

        if (request == null || request.getPayerEmail() == null) {
            response.setResponseMessage("Payer email is required.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        String payerEmail = request.getPayerEmail();
        log.info("🔎 Matching receivables for: {}", payerEmail);

        List<Receipt> receipts = receiptRepository.findByPayerEmailAndStatus(payerEmail, ReceiptStatus.CONFIRMED);
        List<MatchingStatus> eligibleStatuses = List.of(
                MatchingStatus.UNMATCHED,
                MatchingStatus.FULLY_MATCHED,
                MatchingStatus.PARTIALLY_MATCHED,
                MatchingStatus.PENDING
        );
        List<Receivable> openReceivables = receivableRepository.findByMatchingStatusInAndPayerEmail(eligibleStatuses, payerEmail);

        List<MatchedPairDTO> matchedPairs = new ArrayList<>();

        for (Receipt receipt : receipts) {
            BigDecimal receiptAmount = receipt.getBaseAmount();

            for (Receivable receivable : openReceivables) {
                BigDecimal remaining = receivable.getAmount().subtract(receivable.getMatchedAmount());

                if (remaining.compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal toMatch = receiptAmount.min(remaining);
                receivable.setMatchedAmount(receivable.getMatchedAmount().add(toMatch));

                if (receivable.getMatchedAmount().compareTo(receivable.getAmount()) == 0) {
                    receivable.setMatchingStatus(MatchingStatus.FULLY_MATCHED);
                } else {
                    receivable.setMatchingStatus(MatchingStatus.PARTIALLY_MATCHED);
                }

                matchedPairs.add(new MatchedPairDTO(
                        receipt.getId(),
                        receivable.getReference(),
                        toMatch,
                        "Receivable ID: " + receivable.getId()
                ));

                receiptAmount = receiptAmount.subtract(toMatch);
                if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) break;
            }
        }

        receivableRepository.saveAll(openReceivables);

        response.setResponseCode("00");
        response.setResponseMessage(messageSource.getMessage("matcher.success", null, Locale.ENGLISH));
        response.setPayerEmail(payerEmail);
        response.setReceiptCount(receipts.size());
        response.setMatchedCount(matchedPairs.size());
        response.setMatchedPairs(matchedPairs);
        response.setTimestamp(LocalDateTime.now());

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
