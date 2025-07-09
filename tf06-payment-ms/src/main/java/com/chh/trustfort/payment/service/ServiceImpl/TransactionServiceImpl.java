package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.dto.TransactionRequestDto;
import com.chh.trustfort.payment.dto.TransactionResponseDto;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Transaction;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.payload.UpdateWalletBalancePayload;
import com.chh.trustfort.payment.repository.TransactionRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.service.TransactionService;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;

    @Transactional
    public TransactionResponseDto processTransaction(TransactionRequestDto requestDto, AppUser appUser, String idToken) throws TransactionException {
        // 1. Retrieve wallet
        Wallet wallet = walletRepository.findByWalletId(requestDto.getWalletId())
                .orElseThrow(() -> new TransactionException("Wallet not found for ID: " + requestDto.getWalletId()));

        // 2. Check ownership
        if (!wallet.getUserId().equals(appUser.getId())) {
            throw new TransactionException("Unauthorized access to wallet: " + requestDto.getWalletId());
        }

        // 3. Update balance via WalletService
        UpdateWalletBalancePayload payload = new UpdateWalletBalancePayload();
        payload.setUserId(requestDto.getWalletId());
        payload.setAmount(requestDto.getAmount().doubleValue());

        String updateResultJson = walletService.updateWalletBalance(payload, idToken, appUser);
        Gson gson = new Gson();
        JsonObject result = gson.fromJson(updateResultJson, JsonObject.class);

        String responseCode = result.get("responseCode").getAsString();
        if (!responseCode.equals(ResponseCode.SUCCESS.getResponseCode())) {
            throw new TransactionException("Wallet balance update failed: " + result.get("responseMessage").getAsString());
        }

        // 4. Record the transaction
        Transaction txn = new Transaction();
        txn.setWalletId(requestDto.getWalletId());
        txn.setTransactionType(requestDto.getTransactionType());
        txn.setAmount(requestDto.getAmount());
        txn.setDescription(requestDto.getDescription());

        Transaction savedTxn = transactionRepository.save(txn);

        // 5. Prepare and return response
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setTransactionId(savedTxn.getId());
        responseDto.setMessage("Transaction processed successfully");
        responseDto.setNewBalance(result.get("newBalance").getAsBigDecimal());

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
