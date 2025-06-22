// Contract.java
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
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String contractCode;

    private String vendorName;

    private String vendorEmail;

    private BigDecimal ceilingAmount;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
