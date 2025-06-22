package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.ChartOfAccountAccountRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
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

    public ApiResponse postInvoiceToGL(Long invoiceId) {
        PayableInvoice invoice = payableInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!InvoiceStatus.APPROVED.equals(invoice.getStatus())) {
            return ApiResponse.error("Only approved invoices can be posted to GL");
        }

        // Prevent double posting (requires these fields in entity)
        if (invoice.getGlReference() != null) {
            return ApiResponse.error("Invoice has already been posted to GL.");
        }

        // 🔁 Resolve GL account (Chart of Account) for this expense type
        List<ChartOfAccount> accounts = chartOfAccountRepository.findAllByExpenseType(invoice.getExpenseType());
        if (accounts == null || accounts.isEmpty()) {
            return ApiResponse.error("No Chart of Account found for expense type: " + invoice.getExpenseType());
        }

// Select the first matching account (or apply custom logic to pick the right one)
        ChartOfAccount expenseAccount = accounts.get(0);

        List<ChartOfAccount> cashAccounts = chartOfAccountRepository.findCashOrBankAccounts("cash");

        if (cashAccounts.isEmpty()) {
            return ApiResponse.error("No active Cash/Bank account found.");
        }

        ChartOfAccount cashAccount = cashAccounts.get(0); // or add logic to select default/preferred


        // 💼 Build journal entries
        String reference = "GL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String narration = "Payable invoice for " + invoice.getVendorName() + ", Ref: " + invoice.getInvoiceNumber();

        journalEntryService.recordDoubleEntry(
                expenseAccount.getAccountCode(),
                cashAccount.getAccountCode(),
                reference,
                narration,
                invoice.getAmount(),
                "Accounts Dept",
                "Finance BU",
                LocalDateTime.now()
        );

        // 📝 Update invoice with GL status (requires fields: glReference, postedToGL, postedAt)
        invoice.setGlReference(reference);
        invoice.setPostedToGL(true);
        invoice.setPostedAt(LocalDateTime.now());
        payableInvoiceRepository.save(invoice);

        return ApiResponse.success("Invoice posted to GL successfully.", Map.of(
                "glReference", reference,
                "amount", invoice.getAmount(),
                "expenseAccount", expenseAccount.getAccountName(),
                "cashAccount", cashAccount.getAccountName()
        ));
    }
}
