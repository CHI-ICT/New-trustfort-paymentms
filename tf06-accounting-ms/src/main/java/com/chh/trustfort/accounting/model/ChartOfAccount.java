package com.chh.trustfort.accounting.model;


import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountType;
import com.chh.trustfort.accounting.enums.CashFlowCategory;
import lombok.*;

import javax.persistence.*;


@Table(name = "chart_of_accounts")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountClassification classification;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private String subsidiaryCode;

    @Column(nullable = false)
    private String departmentCode;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AccountType accountType;

}
