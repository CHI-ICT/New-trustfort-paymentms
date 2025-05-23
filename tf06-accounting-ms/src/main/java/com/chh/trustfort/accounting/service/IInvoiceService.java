package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.InvoiceRequestDto;
import com.chh.trustfort.accounting.dto.InvoiceResponseDto;

public interface IInvoiceService {
    InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto);
}

