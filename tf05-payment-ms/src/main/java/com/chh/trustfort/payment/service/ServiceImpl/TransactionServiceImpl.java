package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.TransactionRequestDto;
import com.chh.trustfort.payment.dto.TransactionResponseDto;
import com.chh.trustfort.payment.model.Transaction;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.repository.TransactionRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.service.TransactionService;
import com.chh.trustfort.payment.service.WalletService;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService; // to update balance if needed

    @Override
    @Transactional
    public TransactionResponseDto processTransaction(TransactionRequestDto requestDto, Users user) throws com.chh.trustfort.payment.exception.TransactionException {
        // Validate input and retrieve wallet
        Wallet wallet = walletRepository.findByWalletId(requestDto.getWalletId())
                .orElseThrow(() -> new TransactionException("Wallet not found for ID: " + requestDto.getWalletId()));

        if (wallet == null) {
            throw new TransactionException("Wallet not found for ID: " + requestDto.getWalletId());
        }

        // Ensure the wallet belongs to the user
        if (!wallet.getUsers().getId().equals(user.getId())) {
            throw new TransactionException("Unauthorized access to wallet: " + requestDto.getWalletId());
        }

        // Update wallet balance using WalletService's update logic.
        // For credit, amount is positive; for debit, amount is negative.
        double amount = requestDto.getAmount().doubleValue();
        walletService.updateWalletBalance(requestDto.getWalletId(), amount);

        // Create a new transaction record
        Transaction txn = new Transaction();
        txn.setWalletId(requestDto.getWalletId());
        txn.setTransactionType(requestDto.getTransactionType());
        txn.setAmount(requestDto.getAmount());
        txn.setDescription(requestDto.getDescription());

        Transaction savedTxn = transactionRepository.save(txn);

        // Prepare response DTO
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransactionId(savedTxn.getId());
        responseDto.setMessage("Transaction processed successfully");
        responseDto.setNewBalance(wallet.getBalance());

        return responseDto;
    }

    @Override
    public List<TransactionResponseDto> getTransactionHistory(String walletId, Date startDate, Date endDate, Users user) throws com.chh.trustfort.payment.exception.TransactionException {
        // Validate ownership and existence of the wallet
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new TransactionException("Wallet not found for ID: " + walletId));
        if (wallet == null) {
            throw new TransactionException("Wallet not found for ID: " + walletId);
        }
        if (!wallet.getUsers().getId().equals(user.getId())) {
            throw new TransactionException("Unauthorized access to wallet: " + walletId);
        }

        // Retrieve transactions from the repository
        List<Transaction> transactions = transactionRepository.findByWalletIdAndTransactionDateBetween(walletId, startDate, endDate);

        // Map transactions to DTOs
        List<TransactionResponseDto> responseList = new ArrayList<>();
        for (Transaction txn : transactions) {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setTransactionId(txn.getId());
            dto.setTransactionType(txn.getTransactionType());
            dto.setAmount(txn.getAmount());
            dto.setDescription(txn.getDescription());
            dto.setTransactionDate(txn.getTransactionDate());
            // Optionally, include updated balance if stored
            dto.setNewBalance(wallet.getBalance());
            responseList.add(dto);
        }

        return responseList;
    }
}
