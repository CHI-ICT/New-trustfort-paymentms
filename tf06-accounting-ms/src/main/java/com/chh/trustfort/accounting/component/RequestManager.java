package com.chh.trustfort.accounting.component;



import com.chh.trustfort.accounting.Quintuple;
import static com.chh.trustfort.accounting.constant.ApiPath.HEADER_STRING;
import static com.chh.trustfort.accounting.constant.ApiPath.ID_TOKEN;
import static com.chh.trustfort.accounting.constant.ApiPath.TOKEN_PREFIX;
import com.chh.trustfort.accounting.jwt.JwtTokenUtil;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Users;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.payload.TokenData;
import com.chh.trustfort.accounting.repository.AppUserRepository;
import com.chh.trustfort.accounting.repository.UsersRepository;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.payload.TokenData;
import com.chh.trustfort.accounting.repository.AppUserRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author dofoleta
 */
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


    public Quintuple<Boolean, String, String, Users, String> validateRequest(
            String role, String requestBody, HttpServletRequest httpRequest, String idToken) {

        log.info("Validating request for role: {}", role);
        OmniResponsePayload oResponse = new OmniResponsePayload();

        // Extract Authorization Token
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization token");
            return unauthorizedResponse("Missing or invalid authorization token.");
        }
        token = token.replace("Bearer ", "");
        log.info("Extracted Token: {}", token);

        // Extract user details from token
        TokenData tokenData = getTokenData(token);
        if (tokenData == null || tokenData.getSub() == null) {
            log.error("Invalid token data");
            return unauthorizedResponse("Invalid token data.");
        }
        log.info("Token Subject (Username): {}", tokenData.getSub());

        // Fetch user from database
        Users users = usersRepository.getUserByUserName(tokenData.getSub());
        if (users == null) {
            log.error("User not found for username: {}", tokenData.getSub());
            return unauthorizedResponse("User not found.");
        }
        log.info("Authenticated User: {}", users.getUserName());

        // Decrypt the stored encryption key for JWT operations.
        String decryptedKey;
        try {
            decryptedKey = stringEncryptor.decrypt(users.getEncryptionKey());
        } catch (Exception e) {
            log.error("Error decrypting encryption key for user: {}", users.getUserName());
            return unauthorizedResponse("Encryption key error.");
        }

        // Validate the token using the decrypted key.
        boolean isExpired = jwtTokenUtil.isTokenExpired(token, decryptedKey);
        if (isExpired) {
            log.error("Token expired for user: {}", users.getUserName());
            return unauthorizedResponse("Your token has expired.");
        }

        boolean userHasRole = jwtTokenUtil.userHasRole(token, role, decryptedKey);
        if (!userHasRole) {
            return unauthorizedResponse("You do not have the required permissions to perform this action.");
        }

        // Session validation if enabled.
        if (users.isAuthenticateSession()) {
            if (idToken == null) {
                idToken = httpRequest.getHeader("ID-TOKEN");
            }
            if (idToken != null) {
                idToken = idToken.replace("Bearer ", "");
            }
            if (idToken == null || idToken.isBlank()) {
                return unauthorizedResponse("Your session has expired or ID Token is missing.");
            }
        }

        // Determine whether to decrypt the request body.
        // If the payload starts with '{' or matches a known plain pattern (e.g., wallet IDs starting with "WAL-"), then assume it's plain.
        String trimmed = requestBody.trim();
        String decryptedBody;
        if (trimmed.startsWith("{") || trimmed.startsWith("WAL-")) {
            decryptedBody = requestBody;
        } else {
            decryptedBody = aesService.decrypt(requestBody, users.getEcred());
        }

        return new Quintuple<>(false, token, idToken, users, decryptedBody);
    }


    private TokenData getTokenData(String token) {
        try {
            String[] pieces = token.split("\\.");
            if (pieces.length < 2) {
                return null;
            }
            String b64payload = pieces[1];
            String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
            return gson.fromJson(jsonString, TokenData.class);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private Quintuple<Boolean, String, String, Users, String> unauthorizedResponse(String message) {
        OmniResponsePayload oResponse = new OmniResponsePayload();
        oResponse.setResponseMessage(message);
        String responseJson = gson.toJson(oResponse);
        return new Quintuple<>(true, null, null, null, responseJson);
    }

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
