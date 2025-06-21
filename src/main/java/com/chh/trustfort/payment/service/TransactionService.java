package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.TransactionRequestDto;
import com.chh.trustfort.payment.dto.TransactionResponseDto;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import org.hibernate.TransactionException;

import java.util.List;

public interface TransactionService {
    /**
     * Processes a transaction and records it.
     *
     * @param requestDto Details of the transaction request.
     * @param user The user initiating the transaction.
     * @return Transaction response details.
     * @throws TransactionException If the transaction fails.
     */
    TransactionResponseDto processTransaction(TransactionRequestDto requestDto, AppUser appUser, String idToken) throws com.chh.trustfort.payment.exception.TransactionException;

    /**
     * Retrieves the transaction history for a wallet.
     *
     * @param walletId Wallet identifier.
     * @param startDate Start date for history.
     * @param endDate End date for history.
     * @param user The user requesting the history.
     * @return List of transaction records.
     * @throws TransactionException If retrieval fails.
     */
    List<TransactionResponseDto> getTransactionHistory(String walletId, java.util.Date startDate, java.util.Date endDate, Users user) throws com.chh.trustfort.payment.exception.TransactionException;
}
