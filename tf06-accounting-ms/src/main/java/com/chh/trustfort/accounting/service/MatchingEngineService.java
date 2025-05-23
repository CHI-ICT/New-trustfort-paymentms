package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.Invoice;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.repository.InvoiceRepository;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchingEngineService implements IMatchingEngineService {

    private final InvoiceRepository invoiceRepo;
    private final ReceiptRepository receiptRepo;

    public MatchingEngineService(InvoiceRepository invoiceRepo,
                                 ReceiptRepository receiptRepo) {
        this.invoiceRepo = invoiceRepo;
        this.receiptRepo = receiptRepo;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void matchOneToOneByReferenceAndAmount() {
        List<Receipt> unmatchedReceipts = receiptRepo.findByMatchKeyIsNull();

        for (Receipt receipt : unmatchedReceipts) {
            Optional<Invoice> invoiceOpt = invoiceRepo
                    .findFirstByReferenceAndAmountAndMatchKeyIsNull(
                            receipt.getPaymentReference(),
                            receipt.getAmount()
                    );

            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                String matchKey = generateMatchKey();

                receipt.setMatchKey(matchKey);
                invoice.setMatchKey(matchKey);

                receiptRepo.save(receipt);
                invoiceRepo.save(invoice);
            }
        }
    }

    private String generateMatchKey() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

