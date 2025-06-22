// PurchaseOrder.java
package com.chh.trustfort.accounting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String poNumber;

    private String vendorName;

    private String vendorEmail;

    private BigDecimal amount;

    private String currency;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate validUntil;
}