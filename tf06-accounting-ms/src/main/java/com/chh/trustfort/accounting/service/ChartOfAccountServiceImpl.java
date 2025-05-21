// ==== SERVICE IMPL: ChartOfAccountServiceImpl.java ====
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ChartOfAccountRequest;
import com.chh.trustfort.accounting.dto.ChartOfAccountResponse;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.model.*;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;
import com.chh.trustfort.accounting.repository.*;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartOfAccountServiceImpl implements ChartOfAccountService {


    @Autowired
    private ChartOfAccountAccountRepository accountRepository;
    @Autowired
    private AccountCategoryRepository categoryRepo;
    @Autowired
    private EntityCodeRepository entityRepo;

    @Override
    public ChartOfAccount createAccount(CreateCOARequestPayload req) {
        ChartOfAccount parent = null;

        if (req.getParentAccountId() != null) {
            parent = accountRepository.findById(req.getParentAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }

        AccountCategory category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        String generatedCode = generateNextCode(parent, category);

        String entityCode = entityRepo.findBySubsidiary(req.getSubsidiary())
                .orElseThrow(() -> new IllegalArgumentException("Entity code not found"))
                .getCode();

        String deptCode = req.getDepartmentCode() != null ? req.getDepartmentCode() : "001";

        String fullCode = entityCode + "-" + generatedCode + "-" + deptCode;
        String currencyPrefixed = req.getCurrency() + entityCode + generatedCode + deptCode;

        ChartOfAccount account = ChartOfAccount.builder()
                .name(req.getName())
                .code(generatedCode)
                .category(category)
                .parentAccount(parent)
                .subsidiary(req.getSubsidiary())
                .currency(req.getCurrency())
                .normalBalance(req.getNormalBalance())
                .status(req.getStatus())
                .fullAccountCode(fullCode)
                .currencyPrefixedCode(currencyPrefixed)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .classification(req.getClassification())
                .build();


        return accountRepository.save(account);
    }

    @Override
    public ChartOfAccount updateAccount(Long id, ChartOfAccount updatedAccount) {
        return accountRepository.findById(id).map(account -> {
            account.setName(updatedAccount.getName());
            account.setStatus(updatedAccount.getStatus());
            return accountRepository.save(account);
        }).orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }

    @Override
    public Optional<ChartOfAccount> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<ChartOfAccount> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<ChartOfAccount> findByCode(String code) {
        return accountRepository.findAll()
                .stream()
                .filter(a -> a.getCode().equals(code))
                .findFirst();
    }

    private String generateNextCode(ChartOfAccount parent, AccountCategory category) {
        if (parent == null) {
            int base = category.getMinCode();
            int max = category.getMaxCode();

            for (int code = base; code <= max; code += 100) {
                if (!accountRepository.existsByCode(String.valueOf(code))) {
                    return String.valueOf(code);
                }
            }

            // fallback to any open slot
            List<String> takenCodes = accountRepository.findAll().stream()
                    .filter(a -> a.getParentAccount() == null)
                    .map(ChartOfAccount::getCode).collect(Collectors.toList());

            for (int code = 1000; code <= 9999; code += 100) {
                if (!takenCodes.contains(String.valueOf(code))) {
                    return String.valueOf(code);
                }
            }

            throw new RuntimeException("No available root-level codes");
        } else {
            int base = Integer.parseInt(parent.getCode()) * 10 + 10;
            while (accountRepository.existsByCode(String.valueOf(base))) {
                base += 10;
            }
            return String.valueOf(base);
        }
    }
}
