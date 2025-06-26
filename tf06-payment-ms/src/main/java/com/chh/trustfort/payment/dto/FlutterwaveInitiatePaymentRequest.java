package com.chh.trustfort.payment.dto;

import lombok.Data;

@lombok.Data
public class FlutterwaveInitiatePaymentRequest {
    private String tx_ref;
    private String amount;
    private String currency;
    private String redirect_url;
    private Customer customer;
    private Customization customization;

    @lombok.Data
    public static class Customer {
        private String email;
        private String phonenumber;
        private String name;
    }

    @Data
    public static class Customization {
        private String title;
        private String description;
    }
}
