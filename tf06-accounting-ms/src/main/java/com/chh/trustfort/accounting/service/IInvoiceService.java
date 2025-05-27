package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.InvoiceRequestDto;
import com.chh.trustfort.accounting.dto.InvoiceResponseDto;

import java.util.List;

public interface IInvoiceService {
    InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto);
    List<InvoiceResponseDto> getAllInvoices();
    InvoiceResponseDto getInvoiceById(Long id);
    InvoiceResponseDto updateInvoice(Long id, InvoiceRequestDto requestDto);
}

