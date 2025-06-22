package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;

import com.chh.trustfort.payment.payload.UserActivityPayload;
import org.jasypt.encryption.StringEncryptor;

/**
 *
 * @author Daniel Ofoleta
 */
public interface GenericService {

//    String createWallet(CreateWalletRequestPayload requestPayload, Users users);

//    void createUserActivity(UserActivityPayload requestPayload, AppUser appUser);
//
//    String checkAdminIP(String ipAddress);
//
//    public String generateEncryptionKey(boolean encrypResponse);
//
//    public String generateEcred();
//
//    void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId);
//
//    char getTimePeriod();
//
//    String formatDateWithHyphen(String dateToFormat);

    void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId);

    public void generateLog(String operation,AppUser appUser, String logMessage, String logType, String logLevel, String requestId);

    void createUserActivity(String accountNumber, String activity, String amount, String channel, String message, String mobileNumber, char status);

    String generateMnemonic(int max);

    String formatAmountWithComma(String amount);

    String formatDateWithHyphen(String dateToFormat);


    String generateMultivalue(String fieldName, String stringToParse);

//    String sendOTP();

    char getTimePeriod();


    String generateTransRef(String transType);

    String formatOfsUserCredentials(String ofs, String userCredentials);

    public StringEncryptor getPasswordEncryptor();

}
