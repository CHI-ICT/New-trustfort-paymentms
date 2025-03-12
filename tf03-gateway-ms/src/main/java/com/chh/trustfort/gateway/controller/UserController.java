/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.controller;

import com.chh.trustfort.gateway.Quintuple;
import com.chh.trustfort.gateway.component.ApiPath;
import static com.chh.trustfort.gateway.component.ApiPath.BASE_API;
import com.chh.trustfort.gateway.component.RequestManager;
import com.chh.trustfort.gateway.component.Role;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.payload.user.LoginRequestPayload;
import com.chh.trustfort.gateway.service.impl.GenericUserService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Daniel Ofoleta
 */
@RestController
@RequestMapping(value = BASE_API)
@Tag(name = "User", description = "User REST API")
@RefreshScope
public class UserController {

    @Autowired
    private GenericUserService userService;
    
    @Autowired
    private RequestManager requestManager;
    
    @Autowired 
    Gson gson;

    @PostMapping(value = ApiPath.USER_LOGIN, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processLoginRequest(@RequestHeader(value = "id-token", defaultValue = "AUTH") String idToken,@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {
        
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.LOGIN.getValue(), requestPayload, httpRequest, idToken);
        if(request.isError){
           return new ResponseEntity<>(request.payload, HttpStatus.OK); 
        }
        
        LoginRequestPayload oUserLoginRequestPayload = gson.fromJson(request.payload, LoginRequestPayload.class);
        Object response = userService.processLoginRequest(oUserLoginRequestPayload,request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    

   
}
