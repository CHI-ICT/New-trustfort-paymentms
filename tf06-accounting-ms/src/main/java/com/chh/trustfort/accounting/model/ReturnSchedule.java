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
@Table(name = "return_schedule")
public class ReturnSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Investment investment;

    private LocalDate dueDate;
    private BigDecimal expectedReturn;
    private boolean paid;
}
