package com.chh.trustfort.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportApprovalRequest {

    @Schema(description = "Report ID to approve or reject")
    @NotNull(message = "Report ID is required")
    private Long reportId;

    @Schema(description = "User who is taking the approval action")
    @NotBlank(message = "Approver email is required")
    private String approverEmail;

    @Schema(description = "Approval status (APPROVED or REJECTED)")
    @NotBlank(message = "Approval status is required")
    private String approvalStatus;

    @Schema(description = "Optional remarks by the approver")
    private String remarks;

    @NotBlank(message = "Report type is required")
    @Schema(description = "Type of the report being approved")
    private String reportType;

}
