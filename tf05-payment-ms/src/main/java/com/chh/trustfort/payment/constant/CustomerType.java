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
public enum CustomerType {
    INDIVIDUAL("I"),
    CORPORATE("C"),
    JOINT("J"),
    KIDDIES("K");

    private final String customerType;

    public String getCustomerType() {
        return this.customerType;
    }

    CustomerType(String customerType) {
        this.customerType = customerType;
    }
}
