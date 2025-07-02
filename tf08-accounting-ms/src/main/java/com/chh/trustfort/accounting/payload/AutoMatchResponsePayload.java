package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.dto.MatchedPairDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AutoMatchResponsePayload {
    private String responseCode;
    private String responseMessage;
    private String payerEmail;
    private int receiptCount;
    private int matchedCount;
    private List<MatchedPairDTO> matchedPairs;
    private LocalDateTime timestamp;
}
