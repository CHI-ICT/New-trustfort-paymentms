/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

import javax.validation.constraints.NotBlank;
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
public class IPRequestPayload {

    @NotBlank(message = "IP Address is required")
    private String ipAddress;
    @NotBlank(message = "Name of Organization is required")
    private String organization;
    @NotBlank(message = "Organization address is required")
    private String organizationAddress;
    @NotBlank(message = "Organization contact number is required")
    private String organizationContact;
    @NotBlank(message = "Organization contact person is required")
    private String organizationContactPerson;
}
