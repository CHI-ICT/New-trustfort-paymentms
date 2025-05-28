package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.InvoiceRequestDto;
import com.chh.trustfort.accounting.dto.InvoiceResponseDto;
import com.chh.trustfort.accounting.model.Invoice;
import com.chh.trustfort.accounting.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService implements IInvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Override
    public InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto) {
        Invoice invoice = buildInvoiceFromRequest(requestDto);
        Invoice saved = invoiceRepo.save(invoice);
        return mapToResponseDto(saved);
    }

    @Override
    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceRepo.findAll().stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public InvoiceResponseDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToResponseDto(invoice);
    }

    @Override
    public InvoiceResponseDto updateInvoice(Long id, InvoiceRequestDto requestDto) {
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setUserId(requestDto.getUserId());
        invoice.setAmount(requestDto.getAmount());
        invoice.setReference(requestDto.getReference());
        invoice.setDescription(requestDto.getDescription());
        invoice.setDueDate(requestDto.getDueDate());
        return mapToResponseDto(invoiceRepo.save(invoice));
    }

    private Invoice buildInvoiceFromRequest(InvoiceRequestDto requestDto) {
        Invoice invoice = new Invoice();
        invoice.setUserId(requestDto.getUserId());
        invoice.setAmount(requestDto.getAmount());
        invoice.setReference(requestDto.getReference());
        invoice.setDescription(requestDto.getDescription());
        invoice.setDueDate(requestDto.getDueDate());
        invoice.setCreatedAt(LocalDateTime.now());
        return invoice;
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

