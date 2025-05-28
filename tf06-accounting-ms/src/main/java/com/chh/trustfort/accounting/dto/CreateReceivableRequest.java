// CreateReceivableRequest.java
package com.chh.trustfort.accounting.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateReceivableRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Email must be valid")
    private String payerEmail;

    @NotBlank(message = "Customer account is required")
    private String customerAccount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    @NotBlank(message = "Created by is required")
    private String createdBy;


}
