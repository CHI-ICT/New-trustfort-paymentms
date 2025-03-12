/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.payload;

import java.math.BigDecimal;
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
public class ChannelUserListPayload {

    private String userName;
    private String dateCreated;
    private String locked;
    private String enabled;
    private String expired;
    private String ipAddress;
    private String organizationName;
    private String organizationContact;
    private String organizationAddress;
    private String passwordChangeDate;
    private boolean payBonusForAccountOpened;
    private BigDecimal accountBonus;
    private String accountNumber;
    private String role;
    private String channel;
}
