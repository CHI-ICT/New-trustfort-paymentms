package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Utility.InvoiceValidationUtil;
import com.chh.trustfort.accounting.dto.PayableInvoiceRequestDTO;
import com.chh.trustfort.accounting.enums.ExpenseType;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayableStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayableInvoiceServiceImpl implements PayableInvoiceService {


    private final PayableInvoiceRepository payableInvoiceRepository;
    private final InvoiceValidationUtil invoiceValidationUtil;

    @Override
    public PayableInvoice submitInvoice(PayableInvoiceRequestDTO request) {
        log.info("Submitting new payable invoice for vendor: {}", request.getVendorName());

        // Basic duplication validation
        boolean alreadyExists = payableInvoiceRepository.existsByVendorEmailAndDescriptionAndAmount(
                request.getVendorEmail(),
                request.getDescription(),
                request.getAmount()
        );

        if (alreadyExists) {
            throw new RuntimeException("Duplicate invoice already exists for this vendor and description.");
        }
        // Generate a unique hash to prevent duplicates
        String invoiceHash = generateInvoiceHash(
                request.getVendorName(),
                request.getVendorEmail(),
                request.getAmount().toString(),
                request.getCurrency(),
                request.getDueDate().toString()
        );
        if (payableInvoiceRepository.existsByInvoiceHash(invoiceHash)) {
            throw new ApiException("Duplicate invoice submission detected.", HttpStatus.CONFLICT);
        }

        PayableInvoice invoice = PayableInvoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .reference("PI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .vendorName(request.getVendorName())
                .vendorEmail(request.getVendorEmail())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(InvoiceStatus.PENDING_APPROVAL)
                .submittedAt(LocalDateTime.now())
                .submittedBy("system")
                .dueDate(request.getDueDate())
                .invoiceDate(LocalDate.now())
                .currency(request.getCurrency())
                .expenseType(ExpenseType.GENERAL)
                .build();

        // Validate against PO or Contract
//        if (!invoiceValidationUtil.isInvoiceMatchingPOOrContract(invoice)) {
//            throw new RuntimeException("Invoice does not match any existing PO or Contract.");
//        }

        // 🔧 Set missing or auto-generated fields
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setInvoiceDate(LocalDate.now());

        // Optional default for expense type
        if (invoice.getExpenseType() == null) {
            invoice.setExpenseType(ExpenseType.GENERAL);
        }

        return payableInvoiceRepository.save(invoice);
    }

    private String generateInvoiceHash(String vendorName, String vendorEmail, String amount, String currency, String dueDate) {
        String raw = vendorName.trim().toLowerCase() + "|" +
                vendorEmail.trim().toLowerCase() + "|" +
                amount + "|" +
                currency.trim().toUpperCase() + "|" +
                dueDate;
        return DigestUtils.sha256Hex(raw); // Apache Commons Codec
    }

}
