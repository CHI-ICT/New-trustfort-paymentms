package com.chh.trustfort.accounting.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportApprovalResponse {

    @Schema(description = "Unique ID for the approval record")
    private Long approvalId;

    @Schema(description = "Associated report ID")
    private Long reportId;

    @Schema(description = "Email of the user who approved or rejected the report")
    private String approverEmail;

    @Schema(description = "Final status (APPROVED or REJECTED)")
    private String approvalStatus;

    @Schema(description = "Any comments made by the approver")
    private String remarks;

    @Schema(description = "Timestamp of the approval action")
    private String approvedAt;

    @Schema(description = "Current approval step in the workflow (e.g., LEVEL_1, LEVEL_2)")
    private String approvalLevel;

    @Schema(description = "Next approver email if required")
    private String nextApprover;

    @Schema(description = "Approval status of the entire report (PENDING, COMPLETED)")
    private String reportStatus;

    @Schema(description = "Type of the report")
    private String reportType;

}
