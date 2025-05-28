package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.RiskAssessmentLevel;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "risk_assessment_result")
public class RiskAssessmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;

    @Enumerated(EnumType.STRING)
    private RiskAssessmentLevel riskLevel;
    private String reason;
    private LocalDateTime evaluatedAt;
}
