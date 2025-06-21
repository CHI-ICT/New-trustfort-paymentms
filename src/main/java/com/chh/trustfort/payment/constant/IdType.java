/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.payment.constant;

/**
 *
 * @author Daniel Ofoleta
 */
public enum IdType {
    OTHERS("5"),
    DRIVERS_LICENSE("1"),
    INT_PASSPORT("2"),
    PVC("3"),
    VOTERS_CARD("3"),
    NIN("3"),
    COMPANY_ID("4");
    
    private final String idType;

    public String getIdType() {
        return this.idType;
    }

    IdType(String idType) {
        this.idType = idType;
    }
}
