package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.WalletLedgerEntry;
import com.chh.trustfort.payment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerEntryRepository extends JpaRepository<WalletLedgerEntry, Long> {
    List<WalletLedgerEntry> findByWalletId(String userId);

    // ✅ Add this method to fetch entries by status
    List<WalletLedgerEntry> findByStatus(TransactionStatus status);
    List<WalletLedgerEntry> findByWallet_UsersAndTransactionTypeAndCreatedAtAfter(
            Users user, TransactionType type, LocalDateTime after);
    List<WalletLedgerEntry> findByStatusAndDescription(TransactionStatus status, String description);
    boolean existsByReference(String reference);

    List<WalletLedgerEntry> findByStatusIn(List<TransactionStatus> statuses);

    Optional<WalletLedgerEntry> findPendingByWalletId(@Param("walletId") String walletId);

    Optional<WalletLedgerEntry> findByTransactionReference(String txRef);

    boolean existsByDescription(String description);




}
