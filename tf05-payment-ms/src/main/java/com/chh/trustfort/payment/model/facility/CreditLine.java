package com.chh.trustfort.payment.model.facility;

import com.chh.trustfort.payment.enums.CreditStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@Table(name = "credit_line")
public class CreditLine {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal repaidAmount;

    @Enumerated(EnumType.STRING)
    private CreditStatus status;

    @Column(name = "requested_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;

    private LocalDateTime approvedDate;

    // Optional: for audit
    private String reason;
}
