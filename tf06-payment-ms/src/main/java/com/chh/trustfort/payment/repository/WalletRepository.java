package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.component.WalletUtil;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class WalletRepository {
    private static final Logger log = LoggerFactory.getLogger(WalletRepository.class);

    @Autowired
    private WalletUtil walletUtil;

    @PersistenceContext
    private EntityManager em;

    public synchronized String generateWalletId() {
        log.info("🚀 Generating wallet ID using DB sequence...");
        try {
            // Fetch the next serial number from the database sequence
            BigInteger serialNumberBI = (BigInteger) em.createNativeQuery("SELECT NEXTVAL('wallet_serial_seq')").getSingleResult();
            long serialNumber = serialNumberBI.longValue();

            // Validate serial number
            if (serialNumber <= 0) {
                throw new WalletException("❌ Invalid serial number retrieved from sequence.");
            }

            // Optionally add a check digit here if needed
            String walletId = "WAL-" + serialNumber;

            log.info("✅ Generated Wallet ID: {}", walletId);
            return walletId;
        } catch (Exception e) {
            log.error("❌ Error generating wallet ID using sequence", e);
            throw new WalletException("Wallet ID generation failed due to an internal error.");
        }
    }


    public List<Wallet> findByUserPhone(String phoneNumber) {
        try {
            return em.createQuery(
                            "SELECT w FROM Wallet w WHERE w.users.phoneNumber = :phone", Wallet.class)
                    .setParameter("phone", phoneNumber)
                    .getResultList();
        } catch (Exception e) {
            log.error("❌ Failed to retrieve wallets by user.phoneNumber: {}", phoneNumber, e);
            return Collections.emptyList();
        }
    }


    public List<Wallet> findByUserId(String userId) {
        try {
            return em.createQuery(
                            "SELECT w FROM Wallet w WHERE w.userId = :userId", Wallet.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            log.error("❌ Failed to retrieve wallets for userId: {}", userId, e);
            return Collections.emptyList();
        }
    }

    public Optional<String> findWalletIdByUserId(String userId) {
        try {
            return Optional.ofNullable(em.createQuery(
                            "SELECT w.walletId FROM Wallet w WHERE w.userId = :userId", String.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getSingleResult());
        } catch (Exception e) {
            log.warn("⚠️ Wallet not found for userId: {}", userId);
            return Optional.empty();
        }
    }




    public Wallet createWallet(Wallet wallet) {
        log.info("Creating wallet for userId: {}", wallet.getUserId());
        em.persist(wallet);
        em.flush();
        log.info("Wallet created with ID: {}", wallet.getWalletId());
        return wallet;
    }

    public Wallet updateUser(Wallet wallet) {
        log.info("Updating wallet with ID: {}", wallet.getWalletId());
        em.merge(wallet);
        em.flush();
        log.info("Wallet updated successfully.");
        return wallet;
    }

    public boolean existsByOwner(Users users) {
        Long count = em.createQuery("SELECT COUNT(w) FROM Wallet w WHERE w.users = :users", Long.class)
                .setParameter("users", users)
                .getSingleResult();
        return count > 0;
    }
    public Optional<Wallet> findByWalletId(String walletId) {
        try {
            return Optional.ofNullable(
                    em.createQuery("SELECT w FROM Wallet w WHERE w.walletId = :walletId", Wallet.class)
                            .setParameter("walletId", walletId)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public Optional<Wallet> findByAccountNumber(String accountNumber) {
        try {
            Wallet wallet = em.createQuery("SELECT w FROM Wallet w WHERE w.accountNumber = :accountNumber", Wallet.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            return Optional.of(wallet);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Wallet> findByEmailAddress(String emailAddress) {
        try {
            Wallet wallet = em.createQuery("SELECT w FROM Wallet w WHERE w.users.email = :emailAddress", Wallet.class)
                    .setParameter("emailAddress", emailAddress)
                    .getSingleResult();
            return Optional.of(wallet);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUserId(String userId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(w) FROM Wallet w WHERE w.userId = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("❌ Failed to check wallet existence for userId: {}", userId, e);
            return false;
        }
    }

}
