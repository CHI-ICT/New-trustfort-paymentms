package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.EOPRequestDTO;
import com.chh.trustfort.accounting.dto.EOPResponseDTO;

import java.util.Optional;

// EOPService.java
public interface EOPService {
    EOPResponseDTO generateEOP(Long invoiceId, EOPRequestDTO request);
    Optional<EOPResponseDTO> getEOPByInvoiceId(Long invoiceId);
}