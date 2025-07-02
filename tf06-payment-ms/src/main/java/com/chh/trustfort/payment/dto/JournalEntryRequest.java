package com.chh.trustfort.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class JournalEntryRequest {
//    private String accountCode; // e.g. "REV001" or "EXP001"
//    private BigDecimal amount;
//    private String description;
//    private String transactionType; // "DEBIT" or "CREDIT"
//    private String department;
//    private String businessUnit;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime transactionDate;
//    private String walletId;
//    private String reference;
//    private String transactionType; // CREDIT
//    private BigDecimal amount;
//    private String narration;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime transactionDate;
        private String accountCode;
        private String walletId;// e.g., "1001" for wallet
        private String transactionType;  // "CREDIT"
        private BigDecimal amount;
        private String description;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime transactionDate;
        private String reference;
        private String department;
        private String businessUnit;
    }


