// Repository
package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {

    @Query("SELECT r FROM Receivable r WHERE r.status <> :paidStatus AND r.dueDate < :currentDate")
    List<Receivable> findOverdueReceivables(@Param("paidStatus") ReceivableStatus paidStatus,
                                            @Param("currentDate") LocalDate currentDate);

    List<Receivable> findByStatusNot(ReceivableStatus status);

    boolean existsByPayerEmailAndCurrency(String payerEmail, String currency);

    Optional<Receivable> findByReference(String reference);

    Optional<Receivable> findByReferenceIgnoreCase(String reference);

    List<Receivable> findByMatchingStatusAndPayerEmail(MatchingStatus status, String payerEmail);
//    List<Receivable> findByMatchingStatusAndPayerEmail(MatchingStatus status, String customerEmail);

    List<Receivable> findByMatchingStatusInAndPayerEmail(List<MatchingStatus> statuses, String payerEmail);





//    @Query("SELECT r FROM Receivable r WHERE LOWER(r.reference) = LOWER(:reference)")
//    Optional<Receivable> findByReference(@Param("reference") String reference);



}