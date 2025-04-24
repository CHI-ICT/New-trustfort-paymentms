package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.UserActivityPayload;

/**
 *
 * @author Daniel Ofoleta
 */
public interface GenericService {

    void createUserActivity(UserActivityPayload requestPayload,AppUser appUser);

    String checkAdminIP(String ipAddress);

    public String generateEncryptionKey(boolean encrypResponse);
    
    public String generateEcred();

    void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId);

    char getTimePeriod();

    String formatDateWithHyphen(String dateToFormat);

}
