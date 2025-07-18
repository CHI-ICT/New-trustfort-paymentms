/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.*;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Daniel Ofoleta
 */
public interface AppUserRepository {

    public AppUser getAppUserByUserName(String userName);

    public AppUser getAppUserById(long appUserId);

    public AppUser createAppUser(AppUser user);

    public AppUser save(AppUser user);

    public List<AppUser> getAppUserList();

    public AppUser updateAppUser(AppUser appUser);

    public AppUser deleteAppUser(AppUser appUser);

    public AppUserActivity getSingleUserActivity(AppUser appUser);

    public String getEncryptionKey(String userName);

    public List<String> getAppUserRoleNameByGroup(UserGroup oAppUserGroup);

    public List<String> getAppUserRoleDescriptionByGroup(AppUserGroup oAppUserGroup);

    public List<String> getAppUserGroupDescription();
    
    public AppUserGroup getAppUserGroupByGroupName(String groupName);

    public AppUserGroup createAppUserGroup(AppUserGroup oAppUserGroup);

    public AppUserGroup updateAppUserGroup(AppUserGroup oAppUserGroup);

    public AppUserGroup deleteAppUserGroup(AppUserGroup oAppUserGroup);

    public List<AppUserGroup> getAppUserGroupList();

    public AppUserRole getAppUserRoleUsingRoleName(String roleName);

    public List<String> getAppUserRoleNameList();

    public List<AppUserRole> getAppUseRoleList();

    public AppUserRole createAppUserRole(AppUserRole appRole);
    
    public AppUserRoleMap createAppUserRoleMap(AppUserRoleMap oAppUserRoleMap);
    
    public AppUserActivity createUserActivity( AppUserActivity userActivity);

    boolean userExistsById(long appUserId);

    Optional<AppUser> findById(Long userId);
}
