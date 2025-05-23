package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.CreditStatus;
import com.chh.trustfort.accounting.model.CreditLine;
import com.chh.trustfort.accounting.repository.CreditLineRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DisbursementService implements IDisbursementService {

//    private final CreditLineRepository creditLineRepo;
//    private final WalletService walletService;
//    private final JournalService journalService;
//    private final TransactionLogService transactionLogService;
//
//    public DisbursementService(CreditLineRepository creditLineRepo,
//                               WalletService walletService,
//                               JournalService journalService,
//                               TransactionLogService transactionLogService) {
//        this.creditLineRepo = creditLineRepo;
//        this.walletService = walletService;
//        this.journalService = journalService;
//        this.transactionLogService = transactionLogService;
//    }
//
    @Override
    @Transactional
    public void disburseIfFullyApproved(Long creditLineId) {
//        CreditLine credit = creditLineRepo.findById(creditLineId)
//                .orElseThrow(() -> new RuntimeException("Credit line not found"));

//        if (credit.getStatus() != CreditStatus.APPROVED) {
//            throw new RuntimeException("Credit line is not fully approved");
//        }
//
//        // 1. Credit the wallet
//        walletService.creditToWallet(credit.getUserId(), credit.getAmount());
//
//        // 2. Create journal entry
//        journalService.recordDisbursement(
//                credit.getUserId(),
//                creditLineId,
//                credit.getAmount()
//        );
//
//        // 3. Log transaction
//        transactionLogService.log(
//                credit.getUserId(),
//                "DISBURSEMENT",
//                credit.getAmount()
//        );
    }
}

