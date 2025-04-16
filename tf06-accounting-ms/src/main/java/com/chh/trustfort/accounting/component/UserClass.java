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

public enum UserClass {
    ADMIN("AD"),
    CUSTOMER("CU"),
    AGENT("AG"),
    BROKER("BR"),
    REINSURANCE("RE");
    
 
    
    private final String role;
    
    private UserClass(String role) {
        this.role= role;
    }

    

    public String getValue() {
        return this.role;
    }

    
}
