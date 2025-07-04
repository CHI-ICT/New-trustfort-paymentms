package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Wallet;
import com.chh.trustfort.accounting.component.WalletUtil;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Daniel Ofoleta
 */
@Repository
public class WalletRepository  {
    
    @Autowired
    WalletUtil walletUtil;
    
    @PersistenceContext
    EntityManager em;

    public synchronized String generateWalletId() {
        // Retrieve the current serial number from the database
        Long serialNumber = (Long) em.createNativeQuery("SELECT serial_number FROM wallet_serial WHERE id = 1")
                                              .getSingleResult();

        // Increment the serial number
        String serial = String.valueOf(serialNumber);
        int checkDigit = walletUtil.calculateCheckDigit(serial);

        // Update the serial number in the database
        em.createNativeQuery("UPDATE wallet_serial SET serial_number = ? WHERE id = 1")
                     .setParameter(1, serialNumber + 1)
                     .executeUpdate();

        // Combine serial and check digit to form the wallet ID
        return serial + checkDigit;
    }

    public Wallet createWallet(Wallet wallet) {
        em.persist(wallet);
        em.flush();
        return wallet;
    }

    public Wallet updateUser(Wallet wallet) {
        em.merge(wallet);
        em.flush();
        return wallet;
    }

}
