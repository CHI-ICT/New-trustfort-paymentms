/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.component;

/**
 *
 * @author Daniel Ofoleta
 */
public enum Role {
    CFREATE_WALLET("C_WAL"),
    FUND_WALLET("F_WAL"),
    FETCH_WALLET("W_FET"),
    TRANSFER_FUNDS("T_FND"),
    CHECK_BALANCE("W_BAL"),
    TRANSACTION_HISTORY("T_HIS"),
    WITHDRAW_FUNDS("W_FND"),
    FREEZE_WALLET("W_FRZ"),
    UNFREEZE_WALLET("W_UFR"),
    CLOSE_WALLET("W_CLS"),
    LOCK_FUNDS("L_FND"),
    UNLOCK_FUNDS("U_FND")
    
    ;

    private final String role;

    private Role(String role) {
        this.role = role;
    }

    public String getValue() {
        return this.role;
    }

}
