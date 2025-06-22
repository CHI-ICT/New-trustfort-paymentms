// --- DebtorReportService.java ---
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtorReportRow;
import java.util.List;

public interface DebtorReportService {
    List<DebtorReportRow> generateDebtorReport();
}
