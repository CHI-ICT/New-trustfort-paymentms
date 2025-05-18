// ==== SERVICE IMPL: ChartOfAccountServiceImpl.java ====
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ChartOfAccountRequest;
import com.chh.trustfort.accounting.dto.ChartOfAccountResponse;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.Currency;
import com.chh.trustfort.accounting.model.Department;
import com.chh.trustfort.accounting.model.Subsidiary;
import com.chh.trustfort.accounting.repository.*;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartOfAccountServiceImpl implements ChartOfAccountService {

    private final ChartOfAccountRepository repository;
    private final DepartmentRepository departmentRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    public ChartOfAccountResponse createChartOfAccount(ChartOfAccountRequest request) {
        validateCodes(request);

        String code = generateCode(request);
        if (repository.existsByCode(code)) {
            throw new IllegalArgumentException("Chart of Account already exists: " + code);
        }

        ChartOfAccount coa = ChartOfAccount.builder()
                .name(request.getName())
                .code(code)
                .currencyCode(request.getCurrencyCode())
                .subsidiaryCode(request.getSubsidiaryCode())
                .departmentCode(request.getDepartmentCode())
                .classification(request.getClassification())
                .active(true)
                .build();

        return mapToResponse(repository.save(coa));
    }

    @Override
    public List<ChartOfAccountResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void validateCodes(ChartOfAccountRequest request) {
        if (!departmentRepository.existsByCode(request.getDepartmentCode())) {
            throw new IllegalArgumentException("Invalid department code");
        }
        if (!subsidiaryRepository.existsByCode(request.getSubsidiaryCode())) {
            throw new IllegalArgumentException("Invalid subsidiary code");
        }
        if (!currencyRepository.existsByCode(request.getCurrencyCode())) {
            throw new IllegalArgumentException("Invalid currency code");
        }
    }

    private ChartOfAccountResponse mapToResponse(ChartOfAccount coa) {
        return ChartOfAccountResponse.builder()
                .code(coa.getCode())
                .name(coa.getName())
                .currencyCode(coa.getCurrencyCode())
                .subsidiaryCode(coa.getSubsidiaryCode())
                .departmentCode(coa.getDepartmentCode())
                .classification(coa.getClassification())
                .build();
    }

    private String generateCode(ChartOfAccountRequest request) {
        Department dept = departmentRepository.findByCode(request.getDepartmentCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department code"));

        Subsidiary sub = subsidiaryRepository.findByCode(request.getSubsidiaryCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subsidiary code"));

        Currency currency = currencyRepository.findByCode(request.getCurrencyCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency code"));

        AccountClassification classification = request.getClassification();

        return currency.getCode()
                + sub.getCode()
                + classification.getCode()
                + dept.getCode();
    }
}
