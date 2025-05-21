package com.chh.trustfort.accounting.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceivableRequest {

    @NotBlank
    private String customerName;

    private String customerEmail;

    private String customerAccount;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotNull
    private LocalDate dueDate;
}
