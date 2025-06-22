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
public enum Channel {
    MOBILE_APP("MOBILE"),
    USSD("USSD"),
    INTERNET_BANKING("IBANKING");

    private final String channelCode;

    public String getChannelCode() {
        return this.channelCode;
    }

    Channel(String channelCode) {
        this.channelCode = channelCode;
    }
}
