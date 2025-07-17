package com.chh.trustfort.payment.Responses;

import com.chh.trustfort.payment.dto.LedgerEntryDTO;
import lombok.Data;

import java.util.List;

@Data
public class TransactionHistoryResponse {
    private String responseCode;
    private String message;
    private List<LedgerEntryDTO> transactions;
}
