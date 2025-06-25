package com.chh.trustfort.accounting.component;


import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.LogService;
import com.chh.trustfort.accounting.component.ResponseCode;
import com.chh.trustfort.accounting.jwt.JwtTokenUtil;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.payload.TokenData;
import com.chh.trustfort.accounting.repository.AppUserRepository;
import com.chh.trustfort.accounting.repository.UsersRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static com.chh.trustfort.accounting.constant.ApiPath.*;


@Component
public class RequestManager {

    private static final Logger log = LoggerFactory.getLogger(RequestManager.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private AesService aesService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    LogService logService;

    @Autowired
    JwtTokenUtil jwtToken;

    public Quintuple<Boolean, String, String, AppUser, String> validateRequest(String role, @Nullable String requestbody, HttpServletRequest httpRequest, String idToken) {


        OmniResponsePayload oResponse = new OmniResponsePayload();
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        TokenData tokenData = getTokenData(token);

        AppUser appUser = appUserRepository.getAppUserByUserName(tokenData.getSub());

        if(appUser==null){
            String message = "This channel is not found in the register. Contact admin.";
            oResponse.setResponseCode(ResponseCode.TOKEN_EXPIRED.getResponseCode());
            oResponse.setResponseMessage(message);
            String exceptionJson = gson.toJson(oResponse);
            String sResponse = aesService.encrypt(exceptionJson, appUser);
            logService.logInfo(appUser, message, "API Response", exceptionJson);
            return new Quintuple(true, token, idToken, appUser, sResponse);
        }


        boolean expired = jwtToken.isTokenExpired(token,appUser.getEncryptionKey());
        if (expired) {
            String message = "Your token has expired.";
            oResponse.setResponseCode(ResponseCode.TOKEN_EXPIRED.getResponseCode());
            oResponse.setResponseMessage(message);
            String exceptionJson = gson.toJson(oResponse);
            String sResponse = aesService.encrypt(exceptionJson, appUser);
            logService.logInfo(appUser, message, "API Response", exceptionJson);
            return new Quintuple(true, token, idToken, appUser, sResponse);
        }

        boolean userHasRole = jwtToken.userHasRole(token, role,appUser.getEncryptionKey());
        log.info("🔍 Required role: {}", role);
        if (!userHasRole) {
            String message = "You do not have the required permissions to perform this action.";
            oResponse.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
            oResponse.setResponseMessage(message);
            String exceptionJson = gson.toJson(oResponse);
            String sResponse = aesService.encrypt(exceptionJson, appUser);
            logService.logInfo(appUser, message, "API Response", exceptionJson);
            return new Quintuple(true, token, idToken, appUser, sResponse);
        }



        // validte session
        if (appUser.isAuthenticateSession()) {
            if (idToken == null) {
                idToken = httpRequest.getHeader(ID_TOKEN);
            }

            if (idToken != null) {
                idToken = idToken.replace(TOKEN_PREFIX, "");
                System.out.println("idTokenInMethod: "+idToken);
            }

            if ((idToken == null || idToken.isBlank())) {
                String message = "Your session has expired or ID Token is absent in your request.";
                oResponse.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
                oResponse.setResponseMessage(message);
                String exceptionJson = gson.toJson(oResponse);
                String sResponse = aesService.encrypt(exceptionJson, appUser);
                logService.logInfo(appUser, message, "API Response", exceptionJson);
                return new Quintuple(true, token, idToken, appUser, sResponse);
            } else {
                if (!idToken.equalsIgnoreCase("AUTH")) {
                    //ToDo: validate session here
                }
            }
        }

        if(requestbody == null){
            return new Quintuple(false, token, idToken, appUser, null);

        }

        return new Quintuple(false, token, idToken, appUser, aesService.decrypt(requestbody, appUser));
    }
    private TokenData getTokenData(String token) {
        TokenData oTokenData = null;
        try {
            String[] pieces = token.split("\\.");
            String b64payload = pieces[1];
            String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
            oTokenData = gson.fromJson(jsonString, TokenData.class);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }

        return oTokenData;
    }



}
