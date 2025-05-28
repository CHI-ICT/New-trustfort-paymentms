package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roll_over_request")
public class RolloverRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private boolean approved;
    private String approvedBy;
    private LocalDateTime approvedAt;
}
