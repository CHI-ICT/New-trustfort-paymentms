package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.custom.JournalEntryRepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>, JournalEntryRepositoryCustom{

    @Query("SELECT COALESCE(SUM( " +
            "CASE " +
            " WHEN j.transactionType = 'DEBIT' AND j.account.normalBalance = 'DEBIT' THEN j.amount " +
            " WHEN j.transactionType = 'CREDIT' AND j.account.normalBalance = 'CREDIT' THEN j.amount " +
            " ELSE -j.amount " +
            "END), 0) " +
            "FROM JournalEntry j " +
            "WHERE j.account.classification = :classification " +
            "AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByClassificationAndDateRange(@Param("classification") AccountClassification classification,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

//    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
//            "WHERE j.account.classification = :classification " +
//            "AND j.transactionDate <= :asOfDate")
//    BigDecimal sumByClassification(@Param("classification") AccountClassification classification,
//                                   @Param("asOfDate") LocalDateTime asOfDate);

//    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
//            "WHERE j.account.classification = :classification " +
//            "AND j.transactionDate <= :asOfDate")
//    BigDecimal sumByClassification(@Param("classification") AccountClassification classification,
//                                   @Param("asOfDate") LocalDateTime asOfDate); // ✅ Now matches your call
@Query("SELECT COALESCE(SUM( " +
        "CASE " +
        " WHEN j.transactionType = 'DEBIT' AND j.account.normalBalance = 'DEBIT' THEN j.amount " +
        " WHEN j.transactionType = 'CREDIT' AND j.account.normalBalance = 'CREDIT' THEN j.amount " +
        " ELSE -j.amount " +
        "END), 0) " +
        "FROM JournalEntry j " +
        "WHERE j.account.classification = :classification " +
        "AND j.transactionDate <= :asOfDate")
BigDecimal sumByClassification(@Param("classification") AccountClassification classification,
                               @Param("asOfDate") LocalDate asOfDate);


    @Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification " +
            "AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findEquityEntriesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("classification") AccountClassification classification
    );

//    @Query("SELECT j FROM JournalEntry j WHERE j.transactionDate BETWEEN :startDate AND :endDate")
//    List<JournalEntry> findByTransactionDateBetween(
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate);

    @Query("SELECT j FROM JournalEntry j JOIN FETCH j.account WHERE j.transactionDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findByTransactionDateBetween(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);



    List<JournalEntry> findAllByAccount_ClassificationInAndTransactionDateBetween(
            List<AccountClassification> classifications,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
            "WHERE j.account.classification = :classification " +
            "AND j.account.accountName = :accountName " +
            "AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTaxAmountByClassificationAndAccountName(
            @Param("classification") AccountClassification classification,
            @Param("accountName") String accountName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findByClassificationAndDateRange(@Param("classification") AccountClassification classification,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

//    @Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification AND j.transactionDate <= :asOfDate")
//    List<JournalEntry> findByClassificationAndDate(@Param("classification") AccountClassification classification,
//                                                   @Param("asOfDate") LocalDateTime asOfDate);
//@Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification AND j.transactionDate <= :asOfDate")
//List<JournalEntry> findByClassificationAndDate(@Param("classification") AccountClassification classification,
//                                               @Param("asOfDate") LocalDateTime asOfDate);
@Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification AND j.transactionDate <= :asOfDate")
List<JournalEntry> findByClassificationAndDate(@Param("classification") AccountClassification classification,
                                               @Param("asOfDate") LocalDate asOfDate); // ✅ Fixed

    @Query("SELECT j FROM JournalEntry j WHERE j.account = :account AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findByAccountAndTransactionDateBetween(
            ChartOfAccount account,
            LocalDate startDate,
            LocalDate endDate
    );



    @Query("SELECT COALESCE(SUM(CASE WHEN j.transactionType = a.normalBalance THEN j.amount ELSE -j.amount END), 0) " +
            "FROM JournalEntry j JOIN j.account a " +
            "WHERE a.classification = :classification AND j.transactionDate <= :asOfDate")
    BigDecimal computeNetByClassification(@Param("classification") AccountClassification classification,
                                          @Param("asOfDate") LocalDate asOfDate);



    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
            "WHERE j.account.id = :accountId AND j.transactionType = 'DEBIT' AND j.transactionDate <= :asOfDate")
    BigDecimal sumDebit(@Param("accountId") Long accountId, @Param("asOfDate") LocalDate asOfDate);

    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
            "WHERE j.account.id = :accountId AND j.transactionType = 'CREDIT' AND j.transactionDate <= :asOfDate")
    BigDecimal sumCredit(@Param("accountId") Long accountId, @Param("asOfDate") LocalDate asOfDate);

    @Query("SELECT j FROM JournalEntry j WHERE j.transactionDate < :startDate AND j.account.classification = :classification")
    List<JournalEntry> findEquityEntriesBeforeDate(@Param("startDate") LocalDate startDate, @Param("classification") AccountClassification classification);


}
