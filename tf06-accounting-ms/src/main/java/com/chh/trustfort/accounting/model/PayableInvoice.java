package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayableInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private String reference;

    @ManyToOne
    private Vendor vendor;

    private String vendorName;
    private String vendorEmail;
    private String description;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;


    private String currency; // ✅ Add this line

    private String submittedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @JsonManagedReference  // Owns the serialization
    @JsonIgnore
    private List<PaymentSchedule> schedules = new ArrayList<>();


    @Column(unique = true)
    private String invoiceHash;

}