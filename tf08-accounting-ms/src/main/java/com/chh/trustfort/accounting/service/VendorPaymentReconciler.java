package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.ReconciliationResult;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.BankTransaction;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentDiscrepancy;
import com.chh.trustfort.accounting.repository.BankTransactionRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentDiscrepancyRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorPaymentReconciler {

    private final PayableInvoiceRepository invoiceRepo;
    private final BankTransactionRepository bankRepo;
    private final PaymentDiscrepancyRepository discrepancyRepo;
    private final PayoutClassifierService payoutClassifierService;
    private final AesService aesService;
    private final Gson gson;

    public String reconcileVendorPayments(AppUser appUser) {
        log.info("🔄 Starting vendor payment reconciliation...");

        List<PayableInvoice> invoices = invoiceRepo.findByStatus(InvoiceStatus.APPROVED);
        List<BankTransaction> bankTransactions = bankRepo.findAllByMatchedFalse();

        List<String> reconciled = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (PayableInvoice invoice : invoices) {
            Optional<BankTransaction> match = bankTransactions.stream()
                    .filter(tx -> tx.getTransactionReference().equals(invoice.getReference())
                            && tx.getAmount().compareTo(invoice.getAmount()) == 0)
                    .findFirst();

            if (match.isPresent()) {
                BankTransaction tx = match.get();
                invoice.setStatus(InvoiceStatus.RECONCILED);
                PayoutCategory category = payoutClassifierService.resolveCategory(invoice);
                invoice.setPayoutCategory(category);
                tx.setMatched(true);

                invoiceRepo.save(invoice);
                bankRepo.save(tx);
                reconciled.add(invoice.getInvoiceNumber());

                log.info("✅ Reconciled invoice {} with transaction {}", invoice.getInvoiceNumber(), tx.getTransactionReference());
            } else {
                discrepancyRepo.save(PaymentDiscrepancy.builder()
                        .invoiceNumber(invoice.getInvoiceNumber())
                        .expectedReference(invoice.getReference())
                        .actualReference(null)
                        .expectedAmount(invoice.getAmount())
                        .actualAmount(null)
                        .issue("MISSING_PAYMENT")
                        .createdAt(LocalDateTime.now())
                        .build());

                failed.add(invoice.getInvoiceNumber());
                log.warn("⚠️ No match for invoice {}", invoice.getInvoiceNumber());
            }
        }

        ReconciliationResult result = ReconciliationResult.builder()
                .reconciledInvoices(reconciled)
                .discrepancies(failed)
                .build();

        log.info("🎯 Reconciliation complete. Success: {}, Failed: {}", reconciled.size(), failed.size());
        return aesService.encrypt(SecureResponseUtil.success("Reconciliation completed", result), appUser);
    }
}