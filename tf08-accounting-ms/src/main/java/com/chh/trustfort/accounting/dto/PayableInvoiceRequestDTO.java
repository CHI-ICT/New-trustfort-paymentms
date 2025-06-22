package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.ExpenseType;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayableInvoiceRequestDTO {
    @NotNull
    private String vendorName;

    @NotNull
    @Email
    private String vendorEmail;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    private String description;

    @NotNull
    private LocalDate dueDate;

    private ExpenseType expenseType;  // ✅ Optional input for classification

    private Boolean paid = false;     // ✅ Optional input for paid flag
}