package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.model.RiskAssessmentResult;

public interface RiskAssessmentService {
    RiskAssessmentResult assess(Long investmentId);
}
