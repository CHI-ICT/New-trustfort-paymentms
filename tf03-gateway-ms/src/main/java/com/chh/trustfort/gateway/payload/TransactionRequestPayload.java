/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Daniel Ofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestPayload {

    @NotBlank(message = "NUBAN account number is required")
    @Pattern(regexp = "^(AIRTIME|CABLE TV|ELECTRICITY|FUNDS TRANSFER)$", message = "Transaction type must be either AIRTIME, CABLE TV, ELECTRICITY or FUNDS TRANSFER")
    @Schema(name = "Trans Type", example = "AIRTIME", description = "Transaction type must be either AIRTIME, CABLE TV, ELECTRICITY or FUNDS TRANSFER")
    private String transType;
    @NotNull(message = "Transaction reference cannot be null")
    @NotEmpty(message = "Transaction reference cannot be empty")
    @NotBlank(message = "Transaction reference cannot be blank")
    private String transRef;
//    @NotBlank(message = "Hash value is required")
//    @Schema(name = "Hash value", example = "PYLON67XXTY78999GHTRE", description = "Encrypted hash value is required")
//    private String hash;
    @NotBlank(message = "Request ID is required")
    @Schema(name = "Request ID", example = "PYLON67XXTY78999GHTRE", description = "Request ID is required")
    private String requestId;
}
