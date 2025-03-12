/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.controller;

import static com.chh.trustfort.gateway.component.ApiPath.BASE_API;
import static com.chh.trustfort.gateway.component.ApiPath.GENERATE_TOKEN;
import static com.chh.trustfort.gateway.component.ApiPath.IP_BLOCK;
import static com.chh.trustfort.gateway.component.ApiPath.STATISTICS_MEMORY;
import static com.chh.trustfort.gateway.component.ApiPath.USER_LOCKED;
import com.chh.trustfort.gateway.component.ResponseCode;
import com.chh.trustfort.gateway.jwt.JwtTokenUtil;
import com.chh.trustfort.gateway.payload.LoginUserPayload;
import com.chh.trustfort.gateway.payload.MemoryStats;
import com.chh.trustfort.gateway.payload.OmniResponsePayload;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Daniel Ofoleta
 */
@RestController
@RequestMapping(value = BASE_API)
@Tag(name = "Auth", description = "Authentication REST API")
@RefreshScope
public class AuthenticationController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    MessageSource messageSource;
    
    @Autowired
    Gson gson;

    @PostMapping(value = GENERATE_TOKEN)
    public ResponseEntity<?> generateToken(@Valid @RequestBody LoginUserPayload loginUser, HttpServletRequest httpRequest) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword())
        );
       
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.doGenerateToken(loginUser.getUserName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = IP_BLOCK)
    public ResponseEntity<Object> ipWhitelisting(@Valid @RequestBody LoginUserPayload loginUser, HttpServletRequest httpRequest) throws AuthenticationException {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.IP_BANNED.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("appMessages.ip.banned", new Object[0], Locale.ENGLISH));

        String responseJson = gson.toJson(response);
        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    @PostMapping(value = USER_LOCKED)
    public ResponseEntity<Object> userLocked(@Valid @RequestBody LoginUserPayload loginUser, HttpServletRequest httpRequest) throws AuthenticationException {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.IP_BANNED.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("appMessages.user.locked", new Object[0], Locale.ENGLISH));

        String responseJson = gson.toJson(response);
        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    @GetMapping(value = STATISTICS_MEMORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch the JVM statistics")
    public MemoryStats getMemoryStatistics(HttpServletRequest httpRequest) {
        MemoryStats stats = new MemoryStats();
        stats.setHeapSize(Runtime.getRuntime().totalMemory());
        stats.setHeapMaxSize(Runtime.getRuntime().maxMemory());
        stats.setHeapFreeSize(Runtime.getRuntime().freeMemory());
        return stats;
    }

//    @PostMapping(value = VALIDATE_TOKEN)
//    public ResponseEntity<?> validateToken(@Valid @RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) throws AuthenticationException {
//        
//        AppUserPayload payload = null;
//        AppUser appUser =null;
//        
//        AppUserPayload omniPayload = new AppUserPayload();
//        
//        if (requestPayload != null) {
//            appUser = userService.getAppUser(requestPayload.getAppUserId());
//        }
//        if (appUser == null) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            omniPayload.setResponseMessage("App user not found!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//        if (requestPayload != null) {
//            PlainTextPayload res = aesService.decrypt(requestPayload, appUser);
//            if (res.isError()) {
//
//            } else {
//                payload = gson.fromJson(res.getPlainTextPayload(), AppUserPayload.class);
//            }
//        }
//
//        
//        if (payload == null) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            omniPayload.setResponseMessage("App user not found found!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//        boolean expired = jwtTokenUtil.isTokenExpired(payload.getToken());
//        if (expired) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.TOKEN_EXPIRED.getResponseCode());
//            omniPayload.setResponseMessage("Expired access token not allowed!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//        String userName = jwtTokenUtil.getUserNameFromToken(payload.getToken());
//        if (userName == null) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            omniPayload.setResponseMessage("App user not found found!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//
//        if (appUser.isExpired() || appUser.isLocked() || !appUser.isEnabled()) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.CUSTOMER_DISABLED.getResponseCode());
//            omniPayload.setResponseMessage("App user is disabled!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//
//        boolean userHasRole = jwtTokenUtil.userHasRole(payload.getToken(), payload.getRole());
//        if (!userHasRole) {
//            GenericPayload oGenericPayload = new GenericPayload();
//            omniPayload.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
//            omniPayload.setResponseMessage("App user is has no such role assigned!");
//            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//            return ResponseEntity.ok(gson.toJson(oGenericPayload));
//        }
//
//        GenericPayload oGenericPayload = new GenericPayload();
//
//        omniPayload.setAuthenticatDevice(appUser.isAuthenticateDevice());
//        omniPayload.setChannel(appUser.getChannel());
//        omniPayload.setId(appUser.getId());
//        omniPayload.setUserName(appUser.getUserName());
//  
//
//        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniPayload), appUser));
//        return ResponseEntity.ok(gson.toJson(oGenericPayload));
//    }

}
