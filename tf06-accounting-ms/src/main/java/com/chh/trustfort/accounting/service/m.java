//package com.chh.trustfort.accounting.service;
//
//import com.chh.trustfort.accounting.model.ChartOfAccount;
//import com.chh.trustfort.accounting.model.JournalEntry;
//import com.chh.trustfort.accounting.payload.ReconciliationIssue;
//import com.chh.trustfort.accounting.payload.TrialBalanceDTO;
//import com.chh.trustfort.accounting.payload.TrialBalanceRequest;
//import com.chh.trustfort.accounting.payload.TrialBalanceResponse;
//import com.chh.trustfort.accounting.repository.ChartOfAccountRepository;
//import com.chh.trustfort.accounting.repository.JournalEntryRepository;
//import com.chh.trustfort.accounting.repository.TrialBalanceAdjustmentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class TrialBalanceServiceImpl implements TrialBalanceService {
//
//    @Autowired
//    private JournalEntryRepository ledgerRepo;
//    @Autowired private ChartOfAccountRepository accountRepo;
//    @Autowired private TrialBalanceAdjustmentRepository adjustmentRepo;
//
//    @Override
//    public TrialBalanceResponse generateTrialBalance(TrialBalanceRequest request) {
//        List<JournalEntry> entries = ledgerRepo.findByDateRange(request.getStartDate(), request.getEndDate());
//        Map<String, TrialBalanceDTO> map = new HashMap<>();
//
//        for (LedgerEntry entry : entries) {
//            TrialBalanceDTO dto = map.computeIfAbsent(entry.getAccountCode(), code -> {
//                ChartOfAccount account = accountRepo.findByCode(code);
//                return new TrialBalanceDTO(code, account.getName(), BigDecimal.ZERO, BigDecimal.ZERO);
//            });
//            if (entry.isDebit()) dto.setDebit(dto.getDebit().add(entry.getAmount()));
//            else dto.setCredit(dto.getCredit().add(entry.getAmount()));
//        }
//
//        if (request.isIncludeAdjustments()) {
//            List<TrialBalanceAdjustment> adjustments = adjustmentRepo.findAll();
//            for (TrialBalanceAdjustment adj : adjustments) {
//                TrialBalanceDTO dto = map.get(adj.getAccountCode());
//                if (dto != null) dto.setDebit(dto.getDebit().add(adj.getAdjustmentAmount()));
//            }
//        }
//
//        BigDecimal totalDebit = map.values().stream().map(TrialBalanceDTO::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal totalCredit = map.values().stream().map(TrialBalanceDTO::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        TrialBalanceResponse response = new TrialBalanceResponse();
//        response.setEntries(new ArrayList<>(map.values()));
//        response.setTotalDebit(totalDebit);
//        response.setTotalCredit(totalCredit);
//        response.setIsBalanced(totalDebit.compareTo(totalCredit) == 0);
//        response.setReconciliationIssues(generateReconciliationIssues(map));
//
//        return response;
//    }
//
//    private List<ReconciliationIssue> generateReconciliationIssues(Map<String, TrialBalanceDTO> map) {
//        List<ReconciliationIssue> issues = new ArrayList<>();
//        for (TrialBalanceDTO dto : map.values()) {
//            if (dto.getDebit().compareTo(dto.getCredit()) != 0) {
//                issues.add(new ReconciliationIssue(dto.getAccountCode(), "Debit and Credit mismatch"));
//            }
//        }
//        return issues;
//    }
//}