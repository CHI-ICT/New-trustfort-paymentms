// PaymentMovement.java
package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference; // Movement reference like PMT-MOV-XXXX

    private String sourceReceivableRef;
    private String destinationReceivableRef;

    private BigDecimal amount;

    private String reason;

    private String movedBy;

    private LocalDateTime movedAt;
}
