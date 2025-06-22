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
public enum StatusCode {
    ACTIVE("A"),
    INACTIVE("I"),
    NEW("N"),
    REMOTE("R"),
    DEACTIVATED("D"),
    PENDING("P"),
    CLOSED("C"),
    FAILED("F");
    private final String statusCode;

    public String getStatusCode() {
        return this.statusCode;
    }

    StatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
