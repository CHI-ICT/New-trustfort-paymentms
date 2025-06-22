// Service

package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CashflowForecastRequest;
import com.chh.trustfort.accounting.dto.CashflowForecastResponse;

import java.util.List;

public interface CashflowForecastService {
    List<CashflowForecastResponse> generateForecast(CashflowForecastRequest request);
}