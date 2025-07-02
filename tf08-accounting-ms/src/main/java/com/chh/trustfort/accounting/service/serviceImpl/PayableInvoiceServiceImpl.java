package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.Utility.InvoiceValidationUtil;
import com.chh.trustfort.accounting.dto.PayableInvoiceRequestDTO;
import com.chh.trustfort.accounting.enums.ExpenseType;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PayableInvoiceService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String submitInvoice(PayableInvoiceRequestDTO request, AppUser user) {
        log.info("📝 Submitting payable invoice for vendor: {}", request.getVendorName());

        boolean alreadyExists = payableInvoiceRepository.existsByVendorEmailAndDescriptionAndAmount(
                request.getVendorEmail(),
                request.getDescription(),
                request.getAmount()
        );

        if (alreadyExists) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Duplicate invoice already exists.", "409", "FAIL"), user);
        }

        String invoiceHash = generateInvoiceHash(
                request.getVendorName(),
                request.getVendorEmail(),
                request.getAmount().toString(),
                request.getCurrency(),
                request.getDueDate().toString()
        );

        if (payableInvoiceRepository.existsByInvoiceHash(invoiceHash)) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Duplicate invoice submission detected.", "409", "FAIL"), user);
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
                .submittedBy(user.getEmail())
                .dueDate(request.getDueDate())
                .invoiceDate(LocalDate.now())
                .currency(request.getCurrency())
                .expenseType(request.getExpenseType() != null ? request.getExpenseType() : ExpenseType.GENERAL)
                .paid(Boolean.TRUE.equals(request.getPaid()))
                .invoiceHash(invoiceHash)
                .build();

        if (!invoiceValidationUtil.isInvoiceMatchingPOOrContract(invoice)) {
            return aesService.encrypt(SecureResponseUtil.error(
                    "Invoice does not match any existing PO or Contract.", "422", "FAIL"), user);
        }

        PayableInvoice saved = payableInvoiceRepository.save(invoice);
        log.info("✅ Invoice submitted successfully. Ref: {}", saved.getReference());

        return aesService.encrypt(SecureResponseUtil.success("Invoice submitted successfully.", saved), user);
    }

    private String generateInvoiceHash(String vendorName, String vendorEmail, String amount, String currency, String dueDate) {
        String raw = vendorName.trim().toLowerCase() + "|" +
                vendorEmail.trim().toLowerCase() + "|" +
                amount + "|" +
                currency.trim().toUpperCase() + "|" +
                dueDate;
        return DigestUtils.sha256Hex(raw);
    }
}