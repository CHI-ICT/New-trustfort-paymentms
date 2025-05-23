package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
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

    public void reconcileReceivables() {
        log.info("Running receipt-receivable reconciliation logic");

        List<Receivable> receivables = receivableRepository.findAll();

        for (Receivable receivable : receivables) {
            List<Receipt> linkedReceipts = receiptRepository.findByPaymentReference(receivable.getReference());

            BigDecimal totalPaid = linkedReceipts.stream()
                    .map(Receipt::getBaseAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPaid.compareTo(receivable.getAmount()) >= 0) {
                receivable.setStatus(ReceivableStatus.PAID);
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                receivable.setStatus(ReceivableStatus.PARTIALLY_PAID);
            } else {
                receivable.setStatus(ReceivableStatus.PENDING);
            }

            receivableRepository.save(receivable);
        }
    }
}
