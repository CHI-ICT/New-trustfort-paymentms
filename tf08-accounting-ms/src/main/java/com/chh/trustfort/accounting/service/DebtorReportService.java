// --- DebtorReportService.java ---
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DebtorReportRow;
import com.chh.trustfort.accounting.model.AppUser;

import java.util.List;

public interface DebtorReportService {
    String generateDebtorReport(AppUser appUser);
}
