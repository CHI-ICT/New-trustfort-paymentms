package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import java.util.List;

public interface AgingTrackerService {
    List<DebtAgingSummaryRow> generateAgingSummary();
}
