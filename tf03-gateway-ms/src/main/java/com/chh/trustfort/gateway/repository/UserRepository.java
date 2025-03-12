/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.repository;

import com.chh.trustfort.gateway.model.Users;
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
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public Users findByUserName(String userName) {
        TypedQuery<Users> query = em.createQuery("SELECT t FROM Users t WHERE t.userName = :userName", Users.class)
                .setParameter("userName", userName);
        List<Users> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }

    public Users updateUser(Users user) {
        em.merge(user);
        em.flush();
        return user;
    }

}
