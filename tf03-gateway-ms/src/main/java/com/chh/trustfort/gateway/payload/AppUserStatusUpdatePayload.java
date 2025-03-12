/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

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
public class AppUserStatusUpdatePayload {

    @Autowired
    MessageSource messageSource;

    @NotBlank(message = "Username is required")
    private String userName;
    @NotBlank(message = "Update type is required")
    @Pattern(regexp = "^(Enable|Disable|Lock|Unlock)$", message = "Update type must be either Enable, Disable, Lock or Unlock")
    private String updateType;
}
