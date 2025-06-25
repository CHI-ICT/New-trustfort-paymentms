package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.model.AppUser;

import java.time.LocalDate;
import java.util.List;

public interface BankReconciliationService {
    String reconcileBankWithLedger(LocalDate startDate, LocalDate endDate, AppUser user);
}

