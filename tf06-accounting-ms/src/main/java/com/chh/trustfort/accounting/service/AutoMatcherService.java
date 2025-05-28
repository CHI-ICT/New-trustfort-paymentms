package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.MatchedPairDTO;
import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoMatcherService {

    private final ReceiptRepository receiptRepository;
    private final ReceivableRepository receivableRepository;

    public List<MatchedPairDTO> matchOpenReceivablesFor(String payerEmail) {
        List<Receipt> receipts = receiptRepository.findByPayerEmailAndStatus(payerEmail, ReceiptStatus.CONFIRMED);
        List<MatchingStatus> eligibleStatuses = List.of(MatchingStatus.UNMATCHED,MatchingStatus.FULLY_MATCHED, MatchingStatus.PARTIALLY_MATCHED, MatchingStatus.PENDING);
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

                // Track the pair
                matchedPairs.add(new MatchedPairDTO(
                        receipt.getId(),
                        receivable.getReference(),
                        toMatch,
                        "Receivable ID: " + receivable.getId()
                ));

                // Reduce the receiptAmount and break if used up
                receiptAmount = receiptAmount.subtract(toMatch);
                if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) break;
            }
        }

        receivableRepository.saveAll(openReceivables);
        return matchedPairs;
    }
}
