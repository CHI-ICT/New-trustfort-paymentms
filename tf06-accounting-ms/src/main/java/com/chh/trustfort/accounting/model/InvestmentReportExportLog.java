package com.chh.trustfort.accounting.model;

import java.time.LocalDateTime;

import com.chh.trustfort.accounting.enums.ExportFormat;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "investment_report_export_log")
public class InvestmentReportExportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exportedBy;
    private String reportType;
    private LocalDateTime exportedAt;
    private ExportFormat format;
}
