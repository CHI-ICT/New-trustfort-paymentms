package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EOP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentPath;
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    @ManyToOne
    private PayableInvoice invoice;
}
