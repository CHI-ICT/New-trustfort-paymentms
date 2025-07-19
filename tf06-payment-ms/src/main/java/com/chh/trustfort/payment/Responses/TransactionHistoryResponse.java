package com.chh.trustfort.payment.Responses;

import com.chh.trustfort.payment.dto.LedgerEntryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {
    private String responseCode;
    private String message;
    private List<LedgerEntryDTO> transactions;
}
