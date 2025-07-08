package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.*;
import com.chh.trustfort.accounting.enums.Subsidiary;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chart_of_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "account_code", unique = true, nullable = false)
    private String accountCode;



    @Column(name = "account_name", nullable = false)
    private String accountName;


    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private AccountCategory category;

    @ManyToOne
    @JoinColumn(name = "parent_account_id")
    private ChartOfAccount parentAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subsidiary subsidiary;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType normalBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(unique = true, name = "full_account_code")
    private String fullAccountCode;

    @Column(unique = true, name = "currency_prefixed_code")
    private String currencyPrefixedCode;

    @OneToMany(mappedBy = "parentAccount", cascade = CascadeType.ALL)
    private List<ChartOfAccount> subAccounts;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountClassification classification;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;


}
