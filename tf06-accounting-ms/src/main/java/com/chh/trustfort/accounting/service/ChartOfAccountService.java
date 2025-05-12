package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateChartOfAccountRequest;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    public void createAccount(CreateChartOfAccountRequest request) {
        ChartOfAccount account = new ChartOfAccount();
        account.setAccountCode(request.getAccountCode());
        account.setAccountName(request.getAccountName());
        account.setClassification(request.getClassification());
        account.setAccountType(request.getAccountType());
        account.setDepartment(request.getDepartment());
        account.setBusinessUnit(request.getBusinessUnit());

        chartOfAccountRepository.save(account);
    }
}
