package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.CreateWalletRequestPayload;
import com.chh.trustfort.payment.payload.UserActivityPayload;

/**
 *
 * @author Daniel Ofoleta
 */
public interface GenericService {

    String createWallet(CreateWalletRequestPayload requestPayload, AppUser appUser);

    void createUserActivity(UserActivityPayload requestPayload, AppUser appUser);

    String checkAdminIP(String ipAddress);

    public String generateEncryptionKey(boolean encrypResponse);
    
    public String generateEcred();

    void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId);

    char getTimePeriod();

    String formatDateWithHyphen(String dateToFormat);

}
