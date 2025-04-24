/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.*;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Daniel Ofoleta
 */
@Repository
@Transactional
public class AppUserRepositoryImpl implements AppUserRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public AppUser getAppUserByUserName(String userName) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUser> query = em.createQuery("SELECT t FROM AppUser t WHERE t.userName = :userName", AppUser.class)
                .setParameter("userName", userName);
        List<AppUser> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public AppUser getAppUserById(long appUserId) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUser> query = em.createQuery("SELECT t FROM AppUser t WHERE t.id = :appUserId", AppUser.class)
                .setParameter("appUserId", appUserId);
        List<AppUser> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public AppUser createAppUser(AppUser user) {
        em.persist(user);
        em.flush();
        return user;
    }

    @Override
    public AppUser save(AppUser user) {
        if (user.getId() == null) {
            em.persist(user);  // Insert new record
        } else {
            user = em.merge(user);  // Update existing record
        }
        em.flush();
        return user;
    }


    @Override
    public List<AppUser> getAppUserList() {
        TypedQuery<AppUser> query = em.createQuery("SELECT t FROM AppUser t", AppUser.class);
        List<AppUser> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public AppUser updateAppUser(AppUser appUser) {
        em.merge(appUser);
        em.flush();
        return appUser;
    }

    @Override
    public AppUser deleteAppUser(AppUser appUser) {
        em.remove(em.contains(appUser) ? appUser : em.merge(appUser));
        em.flush();
        return appUser;
    }

    @Override
    public AppUserActivity getSingleUserActivity(AppUser appUser) {
        TypedQuery<AppUserActivity> query = em.createQuery("SELECT t FROM AppUserActivity t WHERE t.appUser = :appUser", AppUserActivity.class)
                .setParameter("appUser", appUser);
        List<AppUserActivity> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public String getEncryptionKey(String userName) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<String> query = em.createQuery(
                        "SELECT t.encryptionKey FROM AppUser t WHERE LOWER(t.userName) = LOWER(:userName)", String.class)
                .setParameter("userName", userName.toLowerCase());
        List<String> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public List<String> getAppUserRoleNameByGroup(UserGroup oAppUserGroup) {
        TypedQuery<String> query = em.createQuery(
                        "SELECT t.appUserRole.roleName FROM AppUserRoleMap t WHERE t.appUserGroup = :oAppUserGroup",
                        String.class)
                .setParameter("oAppUserGroup", oAppUserGroup);
        List<String> record = query.getResultList();
        return record.isEmpty() ? null : record;
    }


    @Override
    public List<String> getAppUserRoleDescriptionByGroup(AppUserGroup oAppUserGroup) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<String> query = em.createQuery("SELECT t.appUserRole.roleDesc FROM AppUserRoleMap t WHERE t.appUserGroup = :oAppUserGroup", String.class)
                .setParameter("oAppUserGroup", oAppUserGroup);
        List<String> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public List<String> getAppUserGroupDescription() {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<String> query = em.createQuery("SELECT t.groupDesc FROM AppUserGroup t ", String.class);
        List<String> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public AppUserGroup createAppUserGroup(AppUserGroup oAppUserGroup) {
        em.persist(oAppUserGroup);
        em.flush();
        return oAppUserGroup;
    }

    @Override
    public AppUserGroup updateAppUserGroup(AppUserGroup oAppUserGroup) {
        em.merge(oAppUserGroup);
        em.flush();
        return oAppUserGroup;
    }

    @Override
    public AppUserGroup deleteAppUserGroup(AppUserGroup oAppUserGroup) {
        em.remove(em.contains(oAppUserGroup) ? oAppUserGroup : em.merge(oAppUserGroup));
        em.flush();
        return oAppUserGroup;
    }

    @Override
    public List<AppUserGroup> getAppUserGroupList() {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUserGroup> query = em.createQuery("SELECT t FROM AppUserGroup t", AppUserGroup.class);
        List<AppUserGroup> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public AppUserGroup getAppUserGroupByGroupName(String groupName) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUserGroup> query = em.createQuery("SELECT t FROM AppUserGroup t WHERE t.groupName = :groupName", AppUserGroup.class)
                .setParameter("groupName", groupName);
        List<AppUserGroup> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public AppUserRole getAppUserRoleUsingRoleName(String roleName) {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUserRole> query = em.createQuery("SELECT t FROM AppUserRole t WHERE t.roleName = :roleName", AppUserRole.class)
                .setParameter("roleName", roleName);
        List<AppUserRole> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    @Override
    public List<String> getAppUserRoleNameList() {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<String> query = em.createQuery("SELECT t.roleName FROM AppUserRole t", String.class);
        List<String> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public List<AppUserRole> getAppUseRoleList() {
        @SuppressWarnings("JPQLValidation")
        TypedQuery<AppUserRole> query = em.createQuery("SELECT t FROM AppUserRole t", AppUserRole.class);
        List<AppUserRole> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record;
    }

    @Override
    public AppUserRole createAppUserRole(AppUserRole oAppUserRole) {
        em.persist(oAppUserRole);
        em.flush();
        return oAppUserRole;
    }

    @Override
    public AppUserRoleMap createAppUserRoleMap(AppUserRoleMap oAppUserRoleMap) {
        em.persist(oAppUserRoleMap);
        em.flush();
        return oAppUserRoleMap;
    }

    @Override
    public AppUserActivity createUserActivity( AppUserActivity userActivity) {
        em.persist(userActivity);
        em.flush();
        return userActivity;
    }


    @Override
    public boolean userExistsById(long appUserId) {
        Long count = em.createQuery("SELECT COUNT(a) FROM AppUser a WHERE a.id = :appUserId", Long.class)
                .setParameter("appUserId", appUserId)
                .getSingleResult();
        return count > 0;
    }

}
