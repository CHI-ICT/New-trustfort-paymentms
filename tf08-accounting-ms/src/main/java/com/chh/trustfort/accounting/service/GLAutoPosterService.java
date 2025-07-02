package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.DoubleEntryRequest;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ✅ Fixing issues in GLAutoPosterService based on missing fields/methods

@Service
@RequiredArgsConstructor
@Slf4j
public class GLAutoPosterService {

    private final PayableInvoiceRepository payableInvoiceRepository;
    private final JournalEntryService journalEntryService;
    private final ChartOfAccountAccountRepository chartOfAccountRepository;
    private final AesService aesService;
    private final Gson gson;

    public String postInvoiceToGL(Long invoiceId, AppUser user) {
        log.info("🔁 Posting Payable Invoice [{}] to GL", invoiceId);

        PayableInvoice invoice = payableInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!InvoiceStatus.APPROVED.equals(invoice.getStatus())) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Only approved invoices can be posted to GL", "400", "FAIL"), user);
        }

        if (invoice.getGlReference() != null) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Invoice has already been posted to GL", "409", "FAIL"), user);
        }

        List<ChartOfAccount> accounts = chartOfAccountRepository.findAllByExpenseType(invoice.getExpenseType());
        if (accounts == null || accounts.isEmpty()) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "No Chart of Account found for expense type: " + invoice.getExpenseType(), "404", "FAIL"), user);
        }

        ChartOfAccount expenseAccount = accounts.get(0);

        List<ChartOfAccount> cashAccounts = chartOfAccountRepository.findCashOrBankAccounts("cash");
        if (cashAccounts.isEmpty()) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "No active Cash/Bank account found.", "404", "FAIL"), user);
        }

        ChartOfAccount cashAccount = cashAccounts.get(0);

        String reference = "GL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String narration = "Payable invoice for " + invoice.getVendorName() + ", Ref: " + invoice.getInvoiceNumber();

        DoubleEntryRequest request = DoubleEntryRequest.builder()
                .debitAccountCode(expenseAccount.getAccountCode())
                .creditAccountCode(cashAccount.getAccountCode())
                .reference(reference)
                .description(narration)
                .amount(invoice.getAmount())
                .department("Accounts Dept")
                .businessUnit("Finance BU")
                .transactionDate(LocalDateTime.now())
                .build();

        journalEntryService.recordDoubleEntry(request, user);


        invoice.setGlReference(reference);
        invoice.setPostedToGL(true);
        invoice.setPostedAt(LocalDateTime.now());
        payableInvoiceRepository.save(invoice);

        Map<String, Object> data = Map.of(
                "glReference", reference,
                "amount", invoice.getAmount(),
                "expenseAccount", expenseAccount.getAccountName(),
                "cashAccount", cashAccount.getAccountName()
        );

        return aesService.encrypt(SecureResponseUtil.success("Invoice posted to GL successfully", data), user);
    }
}