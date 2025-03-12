/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 *
 * @author Daniel Ofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequestPayload {

    @Autowired
    MessageSource messageSource;

    @NotBlank(message = "UserName is required")
    private String userName;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Channel is required")
    @Pattern(regexp = "^(Mobile|IBanking|USSD|ATM|POS|Digital|Agency|Default)$", message = "Channel must be either Mobile, IBanking, USSD, Default, ATM, POS, Agency or Digital")
    private String channel;
    @NotBlank(message = "IP Address is required")
    @Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$", message = "IP Address must be like 192.168.1.1")
    private String ipAddress;
    @NotBlank(message = "Role group is required")
    private String role;
    @NotBlank(message = "Account open bonus is required")
    @Pattern(regexp = "^([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(\\.[0-9][0-9])?$", message = "Account Open Bonus must contain only digits, comma or dot only")
    @Schema(name = "Account Open Bonus", example = "1,000.00", description = "Account Open Bonus")
    private BigDecimal accountOpenBonus;
    @NotBlank(message = "Pay account open bonus is required")
    @Pattern(regexp = "^(True|False)$", message = "Pay account open bonus must be either True or False")
    private String payAccountOpenBonus;
//    @NotBlank(message = "Vendor account number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Debit account must be either 10 digit account number")
    @Schema(name = "Vendor Account Number", example = "0123456789", description = "10 digit NUBAN account number")
    private String accountNumber;
}
