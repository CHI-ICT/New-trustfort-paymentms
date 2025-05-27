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
        log.info("Generating wallet ID...");
        try {
            // Retrieve the current serial number from the database.
            BigInteger serialNumberBI = (BigInteger) em.createNativeQuery(
                            "SELECT COALESCE(MAX(serial_number), 99999) FROM wallet_serial WHERE id = 1")
                    .getSingleResult();
            long serialNumber = serialNumberBI.longValue() + 1; // Increment by 1

            // Validate serial number retrieval
            if (serialNumber <= 0) {
                throw new WalletException("Failed to retrieve a valid serial number for wallet generation.");
            }

            // Construct the Wallet ID with "WAL-" prefix
            String walletId = "WAL-" + serialNumber;

//            // Convert serial number to String and calculate the check digit.
//            String serial = String.valueOf(serialNumber);
//            int checkDigit = walletUtil.calculateCheckDigit(serial);

            // Update the serial number in the database (cast parameter to bigint).
            int updatedRows = em.createNativeQuery(
                            "UPDATE wallet_serial SET serial_number = :newSerial WHERE id = 1")
                    .setParameter("newSerial", serialNumber )
                    .executeUpdate();


            if (updatedRows < 1) {
                throw new WalletException("Failed to update serial number in wallet_serial table.");
            }

//            String walletId = serial + checkDigit;
            log.info("Generated Wallet ID: {}", walletId);
            return walletId;
        } catch (Exception e) {
            log.error("Error generating wallet ID", e);
            throw new WalletException("Wallet ID generation failed due to an internal error.");
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
            Wallet wallet = em.createQuery("SELECT w FROM Wallet w WHERE w.users.emailAddress = :emailAddress", Wallet.class)
                    .setParameter("emailAddress", emailAddress)
                    .getSingleResult();
            return Optional.of(wallet);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUserId(String userId) {
        Long count = em.createQuery("SELECT COUNT(w) FROM Wallet w WHERE w.userId = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }




}
