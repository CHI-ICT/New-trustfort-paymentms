package com.chh.trustfort.accounting.model;


import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountType;
import com.chh.trustfort.accounting.enums.CashFlowCategory;
import lombok.*;

import javax.persistence.*;


@Table(name = "chart_of_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", unique = true, nullable = false)
    private String accountCode;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification", nullable = false)
    private AccountClassification classification;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "department", nullable = true)
    private String department;

    @Column(name = "business_unit", nullable = true)
    private String businessUnit;
}
