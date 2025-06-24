/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.service.impl;

import com.chh.trustfort.gateway.component.ResponseCode;
import com.chh.trustfort.gateway.jwt.JwtTokenUtil;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.model.AppUserActivity;
import com.chh.trustfort.gateway.payload.OmniResponsePayload;
import com.chh.trustfort.gateway.payload.UserActivityPayload;
import com.chh.trustfort.gateway.repository.AppUserRepository;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.chh.trustfort.gateway.service.GenericService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Daniel Ofoleta
 */
@Service
public class GenericServiceImpl implements GenericService {

    @Autowired
    JwtTokenUtil jwtToken;

    @Autowired
    Gson gson;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AppUserRepository appUserRepository;

    private String adminIP;
    @Value("${start.morning}")
    private String startMorning;
    @Value("${end.morning}")
    private String endMorning;
    @Value("${start.afternoon}")
    private String startAfternoon;
    @Value("${end.afternoon}")
    private String endAfternoon;
    @Value("${start.evening}")
    private String startEvening;
    @Value("${end.evening}")
    private String endEvening;
    @Value("${start.night}")
    private String startNight;
    @Value("${end.night}")
    private String endNight;
    Logger logger = LoggerFactory.getLogger(GenericServiceImpl.class);

    @Autowired
    private ApplicationContext context;

    @Override
    public void createUserActivity(UserActivityPayload requestPayload, AppUser appUser) {
        try {
            AppUserActivity userActivity = new AppUserActivity();
            userActivity.setActivity(requestPayload.getActivity());
            userActivity.setAppUser(appUser);
            userActivity.setCreatedAt(LocalDateTime.now());
            userActivity.setCreatedBy(appUser.getUserName());
            userActivity.setDescription(requestPayload.getDescription());
            userActivity.setStatus(requestPayload.getStatus());
            userActivity.setIpAddress(appUser.getIpAddress());

            appUserRepository.createUserActivity(userActivity);
        } catch (Exception ex) {
            logger.debug("User Activity Error [" + appUser + "]-[" + ex.getMessage() + "]");
        }
    }

    @Override
    public String checkAdminIP(String ipAddress) {
        OmniResponsePayload ex = new OmniResponsePayload();
        String[] ipAddresses = adminIP.split(",");
        boolean remoteIPAccepted = true;
        for (String ip : ipAddresses) {
            if (ipAddress.trim().equals(ip)) {
                remoteIPAccepted = true;
            }
        }
        if (!remoteIPAccepted) {
            ex.setResponseCode(ResponseCode.IP_BANNED.getResponseCode());
            ex.setResponseMessage(messageSource.getMessage("appMessages.ip.banned", new Object[0], Locale.ENGLISH));
            String response = gson.toJson(ex);
            return response;
        }
        return null;
    }

    @Override
    public String generateEncryptionKey(boolean encrypResponse) {
        try {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 32;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            if (encrypResponse) {
                StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
                return oStringEncryptor.encrypt(generatedString);
            } else {
                return generatedString;
            }

        } catch (BeansException ex) {
            return null;
        }
    }

    @Override
    public String generateEcred() {
        try {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 32;
            Random random = new Random();

            String encryptionKey = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            String iv = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
//            return oStringEncryptor.encrypt(encryptionKey.concat("/").concat(iv));
            iv = "00000000000000000000000000000000";
            return oStringEncryptor.encrypt(encryptionKey.concat("/").concat(iv));

        } catch (BeansException ex) {
            return null;
        }
    }

    @Override
    public void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId) {
        try {
            String requestBy = jwtToken.getUserNameFromToken(token, appUser.getEncryptionKey());
            String remoteIP = jwtToken.getIPFromToken(token, appUser.getEncryptionKey());
            String channel = jwtToken.getChannelFromToken(token, appUser.getEncryptionKey());

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(logType.toUpperCase(Locale.ENGLISH));
            strBuilder.append(" - ");
            strBuilder.append("[").append(remoteIP).append(":").append(channel.toUpperCase(Locale.ENGLISH)).append(":").append(requestBy.toUpperCase(Locale.ENGLISH)).append("]");
            strBuilder.append("[").append(appUser.getChannel().toUpperCase(Locale.ENGLISH).toUpperCase(Locale.ENGLISH)).append(":").append(requestId.toUpperCase(Locale.ENGLISH)).append("]");
            strBuilder.append("[").append(logMessage).append("]");

            if ("INFO".equalsIgnoreCase(logLevel.trim())) {
                if (logger.isInfoEnabled()) {
                    logger.info(strBuilder.toString());
                }
            }

            if ("DEBUG".equalsIgnoreCase(logLevel.trim())) {
                if (logger.isDebugEnabled()) {
                    logger.error(strBuilder.toString());
                }
            }

        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
    }

    @Override
    public char getTimePeriod() {
        char timePeriod = 'M';
        int hour = LocalDateTime.now().getHour();
        int morningStart = Integer.parseInt(startMorning);
        int morningEnd = Integer.parseInt(endMorning);
        int afternoonStart = Integer.parseInt(startAfternoon);
        int afternoonEnd = Integer.parseInt(endAfternoon);
        int eveningStart = Integer.parseInt(startEvening);
        int eveningEnd = Integer.parseInt(endEvening);
        int nightStart = Integer.parseInt(startNight);
        int nightEnd = Integer.parseInt(endNight);
        //Check the the period of the day
        if (hour >= morningStart && hour <= morningEnd) {
            timePeriod = 'M';
        }
        if (hour >= afternoonStart && hour <= afternoonEnd) {
            timePeriod = 'A';
        }
        if (hour >= eveningStart && hour <= eveningEnd) {
            timePeriod = 'E';
        }
        if (hour >= nightStart && hour <= nightEnd) {
            timePeriod = 'N';
        }
        return timePeriod;
    }

    @Override
    public String formatDateWithHyphen(String dateToFormat) {
        StringBuilder newDate = new StringBuilder(dateToFormat);
        if (dateToFormat.length() == 8) {
            newDate.insert(4, "-").insert(7, "-");
            return newDate.toString();
        }

        return "";
    }

}
