// ==== SERVICE IMPL: ChartOfAccountServiceImpl.java ====
package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.model.*;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;
import com.chh.trustfort.accounting.repository.*;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartOfAccountServiceImpl implements ChartOfAccountService {

    private final ChartOfAccountAccountRepository accountRepository;
    private final AccountCategoryRepository categoryRepo;
    private final EntityCodeRepository entityRepo;
    private final DepartmentRepository departmentRepository;

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

//        String deptCode = req.getDepartmentCode() != null ? req.getDepartmentCode() : "001";

        String deptCode;
        if (req.getDepartmentCode() != null) {
            deptCode = req.getDepartmentCode();
        } else {
            // Fallback or throw if departmentCode is required
            throw new IllegalArgumentException("Department code is required");
        }

// Optionally validate that it exists
        departmentRepository.findByDepartmentCode(deptCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department code"));



//        String fullCode = entityCode + "-" + generatedCode + "-" + deptCode;
//        String currencyPrefixed = req.getCurrency() + entityCode + generatedCode + deptCode;

        String fullCode = entityCode + generatedCode + deptCode;  // No hyphens
        String currencyPrefixed = req.getCurrency() + fullCode;   // e.g. NGN1001000001


        ChartOfAccount account = ChartOfAccount.builder()
                .accountName(req.getName())
                .accountCode(generatedCode)
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
                .expenseType(req.getExpenseType())
                .build();

        return accountRepository.save(account);
    }

    @Override
    public ChartOfAccount updateAccount(Long id, ChartOfAccount updatedAccount) {
        return accountRepository.findById(id).map(account -> {
            account.setAccountName(updatedAccount.getAccountName());
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
    public Optional<ChartOfAccount> findByAccountCode(String accountCode) {
        return accountRepository.findAll()
                .stream()
                .filter(a -> a.getAccountCode().equals(accountCode))
                .findFirst();
    }

    private String generateNextCode(ChartOfAccount parent, AccountCategory category) {
        if (parent == null) {
            int base = category.getMinCode();
            int max = category.getMaxCode();

            for (int code = base; code <= max; code += 100) {
                if (!accountRepository.existsByAccountCode(String.valueOf(code))) {
                    return String.valueOf(code);
                }
            }

            List<String> takenCodes = accountRepository.findAll().stream()
                    .filter(a -> a.getParentAccount() == null)
                    .map(ChartOfAccount::getAccountCode)
                    .collect(Collectors.toList());

            for (int code = 1000; code <= 9999; code += 100) {
                if (!takenCodes.contains(String.valueOf(code))) {
                    return String.valueOf(code);
                }
            }

            throw new RuntimeException("No available root-level codes");
        } else {
            int base = Integer.parseInt(parent.getAccountCode()) * 10 + 10;
            while (accountRepository.existsByAccountCode(String.valueOf(base))) {
                base += 10;
            }
            return String.valueOf(base);
        }
    }
}
