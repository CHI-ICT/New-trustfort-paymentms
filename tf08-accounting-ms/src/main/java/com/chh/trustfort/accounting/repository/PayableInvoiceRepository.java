package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.dto.PayableInvoiceReportDTO;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.PayableInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayableInvoiceRepository extends JpaRepository<PayableInvoice, Long> {
    boolean existsByInvoiceHash(String invoiceHash);
    boolean existsByVendorEmailAndDescriptionAndAmount(String vendorEmail, String description, BigDecimal amount);

    List<PayableInvoice> findByStatus(InvoiceStatus status);

    List<PayableInvoice> findByDueDateAndStatusNot(LocalDate dueDate, InvoiceStatus status);

    List<PayableInvoice> findByDueDateBeforeAndStatusNot(LocalDate date, InvoiceStatus status);

    Optional<PayableInvoice> findByInvoiceNumber(String invoiceNumber);

//    @Query("SELECT new com.chh.trustfort.accounting.dto.PayableInvoiceReportDTO(" +
//            "p.invoiceNumber, p.vendorName, p.vendorEmail, p.amount, p.currency, " +
//            "p.status, p.payoutCategory, p.dueDate, p.paid) " +
//            "FROM PayableInvoice p " +
//            "WHERE (:vendorEmail IS NULL OR p.vendorEmail = :vendorEmail) " +
//            "AND (:status IS NULL OR p.status = :status) " +
//            "AND (:payoutCategory IS NULL OR p.payoutCategory = :payoutCategory)")
//    List<PayableInvoiceReportDTO> fetchFilteredReports(
//            @Param("vendorEmail") String vendorEmail,
//            @Param("status") InvoiceStatus status,
//            @Param("payoutCategory") PayoutCategory payoutCategory);

    @Query("SELECT new com.chh.trustfort.accounting.dto.PayableInvoiceReportDTO(" +
            "p.invoiceNumber, p.vendorName, p.vendorEmail, p.amount, p.currency, " +
            "p.status, p.payoutCategory, p.dueDate, p.paid) " +
            "FROM PayableInvoice p " +
            "WHERE (:vendorEmail IS NULL OR p.vendorEmail = :vendorEmail) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:payoutCategory IS NULL OR p.payoutCategory = :payoutCategory)")
    List<PayableInvoiceReportDTO> fetchFilteredReports(
            @Param("vendorEmail") String vendorEmail,
            @Param("status") InvoiceStatus status,
            @Param("payoutCategory") PayoutCategory payoutCategory);

    List<PayableInvoice> findByDueDateBetweenAndPaidFalse(LocalDate startDate, LocalDate endDate);
}
