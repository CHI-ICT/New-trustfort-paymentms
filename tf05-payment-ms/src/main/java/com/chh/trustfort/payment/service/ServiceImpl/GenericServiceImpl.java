package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.enums.WalletStatus;
import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.AppUserActivity;

import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.payload.UserActivityPayload;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.GenericService;
import com.google.gson.Gson;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
public class GenericServiceImpl implements GenericService {

    private static final Logger log = LoggerFactory.getLogger(GenericServiceImpl.class);

    @Autowired
    private JwtTokenUtil jwtToken;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private Gson gson;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WalletUtil walletUtil;

    @Autowired
    private AesService aesService;

    @Value("${admin.ip}")
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

//    @Override
//    public String createWallet(@Valid CreateWalletRequestPayload requestPayload, Users users) {
//        log.info("Creating wallet for user ID: {}", users.getId());
//
//        CreateWalletResponsePayload oResponse = new CreateWalletResponsePayload();
//        oResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//        oResponse.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));
//
//        if (walletRepository.existsByOwner(users)) {
//            log.warn("Wallet already exists for user ID: {}", users.getId());
//            oResponse.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
//            return aesService.encrypt(gson.toJson(oResponse), users.getEcred());
//        }
//
//        Wallet wallet = new Wallet();
//        wallet.setWalletId(walletRepository.generateWalletId());
//        wallet.setUsers(users);
//        wallet.setCurrency(requestPayload.getCurrency());
//        wallet.setBalance(BigDecimal.ZERO);
//        wallet.setStatus(WalletStatus.ACTIVE);
//        wallet = walletRepository.createWallet(wallet);
//
//        log.info("Wallet created successfully with ID: {}", wallet.getWalletId());
//
//        if (walletUtil.validateWalletId(wallet.getWalletId())) {
//            oResponse.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            oResponse.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
//        }
//
//        return aesService.encrypt(gson.toJson(oResponse), users.getEcred());
//    }




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

            appUserRepository.save(userActivity.getAppUser()); // Ensure this method exists in repository

        } catch (Exception ex) {
            log.error("User Activity Error [{}] - [{}]", appUser, ex.getMessage());
        }
    }

    @Override
    public String checkAdminIP(String ipAddress) {
        OmniResponsePayload responsePayload = new OmniResponsePayload();
        String[] ipAddresses = adminIP.split(",");

        boolean remoteIPAccepted = false;
        for (String ip : ipAddresses) {
            if (ipAddress.trim().equals(ip.trim())) {
                remoteIPAccepted = true;
                break;
            }
        }

        if (!remoteIPAccepted) {
            responsePayload.setResponseCode(ResponseCode.IP_BANNED.getResponseCode());
            responsePayload.setResponseMessage(messageSource.getMessage("appMessages.ip.banned", null, Locale.ENGLISH));
            return gson.toJson(responsePayload);
        }
        return null;
    }

    @Override
    public String generateEncryptionKey(boolean encryptResponse) {
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

            if (encryptResponse) {
                StringEncryptor encryptor = context.getBean("jasyptStringEncryptor", StringEncryptor.class);
                return encryptor.encrypt(generatedString);
            } else {
                return generatedString;
            }

        } catch (BeansException ex) {
            log.error("Encryption Error: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    public String generateEcred() {
        try {
            Random random = new Random();
            int targetStringLength = 32;

            String encryptionKey = random.ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            String iv = random.ints(48, 122 + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            StringEncryptor encryptor = context.getBean("jasyptStringEncryptor", StringEncryptor.class);
            return encryptor.encrypt(encryptionKey.concat("/").concat(iv));

        } catch (BeansException ex) {
            log.error("Ecred Generation Error: {}", ex.getMessage());
            return null;
        }
    }

    @Override
    public void generateLog(AppUser appUser, String token, String logMessage, String logType, String logLevel, String requestId) {
        try {
            // Decrypt the encryption key for this user
            String decryptedKey = stringEncryptor.decrypt(appUser.getEncryptionKey());

            String requestBy = jwtToken.getUserNameFromToken(token, decryptedKey);
            String remoteIP = jwtToken.getIPFromToken(token, decryptedKey);
            String channel = jwtToken.getChannelFromToken(token, decryptedKey);

            String logEntry = String.format("%s - [%s:%s:%s][%s:%s][%s]",
                    logType.toUpperCase(Locale.ENGLISH), remoteIP, channel, requestBy, appUser.getChannel(), requestId, logMessage);

            if ("INFO".equalsIgnoreCase(logLevel.trim()) && log.isInfoEnabled()) {
                log.info(logEntry);
            }
            if ("DEBUG".equalsIgnoreCase(logLevel.trim()) && log.isDebugEnabled()) {
                log.debug(logEntry);
            }
        } catch (Exception ex) {
            log.error("Log Generation Error: {}", ex.getMessage());
        }
    }


    @Override
    public char getTimePeriod() {
        int hour = LocalDateTime.now().getHour();
        if (hour >= Integer.parseInt(startMorning) && hour <= Integer.parseInt(endMorning)) return 'M';
        if (hour >= Integer.parseInt(startAfternoon) && hour <= Integer.parseInt(endAfternoon)) return 'A';
        if (hour >= Integer.parseInt(startEvening) && hour <= Integer.parseInt(endEvening)) return 'E';
        return 'N';
    }

    @Override
    public String formatDateWithHyphen(String dateToFormat) {
        return (dateToFormat.length() == 8) ? dateToFormat.substring(0, 4) + "-" + dateToFormat.substring(4, 6) + "-" + dateToFormat.substring(6) : "";
    }
}
