package com.chh.trustfort.payment.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author dofoleta
 */
public class Test {

    public static void main(String[] args) {

  
            BCrypt();

        
    }

    private static void BCrypt() {

        String deviceId = "HZM6IF2w46";
        final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        String val = bCryptEncoder.encode(deviceId);
        System.out.println(val);
//        String val = bCryptEncoder.("$2a$10$NxIvkZbnFcGXUY2SX.nPRe5vNFCY5uOv2nCqCb5kvDcjQMngSEaRC");
//        System.out.println(val);
//        boolean imeiMatch = bCryptEncoder.matches(deviceId, val);
//        if (!imeiMatch) {
//            System.out.println(imeiMatch);
//        }
//        System.out.println(imeiMatch);

    }

    private static void Dev() {
        String deviceId = "9999";
        final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        String val = bCryptEncoder.encode(deviceId);
        System.out.println(val);

//        boolean match = bCryptEncoder.matches(deviceId, "$2a$10$sL6FmimHduqYhAkzjkwIt.dE.Mj/hLH32tXdwp6pcR3NuNQJlO6GK");
//        System.out.print("match: ");
//        System.out.println(match);
    }

    private static void PA() {
        String deviceId = "1234";
        final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        String val = bCryptEncoder.encode(deviceId);
        System.out.println(val);
    }

    private static void PasswordEncryptor() {
        String val = getPasswordEncryptor().decrypt("Xh0CZ99mLTy5uguQoJDdOg==");
        System.out.println(val);
        val = getPasswordEncryptor().decrypt("m+Ga3Ve3YvC6oG1HYDFUbPX4WYux0nqv");
        System.out.println(val);
    }

    public static StringEncryptor getPasswordEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("F*-CLo5,e:2.TP"); // encryptor's private key
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;
    }

}
