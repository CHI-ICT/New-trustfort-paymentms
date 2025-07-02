package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import com.chh.trustfort.accounting.model.AppUser;

import java.util.List;

public interface AgingTrackerService {
//    List<DebtAgingSummaryRow> generateAgingSummary();
    String generateDebtAgingSummary(AppUser appUser);
}
