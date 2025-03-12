/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.controller;

import static com.chh.trustfort.gateway.component.ApiPath.BASE_API;
import static com.chh.trustfort.gateway.component.ApiPath.CHANNEL_USER_CREATE;
import static com.chh.trustfort.gateway.component.ApiPath.CHANNEL_USER_DELETE;
import static com.chh.trustfort.gateway.component.ApiPath.CHANNEL_USER_LIST;
import static com.chh.trustfort.gateway.component.ApiPath.CHANNEL_USER_STATUS_UPDATE;
import static com.chh.trustfort.gateway.component.ApiPath.CHANNEL_USER_UPDATE;
import static com.chh.trustfort.gateway.component.ApiPath.HEADER_STRING;
import static com.chh.trustfort.gateway.component.ApiPath.TOKEN_PREFIX;
import com.chh.trustfort.gateway.payload.AppUserRequestPayload;
import com.chh.trustfort.gateway.payload.AppUserStatusUpdatePayload;
import com.chh.trustfort.gateway.payload.AppUserUpdatePayload;
import com.chh.trustfort.gateway.payload.UsernamePayload;
import com.chh.trustfort.gateway.service.AppUserService;
import com.chh.trustfort.gateway.service.GenericService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "User", description = "User REST API")
@RefreshScope
public class AppUserController {

    @Autowired
    private AppUserService userService;
    @Autowired
    private GenericService genericService;

    @PostMapping(value = CHANNEL_USER_CREATE, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createChannelUser(@Valid @RequestBody AppUserRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
        //Check if the IP address is from the admin console
        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
        if (adminIPResponse != null) {
            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
        }
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        Object response = userService.createChannelUser(token, requestPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = CHANNEL_USER_LIST, produces = "application/json")
    public ResponseEntity<Object> listChannelUser(HttpServletRequest httpRequest) throws Exception {
        //Check if the IP address is from the admin consol
        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
        if (adminIPResponse != null) {
            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
        }

        Object response = userService.getChannelUserList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = CHANNEL_USER_STATUS_UPDATE, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateStatusChannelUser(@Valid @RequestBody AppUserStatusUpdatePayload requestPayload, HttpServletRequest httpRequest) throws Exception {
        //Check if the IP address is from the admin consol
        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
        if (adminIPResponse != null) {
            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
        }
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        Object response = userService.updateChannelUserStatus(token, requestPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = CHANNEL_USER_UPDATE, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateChannelUser(@Valid @RequestBody AppUserUpdatePayload requestPayload, HttpServletRequest httpRequest) throws Exception {
        //Check if the IP address is from the admin consol
        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
        if (adminIPResponse != null) {
            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
        }
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        Object response = userService.updateChannelUser(token, requestPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = CHANNEL_USER_DELETE, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deleteChannelUser(@Valid @RequestBody UsernamePayload requestPayload, HttpServletRequest httpRequest) throws Exception {
        //Check if the IP address is from the admin consol
        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
        if (adminIPResponse != null) {
            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
        }
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        Object response = userService.deleteChannelUser(token, requestPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PostMapping(value = IP_WHITE_LIST_CREATE, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> createIPAddress(@Valid @RequestBody IPRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.createIPConnection(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping(value = IP_WHITE_LIST_DELETE, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> deleteIPAddress(@Valid @RequestBody IPPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.deleteIPAddress(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    @PostMapping(value = IP_WHITE_LIST, produces = "application/json")
//    public ResponseEntity<Object> ipAddressList(HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//
//        Object response = userService.getIPList();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    /*
//        This end point is for development only. Should be removed from production.
//     */
//    @PostMapping(value = ENCRYPT_STRING, produces = "application/json")
//    public ResponseEntity<Object> encryptString(@Valid @RequestBody OmniRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.encryptString(requestPayload.getInput(), token);
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//
//        Object response = userService.getIPList();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    @GetMapping(value = CHANNEL_ROLE_LIST, produces = "application/json")
//    public ResponseEntity<Object> roleList(HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.getAppRoleList(token);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping(value = CHANNEL_ROLE_GROUP_LIST, produces = "application/json")
//    public ResponseEntity<Object> roleGroupList(HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.getRoleGroupList(token);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping(value = CHANNEL_ROLE_GROUP, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> createRoleGroup(@Valid @RequestBody RoleGroupRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.createRoleGroup(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping(value = CHANNEL_ROLE_GROUP_UPDATE, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> updateRoleGroup(@Valid @RequestBody RoleGroupUpdateRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.updateRoleGroup(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping(value = CHANNEL_GROUP_ROLE_LIST, produces = "application/json")
//    public ResponseEntity<Object> groupRoleList(HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.getGroupRolesList(token);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping(value = CHANNEL_ADD_GROUP_ROLE, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> addGroupRoles(@Valid @RequestBody GroupRolesRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.addGroupRoles(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping(value = CHANNEL_REMOVE_GROUP_ROLE, consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Object> removeGroupRoles(@Valid @RequestBody GroupRolesRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//        //Check if the IP address is from the admin consol
//        String adminIPResponse = genericService.checkAdminIP(httpRequest.getRemoteAddr());
//        if (adminIPResponse != null) {
//            return new ResponseEntity<>(adminIPResponse, HttpStatus.OK);
//        }
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        Object response = userService.removeGroupRoles(token, requestPayload);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


}
