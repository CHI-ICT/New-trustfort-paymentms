package com.chh.trustfort.payment.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


import lombok.Data;
import java.util.List;

@Data
public class CreateWalletRequestPayload {
    private String code;
    private String message;
    private UserData data;
    private String currency;

    @Data
    public static class UserData {
        private String userName;
        private String emailAddress;
        private String fullname;
        private String phoneNumber;
        private Integer userId;
        private String userClass;
        private Integer userGroupId;
        private String ipAddress;
        private List<Integer> roles;
        private boolean is2fa;
    }
}

