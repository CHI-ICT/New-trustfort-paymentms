/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.component;

/**
 *
 * @author Daniel Ofoleta
 */

public enum Role {
    LOGIN("LOGIN"),
    ADMIN("ADMIN");
 
    
    private final String role;
    
    private Role(String role) {
        this.role= role;
    }

    

    public String getValue() {
        return this.role;
    }

    
}
