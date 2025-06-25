//package com.chh.trustfort.accounting.Responses;
//
//import com.chh.trustfort.accounting.security.AesService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class ResponseEncryptionUtil {
//
//    private final AesService aesService;
//
//    private static final String KEY_WITH_IV = "99A47258y83921B1627495826M729361/1234567890123456";
//
//    public String encrypt(Object payload) {
//        try {
//            String plainJson = new ObjectMapper().writeValueAsString(payload);
//            return aesService.encrypt(plainJson, KEY_WITH_IV);
//        } catch (Exception e) {
//            throw new RuntimeException("Encryption failed", e);
//        }
//    }
//}
