package com.chh.trustfort.accounting.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

import com.chh.trustfort.accounting.enums.InvestmentVoucherStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "investment_voucher")
public class InvestmentVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;
    private String createdBy;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private InvestmentVoucherStatus status;
}
