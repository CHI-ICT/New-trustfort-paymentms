// ==== SERVICE: ChartOfAccountService.java ====
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ChartOfAccountRequest;
import com.chh.trustfort.accounting.dto.ChartOfAccountResponse;

import java.util.List;

public interface ChartOfAccountService {
    ChartOfAccountResponse createChartOfAccount(ChartOfAccountRequest request);
    List<ChartOfAccountResponse> getAll();
}