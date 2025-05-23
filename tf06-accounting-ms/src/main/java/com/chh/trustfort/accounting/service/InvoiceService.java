package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.InvoiceRequestDto;
import com.chh.trustfort.accounting.dto.InvoiceResponseDto;
import com.chh.trustfort.accounting.model.Invoice;
import com.chh.trustfort.accounting.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository invoiceRepo;

    public InvoiceService(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;
    }

    @Override
    public InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto) {
        Invoice invoice = new Invoice();
        invoice.setUserId(requestDto.getUserId());
        invoice.setAmount(requestDto.getAmount());
        invoice.setReference(requestDto.getReference());
        invoice.setDescription(requestDto.getDescription());
        invoice.setDueDate(requestDto.getDueDate());
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepo.save(invoice);

        return mapToResponseDto(saved);
    }

    private InvoiceResponseDto mapToResponseDto(Invoice invoice) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(invoice.getId());
        dto.setUserId(invoice.getUserId());
        dto.setAmount(invoice.getAmount());
        dto.setReference(invoice.getReference());
        dto.setDescription(invoice.getDescription());
        dto.setDueDate(invoice.getDueDate());
        dto.setCreatedAt(invoice.getCreatedAt());
        return dto;
    }
}

