package com.chh.trustfort.payment.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApproverPayload {
    private Long ruleId;
    private Long newApproverId;
}

