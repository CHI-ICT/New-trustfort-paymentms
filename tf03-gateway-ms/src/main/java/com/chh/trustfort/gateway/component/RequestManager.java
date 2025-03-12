package com.chh.trustfort.gateway.component;

import com.chh.trustfort.gateway.Quintuple;
import static com.chh.trustfort.gateway.component.ApiPath.HEADER_STRING;
import static com.chh.trustfort.gateway.component.ApiPath.ID_TOKEN;
import static com.chh.trustfort.gateway.component.ApiPath.TOKEN_PREFIX;
import com.chh.trustfort.gateway.jwt.JwtTokenUtil;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.payload.OmniResponsePayload;
import com.chh.trustfort.gateway.payload.TokenData;
import com.chh.trustfort.gateway.repository.AppUserRepository;
import com.chh.trustfort.gateway.security.AesService;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author dofoleta
 */
@Component
public class RequestManager {

    @Autowired
    JwtTokenUtil jwtToken;

    @Autowired
    Gson gson;

    @Autowired
    AesService aesService;

    @Autowired
    LogService logService;

    @Autowired
    AppUserRepository appUserRepository;

    public Quintuple<Boolean, String, String, AppUser, String> validateRequest(String role, String requestbody, HttpServletRequest httpRequest, String idToken) {

        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        TokenData tokenData = getTokenData(token);
        
        AppUser appUser = appUserRepository.getAppUserByUserName(tokenData.getSub());
//        String userName = jwtToken.getUserNameFromToken(token,appUser.getEncryptionKey());

        OmniResponsePayload oResponse = new OmniResponsePayload();
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
        return new Quintuple(false, token, appUser, idToken, aesService.decrypt(requestbody, appUser));
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
