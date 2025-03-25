package com.chh.trustfort.payment.component;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.payload.TokenData;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

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
    private AppUserRepository appUserRepository;

    @Autowired
    private StringEncryptor stringEncryptor;

    public Quintuple<Boolean, String, String, AppUser, String> validateRequest(
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
        AppUser appUser = appUserRepository.getAppUserByUserName(tokenData.getSub());
        if (appUser == null) {
            log.error("User not found for username: {}", tokenData.getSub());
            return unauthorizedResponse("User not found.");
        }
        log.info("Authenticated User: {}", appUser.getUserName());

        // For JWT operations, use the decrypted encryption key from the 'encryption_key' field.
        String decryptedKey;
        try {
            decryptedKey = stringEncryptor.decrypt(appUser.getEncryptionKey());
        } catch (Exception e) {
            log.error("Error decrypting encryption key for user: {}", appUser.getUserName());
            return unauthorizedResponse("Encryption key error.");
        }

        // Validate the token using the decrypted key.
        boolean isExpired = jwtTokenUtil.isTokenExpired(token, decryptedKey);
        if (isExpired) {
            log.error("Token expired for user: {}", appUser.getUserName());
            return unauthorizedResponse("Your token has expired.");
        }

        boolean userHasRole = jwtTokenUtil.userHasRole(token, role, decryptedKey);
        if (!userHasRole) {
            return unauthorizedResponse("You do not have the required permissions to perform this action.");
        }

        // If session validation is enabled, check the ID token.
        if (appUser.isAuthenticateSession()) {
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
        // If the payload starts with '{', assume it is plain JSON.
        String decryptedBody;
        if (requestBody.trim().startsWith("{")) {
            decryptedBody = requestBody;
        } else {
            // Otherwise, decrypt using the ecred (which should be in "key/iv" format)
            decryptedBody = aesService.decrypt(requestBody, appUser.getEcred());
        }

        return new Quintuple<>(false, token, idToken, appUser, decryptedBody);
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

    private Quintuple<Boolean, String, String, AppUser, String> unauthorizedResponse(String message) {
        OmniResponsePayload oResponse = new OmniResponsePayload();
        oResponse.setResponseMessage(message);
        String responseJson = gson.toJson(oResponse);
        return new Quintuple<>(true, null, null, null, responseJson);
    }

}
