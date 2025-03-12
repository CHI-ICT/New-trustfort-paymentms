/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.service.impl;

import com.chh.trustfort.gateway.component.ResponseCode;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.model.AppUserActivity;
import com.chh.trustfort.gateway.model.AppUserGroup;
import com.chh.trustfort.gateway.payload.AppUserRequestPayload;
import com.chh.trustfort.gateway.payload.AppUserResponsePayload;
import com.chh.trustfort.gateway.payload.AppUserStatusUpdatePayload;
import com.chh.trustfort.gateway.payload.AppUserUpdatePayload;
import com.chh.trustfort.gateway.payload.ChannelUserListPayload;
import com.chh.trustfort.gateway.payload.OmniResponsePayload;
import com.chh.trustfort.gateway.payload.UserActivityPayload;
import com.chh.trustfort.gateway.payload.UsernamePayload;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.chh.trustfort.gateway.repository.AppUserRepository;
import com.chh.trustfort.gateway.service.AppUserService;
import com.chh.trustfort.gateway.service.GenericService;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Daniel Ofoleta
 */
@Service(value = "userService")
public class AppUserServiceImpl implements UserDetailsService, AppUserService {

    @Autowired
    GenericService genericService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    MessageSource messageSource;
    @Autowired
    Gson gson;
    
    @Value("${password.change.days}")
    private String passwordChangeDays;

    Logger logger = LoggerFactory.getLogger(AppUserServiceImpl.class);
  
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.getAppUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(), user.getPassword(), authorities);
    }

    @Override
    public String getChannelUserList() {
        List<AppUser> appUserList = userRepository.getAppUserList();
        if (appUserList == null) {
            return "";
        }

        List<ChannelUserListPayload> userList = new ArrayList<>();
        for (AppUser user : appUserList) {
            ChannelUserListPayload newUser = new ChannelUserListPayload();

            newUser.setChannel(user.getChannel());
            newUser.setDateCreated(user.getCreatedAt().toString());
            newUser.setEnabled(String.valueOf(user.isEnabled()));
            newUser.setExpired(String.valueOf(user.isExpired()));
            newUser.setLocked(String.valueOf(user.isLocked()));
            newUser.setPasswordChangeDate(user.getPasswordChangeDate().toString());

            newUser.setUserName(user.getUserName());
            userList.add(newUser);
        }
        String appUserJson = gson.toJson(userList);
        return appUserJson;
    }

    @Override
    public String createChannelUser(String token, AppUserRequestPayload requestPayload) {
        OmniResponsePayload exception = new OmniResponsePayload();
        String responseJson = "";
        try {
            AppUser appUser = userRepository.getAppUserByUserName(requestPayload.getUserName());
            if (appUser != null) {
                exception.setResponseCode(ResponseCode.RECORD_EXIST.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.username.exist", new Object[]{requestPayload.getUserName()}, Locale.ENGLISH));
                String response = gson.toJson(exception);
                return response;
            }

            //Check if the user group is valid
            AppUserGroup appUserGroup = userRepository.getAppUserGroupByGroupName(requestPayload.getRole());
            if (appUserGroup == null) {
                exception.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.role.notexist", new Object[]{requestPayload.getRole()}, Locale.ENGLISH));
                String response = gson.toJson(exception);
                return response;
            }

            AppUser newUser = new AppUser();
            newUser.setUserName(requestPayload.getUserName());
            newUser.setPassword(passwordEncoder.encode(requestPayload.getPassword()));
            newUser.setChannel(requestPayload.getChannel());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setCreatedBy(requestPayload.getUserName());
            newUser.setEnabled(true);
            newUser.setExpired(false);
            String textToencryptionKey = genericService.generateEncryptionKey(true);
            newUser.setEncryptionKey(textToencryptionKey);
            newUser.setLocked(false);
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setUpdatedBy(requestPayload.getUserName());
            newUser.setPasswordChangeDate(LocalDate.now().plusDays(Integer.parseInt(passwordChangeDays)));
            newUser.setEcred(genericService.generateEcred());
            AppUser createdUser = userRepository.createAppUser(newUser);

            AppUserResponsePayload responsePayload = new AppUserResponsePayload();
            responsePayload.setChannel(createdUser.getChannel());
            responsePayload.setLocked(String.valueOf(createdUser.isLocked()));
            responsePayload.setEnabled(String.valueOf(createdUser.isEnabled()));
            responsePayload.setEncryptionKey(textToencryptionKey);
            responsePayload.setUsername(createdUser.getUserName());
            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());

            responseJson = gson.toJson(responsePayload, AppUserResponsePayload.class);
            logger.debug("Channel User Response [" + requestPayload.getUserName() + "]-[" + responseJson + "]");
            UserActivityPayload userActivity = new UserActivityPayload();
            userActivity.setActivity("Channel User");
            userActivity.setCrudType('C');
            userActivity.setDescription("Channel User created for " + createdUser.getUserName().toUpperCase() + " for " + createdUser.getChannel().toUpperCase());
            userActivity.setStatus('S');
            userActivity.setToken(token);
            genericService.createUserActivity(userActivity, createdUser);
            return responseJson;
        } catch (NumberFormatException | NoSuchMessageException ex) {
            logger.debug("Channel User Error [" + requestPayload.getUserName() + "]-[" + responseJson + "]");
            exception.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            exception.setResponseMessage(ex.getMessage());
            return gson.toJson(exception);
        }
    }


    @Override
    public String updateChannelUserStatus(String token, AppUserStatusUpdatePayload requestPayload) {
        OmniResponsePayload exception = new OmniResponsePayload();
        String responseJson;
        String oldValue = "";
        try {
            AppUser appUser = userRepository.getAppUserByUserName(requestPayload.getUserName());
            if (appUser == null) {
                exception.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.username.not.exist", new Object[]{requestPayload.getUserName()}, Locale.ENGLISH));
                return gson.toJson(exception);
            }

            if (requestPayload.getUpdateType().equalsIgnoreCase("Enable")) {
                oldValue = appUser.isEnabled() ? "Enabled" : "Disabled";
                appUser.setEnabled(true);
            } else if (requestPayload.getUpdateType().equalsIgnoreCase("Disable")) {
                oldValue = appUser.isEnabled() ? "Enabled" : "Disabled";
                appUser.setEnabled(false);
            } else if (requestPayload.getUpdateType().equalsIgnoreCase("Lock")) {
                oldValue = appUser.isLocked() ? "Lock" : "Unlock";
                appUser.setLocked(true);
            } else if (requestPayload.getUpdateType().equalsIgnoreCase("Unlock")) {
                oldValue = appUser.isLocked() ? "Lock" : "Unlock";
                appUser.setLocked(false);
            }

            AppUser updatedAppUser = userRepository.updateAppUser(appUser);
            AppUserResponsePayload responsePayload = new AppUserResponsePayload();
            responsePayload.setChannel(updatedAppUser.getChannel());
            responsePayload.setLocked(String.valueOf(updatedAppUser.isLocked()));
            responsePayload.setEnabled(String.valueOf(updatedAppUser.isEnabled()));
            responsePayload.setUsername(updatedAppUser.getUserName());
            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());

            responseJson = gson.toJson(responsePayload, AppUserResponsePayload.class);
            logger.debug("Channel User Response [" + requestPayload.getUserName() + "]-[" + responseJson + "]");
            UserActivityPayload userActivity = new UserActivityPayload();
            userActivity.setActivity("Channel User");
            userActivity.setCrudType('U');
            userActivity.setDescription(updatedAppUser.getUserName().toUpperCase() + " Status changed from " + oldValue + " to " + requestPayload.getUpdateType());
            userActivity.setStatus('S');
            userActivity.setToken(token);
            genericService.createUserActivity(userActivity, updatedAppUser);
            return responseJson;
        } catch (NoSuchMessageException ex) {
            exception.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            exception.setResponseMessage(ex.getMessage());
            return gson.toJson(exception);
        }
    }

    @Override
    public String updateChannelUser(String token, AppUserUpdatePayload requestPayload) {
        OmniResponsePayload exception = new OmniResponsePayload();
        String responseJson;
        String oldValue = "";
        try {
            AppUser appUser = userRepository.getAppUserByUserName(requestPayload.getUserName());
            if (appUser == null) {
                exception.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.username.not.exist", new Object[]{requestPayload.getUserName()}, Locale.ENGLISH));
                String response = gson.toJson(exception);
                return response;
            }

            switch (requestPayload.getUpdateType()) {
                case "Password": {
                    oldValue = appUser.getPassword();
                    appUser.setPassword(passwordEncoder.encode(requestPayload.getNewValue().trim()));
                    break;
                }
                case "Channel": {
                    oldValue = appUser.getChannel();
                    appUser.setChannel(requestPayload.getNewValue());
                    break;
                }

                case "Account Number": {
                    //Check the account number
                    if (!requestPayload.getNewValue().matches("[0-9]{10}")) {
                        exception.setResponseCode(ResponseCode.FORMAT_ERROR.getResponseCode());
                        exception.setResponseMessage(messageSource.getMessage("appMessages.invalid.account.number", new Object[]{requestPayload.getNewValue()}, Locale.ENGLISH));
                        String response = gson.toJson(exception);
                        return response;
                    }

                    break;
                }
                case "Pay Bonus": {
                    //Check the Pay bonus
                    if (!requestPayload.getNewValue().matches("^(True|False)$")) {
                        exception.setResponseCode(ResponseCode.FORMAT_ERROR.getResponseCode());
                        exception.setResponseMessage(messageSource.getMessage("appMessages.invalid.pay.account.open.bonus", new Object[]{requestPayload.getNewValue()}, Locale.ENGLISH));
                        String response = gson.toJson(exception);
                        return response;
                    }

                    break;
                }
                case "Bonus Amount": {
                    //Check the account number
                    if (!requestPayload.getNewValue().matches("^([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(\\.[0-9][0-9])?$")) {
                        exception.setResponseCode(ResponseCode.FORMAT_ERROR.getResponseCode());
                        exception.setResponseMessage(messageSource.getMessage("appMessages.invalid.bonus.amount", new Object[]{requestPayload.getNewValue()}, Locale.ENGLISH));
                        String response = gson.toJson(exception);
                        return response;
                    }

                    break;
                }
            }

            AppUser updatedAppUser = userRepository.updateAppUser(appUser);
            AppUserResponsePayload responsePayload = new AppUserResponsePayload();
            responsePayload.setChannel(updatedAppUser.getChannel());
            responsePayload.setLocked(String.valueOf(updatedAppUser.isLocked()));
            responsePayload.setEnabled(String.valueOf(updatedAppUser.isEnabled()));
            responsePayload.setUsername(updatedAppUser.getUserName());
            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());

            responseJson = gson.toJson(responsePayload, AppUserResponsePayload.class);
            logger.debug("Channel User Response [" + requestPayload.getUserName() + "]-[" + responseJson + "]");
            UserActivityPayload userActivity = new UserActivityPayload();
            userActivity.setActivity("Channel User");
            userActivity.setCrudType('U');
            userActivity.setDescription(updatedAppUser.getUserName().toUpperCase() + " " + requestPayload.getUpdateType()
                    + " changed from " + oldValue + " to " + requestPayload.getNewValue());
            userActivity.setStatus('S');
            userActivity.setToken(token);
            genericService.createUserActivity(userActivity, updatedAppUser);
            return responseJson;
        } catch (NumberFormatException | NoSuchMessageException ex) {
            exception.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            exception.setResponseMessage(ex.getMessage());
            String response = gson.toJson(exception);
            return response;
        }
    }

    @Override
    public String deleteChannelUser(String token, UsernamePayload requestPayload) {
        OmniResponsePayload exception = new OmniResponsePayload();
        String responseJson;
        try {
            AppUser appUser = userRepository.getAppUserByUserName(requestPayload.getUserName());
            if (appUser == null) {
                exception.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.username.not.exist", new Object[]{requestPayload.getUserName()}, Locale.ENGLISH));
                return gson.toJson(exception);
            }

            //Check if the username is in use
            AppUserActivity userActivity = userRepository.getSingleUserActivity(appUser);
            if (userActivity != null) {
                exception.setResponseCode(ResponseCode.RECORD_INUSE.getResponseCode());
                exception.setResponseMessage(messageSource.getMessage("appMessages.username.inuse", new Object[]{requestPayload.getUserName()}, Locale.ENGLISH));
                return gson.toJson(exception);
            }

            AppUser deletedAppUser = userRepository.deleteAppUser(appUser);
            AppUserResponsePayload responsePayload = new AppUserResponsePayload();
            responsePayload.setChannel(deletedAppUser.getChannel());
            responsePayload.setLocked(String.valueOf(deletedAppUser.isLocked()));
            responsePayload.setEnabled(String.valueOf(deletedAppUser.isEnabled()));
            responsePayload.setUsername(deletedAppUser.getUserName());
            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());

            responseJson = gson.toJson(responsePayload, AppUserResponsePayload.class);
            logger.debug("Channel User Response [" + requestPayload.getUserName() + "]-[" + responseJson + "]");
            UserActivityPayload userActivityLog = new UserActivityPayload();
            userActivityLog.setActivity("Channel User");
            userActivityLog.setCrudType('D');
            userActivityLog.setDescription(deletedAppUser.getUserName().toUpperCase() + " Account deleted");
            userActivityLog.setStatus('S');
            userActivityLog.setToken(token);
            genericService.createUserActivity(userActivityLog, deletedAppUser);
            return responseJson;
        } catch (NoSuchMessageException ex) {
            exception.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            exception.setResponseMessage(ex.getMessage());
            return gson.toJson(exception);
        }
    }


//    @Override
//    public String createRoleGroup(String token, RoleGroupRequestPayload requestPayload) {
//        TokenData tokenData = getTokenData(token);
//        AppUser appUser = userRepository.getAppUserByUserName(tokenData.getSub());
//        String requestBy = jwtToken.getUserNameFromToken(token, appUser.getEncryptionKey());
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            AppUserRoleGroup roleGroup = userRepository.getAppUserRoleGroupUsingGroupName(requestPayload.getRoleName());
//            if (roleGroup != null) {
//                errorResponse.setResponseCode(ResponseCode.RECORD_EXIST.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.role.exist", new Object[]{requestPayload.getRoleName()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//
//                UserActivityPayload userActivity = new UserActivityPayload();
//                userActivity.setActivity("Channel Role");
//                userActivity.setCrudType('C');
//                userActivity.setDescription(messageSource.getMessage("appMessages.user.notexist", new Object[0], Locale.ENGLISH));
//                userActivity.setStatus('F');
//                userActivity.setToken(token);
//                genericService.createUserActivity(userActivity,appUser);
//                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            
//
//            AppUserRoleGroup oAppRoleGroup = new AppUserRoleGroup();
//            oAppRoleGroup.setAppUser(appUser);
//            oAppRoleGroup.setCreatedAt(LocalDateTime.now());
//            oAppRoleGroup.setGroupName(requestPayload.getRoleName());
//            userRepository.createRoleGroup(oAppRoleGroup);
//
//            RoleListResponsePayload responsePayload = new RoleListResponsePayload();
//            List<String> roles = userRepository.getRolesFromAppUserRoleGroup();
//            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            responsePayload.setRoles(roles);
//            return gson.toJson(responsePayload);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String updateRoleGroup(String token, RoleGroupUpdateRequestPayload requestPayload) {
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            //Check if the old role name is valid
//            AppUserRoleGroup oldAppRoleGroup = userRepository.getAppUserRoleGroupUsingGroupName(requestPayload.getOldRoleName());
//            if (oldAppRoleGroup == null) {
//                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.role.notexist", new Object[]{requestPayload.getOldRoleName()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            //Check if the new role name is in use
//            AppUserRoleGroup oAppRoleGroup = userRepository.getAppUserRoleGroupUsingGroupName(requestPayload.getNewRoleName());
//            if (oAppRoleGroup != null) {
//                errorResponse.setResponseCode(ResponseCode.RECORD_INUSE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.role.exist", new Object[]{requestPayload.getNewRoleName()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            oldAppRoleGroup.setGroupName(requestPayload.getNewRoleName());
//            userRepository.updateRoleGroup(oldAppRoleGroup);
//
//            //Return successful response
//            RoleListResponsePayload responsePayload = new RoleListResponsePayload();
//            List<String> roles = userRepository.getRolesFromAppUserRoleGroup();
//            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            responsePayload.setRoles(roles);
//            return gson.toJson(responsePayload);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String addGroupRoles(String token, GroupRolesRequestPayload requestPayload) {
//       TokenData tokenData = getTokenData(token);
//        AppUser appUser = userRepository.findByUserName(tokenData.getSub());
//        String requestBy = jwtToken.getUserNameFromToken(token, appUser.getEncryptionKey());
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            AppUserRoleGroup roleGroup = userRepository.getAppUserRoleGroupUsingGroupName(requestPayload.getRoleGroupName());
//            if (roleGroup == null) {
//                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.role.notexist", new Object[]{requestPayload.getRoleGroupName()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
////            //Check the channel information
////            AppUser appUser = userRepository.getAppUserUsingUsername(requestBy);
////            if (appUser == null) {
////                UserActivityPayload userActivity = new UserActivityPayload();
////                userActivity.setActivity("Channel Group Roles");
////                userActivity.setCrudType('C');
////                userActivity.setDescription(messageSource.getMessage("appMessages.user.notexist", new Object[0], Locale.ENGLISH));
////                userActivity.setStatus('F');
////                userActivity.setToken(token);
////                genericService.createUserActivity(userActivity);
////                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
////                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
////                return gson.toJson(errorResponse);
////            }
//
//            List<String> summary = new ArrayList<>();
//            for (String rol : requestPayload.getRoles()) {
//                AppUserRole role = userRepository.getRoleUsingRoleName(rol);
//                if (role != null) {
//                    AppUserRoleGroupMap groupRole = userRepository.getAppUserRoleGroup(roleGroup, role);
//                    if (groupRole == null) {
//                        AppUserRoleGroupMap newRole = new AppUserRoleGroupMap();
//                        newRole.setAppUserRole(role);
//                        newRole.setAppUser(appUser);
//                        newRole.setCreatedAt(LocalDateTime.now());
//                        newRole.setAppUserRoleGroup(roleGroup);
//
//                        userRepository.createAppUserRoleGroupMap(newRole);
//                        summary.add("The role " + rol + " added");
//                    }
//                }
//            }
//
//            GroupRolesResponsePayload responsePayload = new GroupRolesResponsePayload();
//            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            List<String> roles = userRepository.getAppUserRoleGroupMap(roleGroup);
//            responsePayload.setRoles(roles);
//            responsePayload.setSummary(summary);
//            return gson.toJson(responsePayload);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String removeGroupRoles(String token, GroupRolesRequestPayload requestPayload) {
//        TokenData tokenData = getTokenData(token);
//        AppUser appUser = userRepository.findByUserName(tokenData.getSub());
//        String requestBy = jwtToken.getUserNameFromToken(token, appUser.getEncryptionKey());
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            AppUserRoleGroup roleGroup = userRepository.getAppUserRoleGroupUsingGroupName(requestPayload.getRoleGroupName());
//            if (roleGroup == null) {
//                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.role.notexist", new Object[]{requestPayload.getRoleGroupName()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            //Check the channel information
////            AppUser appUser = userRepository.getAppUserUsingUsername(requestBy);
////            if (appUser == null) {
////                UserActivityPayload userActivity = new UserActivityPayload();
////                userActivity.setActivity("Channel Group Roles");
////                userActivity.setCrudType('C');
////                userActivity.setDescription(messageSource.getMessage("appMessages.user.notexist", new Object[0], Locale.ENGLISH));
////                userActivity.setStatus('F');
////                userActivity.setToken(token);
////                genericService.createUserActivity(userActivity);
////                errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
////                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
////                return gson.toJson(errorResponse);
////            }
//
//            List<String> summary = new ArrayList<>();
//            for (String rol : requestPayload.getRoles()) {
//                AppUserRole role = userRepository.getRoleUsingRoleName(rol);
//                if (role != null) {
//                    AppUserRoleGroupMap groupRole = userRepository.getAppUserRoleGroup(roleGroup, role);
//                    if (groupRole != null) {
//                        //Remove the role
//                        userRepository.removeGroupRoles(groupRole);
//                        summary.add("The role " + rol + " removed");
//                    }
//                }
//            }
//
//            GroupRolesResponsePayload responsePayload = new GroupRolesResponsePayload();
//            responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            List<String> roles = userRepository.getAppUserRoleGroupMap(roleGroup);
//            responsePayload.setRoles(roles);
//            responsePayload.setSummary(summary);
//            return gson.toJson(responsePayload);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String getRoleGroupList(String token) {
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            List<String> roles = userRepository.getRolesFromAppUserRoleGroup();
//            if (roles != null) {
//                RoleListResponsePayload responsePayload = new RoleListResponsePayload();
//                responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//                responsePayload.setRoles(roles);
//                return gson.toJson(responsePayload);
//            }
//
//            errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            errorResponse.setResponseMessage(messageSource.getMessage("appMessages.no.record", new Object[0], Locale.ENGLISH));
//            return gson.toJson(errorResponse);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String getAppRoleList(String token) {
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            List<String> roles = userRepository.getAppRolesList();
//            if (roles != null) {
//                RoleListResponsePayload responsePayload = new RoleListResponsePayload();
//                responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//                responsePayload.setRoles(roles);
//                return gson.toJson(responsePayload);
//            }
//
//            errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            errorResponse.setResponseMessage(messageSource.getMessage("appMessages.no.record", new Object[0], Locale.ENGLISH));
//            return gson.toJson(errorResponse);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String getGroupRolesList(String token) {
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        try {
//            List<RolePayload> roleList = new ArrayList<>();
//            List<AppUserRoleGroup> roles = userRepository.getAppUserRoleGroup();
//            GroupRoleListResponsePayload responsePayload = new GroupRoleListResponsePayload();
//            if (roles != null) {
//                responsePayload.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//                for (AppUserRoleGroup rol : roles) {
//                    RolePayload rolePayload = new RolePayload();
//                    rolePayload.setGroup(rol.getGroupName());
//                    List<String> groupRoles = userRepository.getAppUserRoleGroupUsingGroupName(rol);
//                    rolePayload.setRoles(groupRoles);
//                    roleList.add(rolePayload);
//                }
//                responsePayload.setRoles(roleList);
//                return gson.toJson(responsePayload);
//            }
//            errorResponse.setResponseCode(ResponseCode.RECORD_NOT_EXIST.getResponseCode());
//            errorResponse.setResponseMessage(messageSource.getMessage("appMessages.no.record", new Object[0], Locale.ENGLISH));
//            return gson.toJson(errorResponse);
//        } catch (NoSuchMessageException ex) {
//            errorResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public List<String> getUserRoles(String username) {
//        try {
//            AppUser appUser = userRepository.getAppUserUsingUsername(username);
//            if (appUser == null) {
//                return null;
//            }
//
//            List<AppUserRoleGroupMap> userRoles = userRepository.getAppUserRoleGroupMap(appUser);
//            if (userRoles == null) {
//                return null;
//            }
//
//            List<String> roles = new ArrayList<>();
//            for (AppUserRoleGroupMap rol : userRoles) {
//                roles.add(rol.getAppUserRole().getRoleName());
//            }
//
//            return roles;
//        } catch (Exception ex) {
//            return null;
//        }
//    }

//    @Override
//    public void lockUser(String ipAddress) {
//        ConnectingIP connectingIP = userRepository.getIPAddressUsingIP(ipAddress);
//        if (connectingIP != null) {
//            AppUser appUser = userRepository.findUserByIPAddress(connectingIP);
//            if (appUser != null) {
//                appUser.setEnabled(false);
//                userRepository.updateAppUser(appUser);
//            }
//        }
//    }


}
