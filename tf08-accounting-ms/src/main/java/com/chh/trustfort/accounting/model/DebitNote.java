package com.chh.trustfort.accounting.model;// DebitNote.java

import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "debit_notes")
public class DebitNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    private String payerEmail;

    private String customerName;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private DebitNoteStatus status;

    private String remarks;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private String createdBy;

    // 🔧 Add these fields for reversal support
    private String reversalReason;

    private String reversedBy;

    private LocalDateTime reversedAt;

    @Column(name = "is_reversed")
    private boolean reversed;

    @ManyToOne
    @JoinColumn(name = "replaces_debit_note_id")
    private DebitNote replaces;

    @OneToMany(mappedBy = "replaces")
    @JsonIgnore
    private List<DebitNote> replacedBy;


}