// ==== SERVICE: ChartOfAccountService.java ====
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ChartOfAccountRequest;
import com.chh.trustfort.accounting.dto.ChartOfAccountResponse;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;

import java.util.List;
import java.util.Optional;

public interface ChartOfAccountService {
    ChartOfAccount createAccount(CreateCOARequestPayload createAccountRequestPayload);

    ChartOfAccount updateAccount(Long id, ChartOfAccount updatedAccount);

    Optional<ChartOfAccount> findById(Long id);

    List<ChartOfAccount> findAll();

    Optional<ChartOfAccount> findByAccountCode(String accountCode);
}