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
public enum Occupation {
    PROFESSIONAL("3");
    private final String occupation;

    public String getOccupation() {
        return this.occupation;
    }

    Occupation(String occupation) {
        this.occupation = occupation;
    }
}
