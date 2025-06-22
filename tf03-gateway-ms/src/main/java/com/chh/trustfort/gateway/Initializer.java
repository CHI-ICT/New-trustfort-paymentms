/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway;

import com.chh.trustfort.gateway.model.AppUserRole;
import com.chh.trustfort.gateway.model.AppUser;
import com.chh.trustfort.gateway.model.AppUserGroup;
import com.chh.trustfort.gateway.model.AppUserRoleMap;
import com.chh.trustfort.gateway.service.GenericService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.chh.trustfort.gateway.repository.AppUserRepository;
import org.springframework.core.env.Environment;

/**
 *
 * @author Daniel Ofoleta
 */
@Component
public class Initializer implements ApplicationRunner {

    @Autowired
    GenericService genericService;
    @Autowired
    AppUserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        AppUserRole role1 = userRepository.getAppUserRoleUsingRoleName("LOGIN");
        if (role1 == null) {
            AppUserRole newRole1 = new AppUserRole();
            newRole1.setRoleDesc("Login");
            newRole1.setRoleName("LOGIN");
            userRepository.createAppUserRole(newRole1);

            AppUserRole newRole2 = new AppUserRole();
            newRole2.setRoleDesc("Password management");
            newRole2.setRoleName("P_MGT");
            userRepository.createAppUserRole(newRole2);

            AppUserRole newRole3 = new AppUserRole();
            newRole3.setRoleDesc("CUSTOMER INPUT");
            newRole3.setRoleName("C_INP");
            userRepository.createAppUserRole(newRole3);

            AppUserRole newRole4 = new AppUserRole();
            newRole4.setRoleDesc("Customer Authorization");
            newRole4.setRoleName("C_AUTH");
            userRepository.createAppUserRole(newRole4);

            AppUserRole newRole5 = new AppUserRole();
            newRole5.setRoleDesc("Customer Enquiry");
            newRole5.setRoleName("C_ENQ");
            userRepository.createAppUserRole(newRole5);

        }

        // Create default AppUserGroup
        
        AppUserGroup appUserGroup = userRepository.getAppUserGroupByGroupName(env.getProperty("default.group-name"));
        if (appUserGroup == null) {
            AppUserGroup newGroup = new AppUserGroup();
            newGroup.setGroupDesc("Default Group for super app user");
            newGroup.setCreatedAt(LocalDateTime.now());
            newGroup.setGroupName(env.getProperty("default.group-name"));
            newGroup.setCreatedAt(LocalDateTime.now());

            appUserGroup = userRepository.createAppUserGroup(newGroup);

            if (appUserGroup != null) {
                //Fetch all Roles
                List<AppUserRole> roleList = userRepository.getAppUseRoleList();
                // create AppUserRoleMap
                for (AppUserRole appRole : roleList) {
                    AppUserRoleMap oAppUserRoleMap = new AppUserRoleMap();
                    oAppUserRoleMap.setAppUserGroup(appUserGroup);
                    oAppUserRoleMap.setAppUserRole(appRole);
                    oAppUserRoleMap.setCreatedAt(LocalDateTime.now());
                    userRepository.createAppUserRoleMap(oAppUserRoleMap);
                }
            }
        }

        AppUser appUser = userRepository.getAppUserByUserName(env.getProperty("default.app-user"));

        if (appUser == null) {

            AppUser newUser = new AppUser();
            newUser.setChannel(env.getProperty("default.group-name"));
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setCreatedBy("System");
            newUser.setEnabled(true);
            newUser.setEncryptionKey(genericService.generateEncryptionKey(true));
            newUser.setExpired(false);
            newUser.setLocked(false);
            newUser.setPasswordChangeDate(LocalDate.now().plusDays(365));
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setUpdatedBy("System");
            newUser.setUserName(env.getProperty("default.app-user"));
            newUser.setPassword(passwordEncoder.encode(env.getProperty("default.app-pass")));
            newUser.setAppUserGroup(appUserGroup);
            newUser.setEcred(genericService.generateEcred());
            newUser.setAuthenticateDevice(false);
            newUser.setAuthenticateIpAddress(false);
            newUser.setAuthenticateSession(false);
            newUser.setPadding("AES/CBC/PKCS5Padding");

            userRepository.createAppUser(newUser);
        }

    }
}
