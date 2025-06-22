package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.EOPRequestDTO;
import com.chh.trustfort.accounting.dto.EOPResponseDTO;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.model.EvidenceOfPayment;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.EOPRepository;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

// EOPServiceImpl.java
@Service
@RequiredArgsConstructor
@Slf4j
public class EOPServiceImpl implements EOPService {

    private final PayableInvoiceRepository invoiceRepository;
    private final EOPRepository eopRepository;

    @Override
    public EOPResponseDTO generateEOP(Long invoiceId, EOPRequestDTO request) {
        PayableInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!InvoiceStatus.APPROVED.equals(invoice.getStatus()) ) {
            throw new IllegalStateException("Invoice must be approved and paid before generating EOP");
        }

        eopRepository.findByInvoice(invoice).ifPresent(existing -> {
            throw new IllegalStateException("EOP already exists for this invoice.");
        });

        EvidenceOfPayment eop = EvidenceOfPayment.builder()
                .invoice(invoice)
                .paymentAmount(invoice.getAmount())
                .paymentDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod())
                .referenceNumber(request.getReferenceNumber())
                .generatedAt(LocalDateTime.now())
                .generatedBy(request.getGeneratedBy())
                .build();

        eopRepository.save(eop);

        return EOPResponseDTO.builder()
                .eopId(eop.getId())
                .vendorName(invoice.getVendorName())
                .amount(eop.getPaymentAmount())
                .paymentDate(eop.getPaymentDate())
                .paymentMethod(eop.getPaymentMethod())
                .referenceNumber(eop.getReferenceNumber())
                .downloadUrl(null)
                .build();
    }

    @Override
    public Optional<EOPResponseDTO> getEOPByInvoiceId(Long invoiceId) {
        PayableInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        return eopRepository.findByInvoice(invoice).map(eop ->
                EOPResponseDTO.builder()
                        .eopId(eop.getId())
                        .vendorName(invoice.getVendorName())
                        .amount(eop.getPaymentAmount())
                        .paymentDate(eop.getPaymentDate())
                        .paymentMethod(eop.getPaymentMethod())
                        .referenceNumber(eop.getReferenceNumber())
                        .downloadUrl(eop.getDownloadUrl())
                        .build()
        );
    }
}
