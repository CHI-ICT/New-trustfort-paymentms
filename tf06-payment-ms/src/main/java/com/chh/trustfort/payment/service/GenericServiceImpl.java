/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.OtpTokenRepository;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 *
 * @author Daniel Ofoleta
 */
@Service
public class GenericServiceImpl implements GenericService {

    @Autowired
    JwtTokenUtil jwtToken;

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
    @Autowired
    private OtpTokenRepository otpRepository;

    Logger logger = LoggerFactory.getLogger(GenericServiceImpl.class);


    @Override
    public void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId) {
        try {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(logType.toUpperCase(Locale.ENGLISH));
            strBuilder.append(" - ");

            if (token == null || token.equalsIgnoreCase("")) {
                strBuilder.append("[").append(logMessage).append("]");
            } else {
                String requestBy = jwtToken.getUsernameFromToken(token, appUser.getEncryptionKey());
                String remoteIP = jwtToken.getIPFromToken(token, appUser.getEncryptionKey());
                String channel = jwtToken.getChannelFromToken(token, appUser.getEncryptionKey());

                strBuilder.append("[").append(remoteIP).append(":").append(channel.toUpperCase(Locale.ENGLISH)).append(":").append(requestBy.toUpperCase(Locale.ENGLISH)).append("]");
                strBuilder.append("[").append(appUser.getChannel().toUpperCase(Locale.ENGLISH).toUpperCase(Locale.ENGLISH)).append(":").append(requestId.toUpperCase(Locale.ENGLISH)).append("]");
                strBuilder.append("[").append(logMessage).append("]");
            }
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
    public void generateLog(String operation,AppUser appUser, String logMessage, String logType, String logLevel, String requestId) {
        try {

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(logType.toUpperCase());
            strBuilder.append(" - ");

            if (appUser == null) {
                strBuilder.append("[").append(logMessage).append("]");
            } else {
                String requestBy = appUser.getUserName();
                String remoteIP = "";
                String channel = appUser.getChannel();

                strBuilder.append("[").append(remoteIP).append(":").append(channel.toUpperCase()).append(":").append(requestBy.toUpperCase()).append("]");
                strBuilder.append("[").append(operation.toUpperCase()).append(":").append(requestId.toUpperCase()).append("]");
                strBuilder.append("[").append(logMessage).append("]");
            }
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
    public void createUserActivity(String accountNumber, String activity, String amount, String channel, String message, String mobileNumber, char status) {
//        UserActivity newActivity = new UserActivity();
//        newActivity.setCustomerId(accountNumber);
//        newActivity.setActivity(activity);
//        newActivity.setAmount(amount);
//        newActivity.setChannel(channel);
//        newActivity.setCreatedAt(LocalDateTime.now());
//        newActivity.setMessage(message);
//        newActivity.setMobileNumber(mobileNumber);
//        newActivity.setStatus(status);
//        customerRepository.createUserActivity(newActivity);
    }


    @Override
    public String generateMnemonic(int max) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder mnemonic = new StringBuilder();
        while (max-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            mnemonic.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return mnemonic.toString();
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

    @Override
    public String generateMultivalue(String fieldName, String stringToParse) {
        StringBuilder strBuilder = new StringBuilder();
        int strLen = stringToParse.trim().length();
        String firstString, secondString, thirdString, fourthString;
        if (strLen <= 30) {
            firstString = stringToParse.substring(0, strLen).toUpperCase(Locale.ENGLISH);
            if (!firstString.matches("^[A-Za-z].*$")) {
                firstString = "'" + firstString;
            }
            strBuilder.append(fieldName).append(":1:1::=").append(firstString);
        } else if (strLen > 30 && strLen <= 60) {
            firstString = stringToParse.substring(0, 30).toUpperCase(Locale.ENGLISH);
            if (!firstString.matches("^[A-Za-z].*$")) {
                firstString = "'" + firstString;
            }
            secondString = stringToParse.substring(30, strLen).toUpperCase(Locale.ENGLISH);
            if (!secondString.matches("^[A-Zaa-z].*$")) {
                secondString = "'" + secondString;
            }
            strBuilder.append(fieldName).append(":1:1::=").append(firstString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":2:1::=").append(secondString.toUpperCase(Locale.ENGLISH));
        } else if (strLen > 60 && strLen <= 90) {
            firstString = stringToParse.substring(0, 30).toUpperCase(Locale.ENGLISH);
            if (!firstString.matches("^[A-Za-z].*$")) {
                firstString = "'" + firstString;
            }
            secondString = stringToParse.substring(30, 60).toUpperCase(Locale.ENGLISH);
            if (!secondString.matches("^[A-Za-z].*$")) {
                secondString = "'" + secondString;
            }
            thirdString = stringToParse.substring(60, strLen).toUpperCase(Locale.ENGLISH);
            if (!firstString.matches("^[A-Za-z].*$")) {
                thirdString = "'" + thirdString;
            }
            strBuilder.append(fieldName).append(":1:1::=").append(firstString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":2:1::=").append(secondString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":3:1::=").append(thirdString.toUpperCase(Locale.ENGLISH));
        } else if (strLen > 90) {
            firstString = stringToParse.substring(0, 30).toUpperCase(Locale.ENGLISH);
            if (!firstString.matches("^[A-Za-z].*$")) {
                firstString = "'" + firstString;
            }
            secondString = stringToParse.substring(30, 60).toUpperCase(Locale.ENGLISH);
            if (!secondString.matches("^[A-Za-z].*$")) {
                secondString = "'" + secondString;
            }
            thirdString = stringToParse.substring(60, 90).toUpperCase(Locale.ENGLISH);
            if (!thirdString.matches("^[A-Za-z].*$")) {
                thirdString = "'" + thirdString;
            }
            fourthString = stringToParse.substring(90, strLen).toUpperCase(Locale.ENGLISH);
            if (!fourthString.matches("^[A-Za-z].*$")) {
                fourthString = "'" + fourthString;
            }
            strBuilder.append(fieldName).append(":1:1::=").append(firstString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":2:1::=").append(secondString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":3:1::=").append(thirdString.toUpperCase(Locale.ENGLISH)).append(",");
            strBuilder.append(fieldName).append(":4:1::=").append(fourthString.toUpperCase(Locale.ENGLISH));
        }

        return strBuilder.toString();
    }

//    @Override
//    public String sendOTP() {
//
//        String otp = OtpGenerator.generateOtp();
//    }

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
    public String formatAmountWithComma(String amount) {
        if (amount == null || amount.equals("")) {
            return "";
        }
        if (!amount.matches("[0-9.]{1,}")) {
            return amount;
        }
        double value = (Double.parseDouble(amount.replace(",", "")));
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
//        nf.setRoundingMode(RoundingMode.FLOOR);
        String formattedAmount = nf.format(value);
        return formattedAmount;
    }

    @Override
    public String generateTransRef(String transType) {
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return transType + number;
    }

    @Override
    public String formatOfsUserCredentials(String ofs, String userCredentials) {
        String[] userCredentialsSplit = userCredentials.split("/");
        String newUserCredentials = userCredentialsSplit[0] + "/#######";
        String newOfsRequest = ofs.replace(userCredentials, newUserCredentials);
        return newOfsRequest;
    }

    @Override
    public StringEncryptor getPasswordEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("H*-lLo5,e:2.VN");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        ZeroSaltGenerator zeroSaltGenerator = new ZeroSaltGenerator();
        config.setSaltGenerator((SaltGenerator) zeroSaltGenerator);
        config.setStringOutputType("base64");
        encryptor.setConfig((PBEConfig) config);
        return (StringEncryptor) encryptor;
    }

}
