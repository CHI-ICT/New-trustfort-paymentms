/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.constant;

/**
 *
 * @author Daniel Ofoleta
 */
public enum EmploymentStatus {

    
    EMPLOYED("3"),
    UNEMPLYED("2"),
    SELF_EMPLYED("J");

    private final String employmentStatus;

    public String getEmploymentStatus() {
        return this.employmentStatus;
    }

    EmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
