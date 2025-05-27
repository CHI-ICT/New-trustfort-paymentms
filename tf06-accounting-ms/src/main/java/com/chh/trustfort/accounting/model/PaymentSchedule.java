package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference  // Prevent recursive serialization
    private PayableInvoice invoice;


    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private boolean paid;
}
