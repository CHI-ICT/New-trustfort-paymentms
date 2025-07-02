package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByWalletId(String walletId);

    // ✅ Add this method to fetch entries by status
    List<LedgerEntry> findByStatus(TransactionStatus status);
    List<LedgerEntry> findByWallet_UsersAndTransactionTypeAndCreatedAtAfter(
            Users user, TransactionType type, LocalDateTime after);
    List<LedgerEntry> findByStatusAndDescription(TransactionStatus status, String description);
    boolean existsByReference(String reference);

    List<LedgerEntry> findByStatusIn(List<TransactionStatus> statuses);

    Optional<LedgerEntry> findPendingByWalletId(@Param("walletId") String walletId);

    Optional<LedgerEntry> findByTransactionReference(String txRef);

    boolean existsByDescription(String description);




}
