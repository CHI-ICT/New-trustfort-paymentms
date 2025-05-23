package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.dto.InvoiceRequestDto;
import com.chh.trustfort.accounting.dto.InvoiceResponseDto;
import com.chh.trustfort.accounting.service.IInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final IInvoiceService invoiceService;

    public InvoiceController(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponseDto> createInvoice(@RequestBody InvoiceRequestDto dto) {
        return ResponseEntity.ok(invoiceService.createInvoice(dto));
    }
}

