package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.custom.JournalEntryRepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>, JournalEntryRepositoryCustom{

    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
           "WHERE j.account.classification = :classification " +
           "AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByClassificationAndDateRange(@Param("classification") AccountClassification classification,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(j.amount), 0) FROM JournalEntry j " +
            "WHERE j.account.classification = :classification " +
            "AND j.transactionDate <= :asOfDate")
    BigDecimal sumByClassification(@Param("classification") AccountClassification classification,
                                   @Param("asOfDate") LocalDateTime asOfDate);

    @Query("SELECT j FROM JournalEntry j WHERE j.account.classification = :classification " +
            "AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<JournalEntry> findEquityEntriesBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
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
            "AND j.account.name = :accountName " +
            "AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTaxAmountByClassificationAndAccountName(
            @Param("classification") AccountClassification classification,
            @Param("accountName") String accountName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
