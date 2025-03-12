/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

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
public class AppUserResponsePayload {

    private String responseCode;
    private String username;
    private String connectingIP;
    private String enabled;
    private String locked;
    private String channel;
    private String organization;
    private String encryptionKey;
    private String accountNumber;
    private double accountOpenBonus;
    private boolean payAccountOpenBonus;
}
