package com.chh.trustfort.accounting.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionReference;
    private String vendorName;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String narration;

    private boolean matched;
}
