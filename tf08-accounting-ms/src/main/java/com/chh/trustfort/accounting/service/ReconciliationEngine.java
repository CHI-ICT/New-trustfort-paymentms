package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ReconciliationSummaryDTO;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationEngine {

    private final ReceivableRepository receivableRepository;
    private final ReceiptRepository receiptRepository;
    private final AesService aesService;
    private final Gson gson;

    public String reconcileReceivables(AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();

        try {
            log.info("Running receipt-receivable reconciliation logic");

            List<Receivable> receivables = receivableRepository.findAll();

            int updated = 0;
            int paidCount = 0;
            int partialCount = 0;
            int pendingCount = 0;

            for (Receivable receivable : receivables) {
                List<Receipt> linkedReceipts = receiptRepository.findByPaymentReference(receivable.getReference());

                BigDecimal totalPaid = linkedReceipts.stream()
                        .map(Receipt::getBaseAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                ReceivableStatus originalStatus = receivable.getStatus();

                if (totalPaid.compareTo(receivable.getAmount()) >= 0) {
                    receivable.setStatus(ReceivableStatus.PAID);
                    paidCount++;
                } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                    receivable.setStatus(ReceivableStatus.PARTIALLY_PAID);
                    partialCount++;
                } else {
                    receivable.setStatus(ReceivableStatus.PENDING);
                    pendingCount++;
                }

                if (!receivable.getStatus().equals(originalStatus)) {
                    updated++;
                    receivableRepository.save(receivable);
                }
            }

            ReconciliationSummaryDTO summary = new ReconciliationSummaryDTO(
                    receivables.size(),
                    updated,
                    paidCount,
                    partialCount,
                    pendingCount,
                    "Reconciliation process completed successfully."
            );

            response.setResponseCode("00");
            response.setResponseMessage("Reconciliation completed");
            response.setData(summary);
        } catch (Exception ex) {
            log.error("❌ Reconciliation error: {}", ex.getMessage(), ex);
            response.setResponseCode("06");
            response.setResponseMessage("Error during reconciliation: " + ex.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
