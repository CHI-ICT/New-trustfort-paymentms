package com.chh.trustfort.gateway.service.impl;

import com.chh.trustfort.gateway.Pair;
import com.chh.trustfort.gateway.component.LogService;
import com.chh.trustfort.gateway.component.ResponseCode;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.model.Users;
import com.chh.trustfort.gateway.payload.OmniResponsePayload;
import com.chh.trustfort.gateway.payload.user.LoginRequestPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.chh.trustfort.gateway.repository.UserRepository;
import com.chh.trustfort.gateway.security.AesService;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author Daniel Ofoleta
 */
@Service
public class GenericUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Gson gson;

    @Autowired
    AesService aesService;

    @Autowired
    LogService logService;

    @Autowired
    MessageSource messageSource;

    @Value("${password.max.tries}")
    private int maximumPasscodeTries;
//    @Value("${pin.max.tries}")
//    private int maximumPinTries;

    public Users findUserByUserName(String userName, AppUser appUser) {
        Users user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return user;
    }

    public String processLoginRequest(LoginRequestPayload requestPayload, AppUser appUser) {
        Users user = userRepository.findByUserName(requestPayload.getUserName());
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        Pair<Boolean, String> response = processAuthenticationRequest(user, requestPayload, appUser);
        return response.item2;
    }

    private Pair<Boolean, String> processAuthenticationRequest(Users user, LoginRequestPayload requestPayload, AppUser appUser) {
        OmniResponsePayload oResponse = new OmniResponsePayload();
        String sResponse = null;
        final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        // Authenticate Device
        if (appUser.isAuthenticateDevice()) {
            if (requestPayload.getDeviceId() == null || !bCryptEncoder.matches(requestPayload.getDeviceId(), user.getDeviceId())) {
                String message = messageSource.getMessage("device.mismatch", new Object[]{"DEVICE", requestPayload.getUserName()}, Locale.ENGLISH);
                oResponse.setResponseCode(ResponseCode.UNKNOWN_DEVICE.getResponseCode());
                oResponse.setResponseMessage(message);
                sResponse = aesService.encrypt(gson.toJson(oResponse), appUser);
                logService.logInfo(appUser, message, "API Response", sResponse);

                return new Pair(false, aesService.encrypt(sResponse, appUser));
            }
        } else if (user.getPasscodeTries() >= maximumPasscodeTries) {
            String message = messageSource.getMessage("password.voilated", new Object[]{"DEVICE", requestPayload.getUserName()}, Locale.ENGLISH);
            oResponse.setResponseCode(ResponseCode.PASSWORD_PIN_MISMATCH.getResponseCode());
            oResponse.setResponseMessage(message);
            sResponse = aesService.encrypt(gson.toJson(oResponse), appUser);
            logService.logInfo(appUser, message, "API Response", sResponse);

            return new Pair(false, aesService.encrypt(sResponse, appUser));

        }

        boolean loggedin = false;
        switch (requestPayload.getAuthType().toUpperCase()) {
            case "PIN": {
                boolean pinMatch = bCryptEncoder.matches(requestPayload.getPasscode(), user.getPin());
                if (!pinMatch) {
                    user.setPinTries(user.getPinTries() + 1);
                    userRepository.updateUser(user);
                    String message = messageSource.getMessage("pin.mismatch", new Object[]{"PIN", requestPayload.getUserName()}, Locale.ENGLISH);
                    oResponse.setResponseCode(ResponseCode.PASSWORD_PIN_MISMATCH.getResponseCode());
                    oResponse.setResponseMessage(message);
                    sResponse = aesService.encrypt(gson.toJson(oResponse), appUser);
                    logService.logInfo(appUser, message, "API Response", sResponse);

                    return new Pair(false, aesService.encrypt(sResponse, appUser));
                } else {
                    user.setPinTries(0);
                    userRepository.updateUser(user);
                    loggedin = true;
                }
                break;
            }

            case "PASSWORD": {
                boolean passwordMatch = bCryptEncoder.matches(requestPayload.getPasscode(), user.getPasscode());
                if (!passwordMatch) {
                    user.setPasscodeTries(user.getPasscodeTries() + 1);
                    userRepository.updateUser(user);
                } else {
                    loggedin = true;
                }
                break;
            }

            case "BIOMETRIC": {
                loggedin = false;
                try {
                    //Next: Use public key from a base64 string to verify the hashed (signed) data: 
                    String strPubKey = user.getBiometricData();

                    byte[] publicKeyBytes = Base64.getDecoder().decode(strPubKey);
                    X509EncodedKeySpec pspec = new X509EncodedKeySpec(publicKeyBytes);
                    KeyFactory pkeyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = pkeyFactory.generatePublic(pspec);

                    String strSignature = requestPayload.getPasscode();

                    Signature publicSignature = Signature.getInstance("SHA256withRSA");
                    publicSignature.initVerify(publicKey);
                    publicSignature.update(requestPayload.getUserName().trim().getBytes(StandardCharsets.UTF_8));

                    byte[] signatureBytes = Base64.getMimeDecoder().decode(strSignature);

                    loggedin = publicSignature.verify(signatureBytes);

                } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException noSuchAlgorithmException) {
                }
                break;
            }

        }

        if (loggedin) {

            // finalizes login process
            user.setPasscodeTries(0);
            userRepository.updateUser(user);

            String message = messageSource.getMessage("password.voilated", new Object[]{"DEVICE", requestPayload.getUserName()}, Locale.ENGLISH);
            oResponse.setResponseCode(ResponseCode.PASSWORD_PIN_MISMATCH.getResponseCode());
            oResponse.setResponseMessage(message);
            sResponse = aesService.encrypt(gson.toJson(oResponse), appUser);
            logService.logInfo(appUser, message, "API Response", sResponse);
        }

        return new Pair(false, aesService.encrypt(sResponse, appUser));
    }

}