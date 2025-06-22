package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.dto.GenerateAccountNumberResponse;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.VirtualAccount;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.VirtualAccountRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VirtualAccountService {

    private final VirtualAccountRepository virtualAccountRepository;

    private final WalletRepository walletRepository;

    private final LedgerEntryRepository ledgerEntryRepository;
    public GenerateAccountNumberResponse generateAccountNumber(String walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        String accountNumber = generateUniqueMockAccountNumber();

        VirtualAccount account = new VirtualAccount();
        account.setAccountNumber(accountNumber);// still useful for display
        account.setWallet(wallet); // ✅ mapped here
        account.setCreatedAt(LocalDateTime.now());

        virtualAccountRepository.save(account);

        GenerateAccountNumberResponse response = new GenerateAccountNumberResponse();
        response.setAccountNumber(accountNumber);
        response.setWalletId(walletId);
        response.setStatus("SUCCESS");

        return response;
    }


    private String generateUniqueMockAccountNumber() {
        String number;
        do {
            number = "303" + String.valueOf((long)(Math.random() * 1_000_000_000L));
        } while (virtualAccountRepository.existsByAccountNumber(number));
        return number;
    }

    @Transactional
    public String confirmBankTransfer(ConfirmBankTransferRequest request) {
        // Step 1: Find virtual account
        VirtualAccount account = virtualAccountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account number not found"));

        // ✅ Step 2: Get wallet directly from the account (Hibernate will fetch it)
        Wallet wallet = account.getWallet();
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for virtual account");
        }

        // Step 3: Credit the wallet
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.updateUser(wallet);

        // ✅ Step 4: Log the ledger entry
        LedgerEntry ledger = new LedgerEntry();
        ledger.setWalletId(wallet.getWalletId());
        ledger.setTransactionType(TransactionType.CREDIT);
        ledger.setAmount(request.getAmount());
        ledger.setStatus(TransactionStatus.COMPLETED);
        ledger.setDescription("Bank Transfer Funding");
        ledgerEntryRepository.save(ledger);

        return "Wallet funded successfully via bank transfer";
    }

}