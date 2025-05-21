/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.jwt;


import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Users;
import com.chh.trustfort.accounting.repository.UsersRepository;
import com.chh.trustfort.accounting.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.chh.trustfort.accounting.repository.AppUserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Daniel Ofoleta
 */
@Component
public class JwtTokenUtil implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private StringEncryptor stringEncryptor; // Injected encryptor

    @Value("${jwt.issuer}")
    private String issuer;

    // Although we're not using jwtSecret for signing anymore, we keep it for fallback if needed.
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Generates a JWT token for a user.
     */
    public String doGenerateToken(String subject, String remoteIP) {
        Users users = usersRepository.getUserByUserName(subject);
        if (users == null) {
            log.error("User not found: {}", subject);
            return null;
        }
        log.info("Generating token for user: {}", users.getUserName());

        try {
            // Decrypt the stored encryption key for signing (this key is used for JWT)
            String encryptKey = stringEncryptor.decrypt(users.getEncryptionKey());
            log.info("Using Encryption Key for Signing: {}", encryptKey);

            Claims claims = Jwts.claims().setSubject(subject);

            claims.put("auth", encryptKey);
            claims.put("IP", remoteIP);
            claims.put("uniqueId", UUID.randomUUID().toString());
            claims.put("issuedAt", System.currentTimeMillis());


            // Fetch roles dynamically from DB
            List<String> roles = userRepository.getAppUserRoleNameByGroup(users.getUserGroup());

// Ensure at least one role is present
            if (roles == null || roles.isEmpty()) {
                roles = new ArrayList<>();
            }
            // ✅ Assign commission roles correctly


// ✅ Ensure both `C_WAL` and `W_BAL` exist
            if (!roles.contains(Role.CREATE_WALLET.getValue())) {
                roles.add(Role.CREATE_WALLET.getValue());
            }
            if (!roles.contains(Role.CHECK_BALANCE.getValue())) {
                roles.add(Role.CHECK_BALANCE.getValue());
            }
            if (!roles.contains(Role.FUND_WALLET.getValue())) {
                roles.add(Role.FUND_WALLET.getValue());
            }
            if (!roles.contains(Role.FETCH_WALLET.getValue())) {
                roles.add(Role.FETCH_WALLET.getValue());
            }
            if (!roles.contains(Role.TRANSFER_FUNDS.getValue())) {
                roles.add(Role.TRANSFER_FUNDS.getValue());
            }
            if (!roles.contains(Role.TRANSACTION_HISTORY.getValue())) {
                roles.add(Role.TRANSACTION_HISTORY.getValue());
            }
            if (!roles.contains(Role.WITHDRAW_FUNDS.getValue())) {
                roles.add(Role.WITHDRAW_FUNDS.getValue());
            }
            if (!roles.contains(Role.CREDIT_COMMISSION.getValue())) {
                roles.add(Role.CREDIT_COMMISSION.getValue());
            }
            if (!roles.contains(Role.VIEW_COMMISSION.getValue())) {
                roles.add(Role.VIEW_COMMISSION.getValue());
            }
            if (!roles.contains(Role.FREEZE_WALLET.getValue())) {
                roles.add(Role.FREEZE_WALLET.getValue());
            }
            if (!roles.contains(Role.UNFREEZE_WALLET.getValue())) {
                roles.add(Role.UNFREEZE_WALLET.getValue());
            }
            if (!roles.contains(Role.CLOSE_WALLET.getValue())) {
                roles.add(Role.CLOSE_WALLET.getValue());
            }
            if (!roles.contains(Role.LOCK_FUNDS.getValue())) {
                roles.add(Role.LOCK_FUNDS.getValue());
            }
            if (!roles.contains(Role.UNLOCK_FUNDS.getValue())) {
                roles.add(Role.UNLOCK_FUNDS.getValue());
            }
            if (!roles.contains(Role.GENERATE_PAYMENT_REFERENCE.getValue())) {
                roles.add(Role.GENERATE_PAYMENT_REFERENCE.getValue());
            }
            if (!roles.contains(Role.GENERATE_OTP.getValue())) {
                roles.add(Role.GENERATE_OTP.getValue());
            }
            if (!roles.contains(Role.MOCK_FCMB_BASE.getValue())) {
                roles.add(Role.MOCK_FCMB_BASE.getValue());
            }
            if (!roles.contains(Role.SIMULATE_TRANSFER_STATUS.getValue())) {
                roles.add(Role.SIMULATE_TRANSFER_STATUS.getValue());
            }
            if (!roles.contains(Role.FUND_WEBHOOK.getValue())) {
                roles.add(Role.FUND_WEBHOOK.getValue());
            }
            if (!roles.contains(Role.SETUP_PIN.getValue())) {
                roles.add(Role.SETUP_PIN.getValue());
            }
            if (!roles.contains(Role.VALIDATE_PIN.getValue())) {
                roles.add(Role.VALIDATE_PIN.getValue());
            }
            if (!roles.contains(Role.HANDLE_WEBHOOK.getValue())) {
                roles.add(Role.HANDLE_WEBHOOK.getValue());
            }
            if (!roles.contains(Role.INITIATE_CARD_PAYMENT.getValue())) {
                roles.add(Role.INITIATE_CARD_PAYMENT.getValue());
            }
            if (!roles.contains(Role.HANDLE_FCMB_WEBHOOK.getValue())) {
                roles.add(Role.HANDLE_FCMB_WEBHOOK.getValue());
            }
            if (!roles.contains(Role.GENERATE_ACCOUNT.getValue())) {
                roles.add(Role.GENERATE_ACCOUNT.getValue());
            }
            if (!roles.contains(Role.CONFIRM_TRANSFER.getValue())) {
                roles.add(Role.CONFIRM_TRANSFER.getValue());
            }
            if (!roles.contains(Role.CONFIRM_BANK_TRANSFER.getValue())) {
                roles.add(Role.CONFIRM_BANK_TRANSFER.getValue());
            }
            if (!roles.contains(Role.VERIFY_FLW_TRANSACTION.getValue())) {
                roles.add(Role.VERIFY_FLW_TRANSACTION.getValue());
            }
            if (!roles.contains(Role.JOURNAL_ENTRY.getValue())) {
                roles.add(Role.JOURNAL_ENTRY.getValue());
            }
            if (!roles.contains(Role.CREATE_CHART_OF_ACCOUNT.getValue())) {
                roles.add(Role.CREATE_CHART_OF_ACCOUNT.getValue());
            }
            if (!roles.contains(Role.CASH_FLOW_STATEMENT.getValue())) {
                roles.add(Role.CASH_FLOW_STATEMENT.getValue());
            }
            if (!roles.contains(Role.EQUITY_STATEMENT.getValue())) {
                roles.add(Role.EQUITY_STATEMENT.getValue());
            }
            if (!roles.contains(Role.GENERATE_BALANCE_SHEET.getValue())) {
                roles.add(Role.GENERATE_BALANCE_SHEET.getValue());
            }
            if (!roles.contains(Role.DOUBLE_ENTRY.getValue())) {
                roles.add(Role.DOUBLE_ENTRY.getValue());
            }
            if (!roles.contains(Role.GET_INCOME_STATEMENT.getValue())) {
                roles.add(Role.GET_INCOME_STATEMENT.getValue());
            }
            if (!roles.contains(Role.VALIDATE_STATEMENT_INTEGRITY.getValue())) {
                roles.add(Role.VALIDATE_STATEMENT_INTEGRITY.getValue());
            }
            if (!roles.contains(Role.EXPORT_INCOME_STATEMENT.getValue())) {
                roles.add(Role.EXPORT_INCOME_STATEMENT.getValue());
            }
            if (!roles.contains(Role.EXPORT_BALANCE_SHEET.getValue())) {
                roles.add(Role.EXPORT_BALANCE_SHEET.getValue());
            }
            if (!roles.contains(Role.EXPORT_CASH_FLOW.getValue())) {
                roles.add(Role.EXPORT_CASH_FLOW.getValue());
            }
            if (!roles.contains(Role.EXPORT_EQUITY_STATEMENT.getValue())) {
                roles.add(Role.EXPORT_EQUITY_STATEMENT.getValue());
            }
            if (!roles.contains(Role.EXPORT_ALL_STATEMENTS.getValue())) {
                roles.add(Role.EXPORT_ALL_STATEMENTS.getValue());
            }
            if (!roles.contains(Role.RECONCILE_TAX.getValue())) {
                roles.add(Role.RECONCILE_TAX.getValue());
            }
            if (!roles.contains(Role.TAX_FINANCE.getValue())) {
                roles.add(Role.TAX_FINANCE.getValue());
            }
            if (!roles.contains(Role.FILING_REPORT.getValue())) {
                roles.add(Role.FILING_REPORT.getValue());
            }
            if (!roles.contains(Role.RECONCILIATION.getValue())) {
                roles.add(Role.RECONCILIATION.getValue());
            }
            if (!roles.contains(Role.EXPORT_FILING_REPORT.getValue())) {
                roles.add(Role.EXPORT_FILING_REPORT.getValue());
            }
            if (!roles.contains(Role.ALERTS.getValue())) {
                roles.add(Role.ALERTS.getValue());
            }
            if (!roles.contains(Role.SYNC_BANK_INFLOW.getValue())) {
                roles.add(Role.SYNC_BANK_INFLOW.getValue());
            }
            if (!roles.contains(Role.GENERATE_RECEIPT.getValue())) {
                roles.add(Role.GENERATE_RECEIPT.getValue());
            }
            if (!roles.contains(Role.ALERT_PENDING_RECEIPTS.getValue())) {
                roles.add(Role.ALERT_PENDING_RECEIPTS.getValue());
            }
            if (!roles.contains(Role.COA_BASE.getValue())) {
                roles.add(Role.COA_BASE.getValue());
            }
            if (!roles.contains(Role.CREATE.getValue())) {
                roles.add(Role.CREATE.getValue());
            }
            if (!roles.contains(Role.GET_ALL.getValue())) {
                roles.add(Role.GET_ALL.getValue());
            }




            claims.put("roles", roles);



            Date issuedAt = new Date();
            Date expirationDate = Date.from(Instant.now().plus(12, ChronoUnit.HOURS));
            log.info("Token issued at: {}", issuedAt);
            log.info("Token expires at: {}", expirationDate);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(encryptKey.getBytes()))
                    .compact();
        } catch (Exception e) {
            log.error("Error while generating JWT token", e);
            return null;
        }
    }


    public String getUserNameFromToken(String token, String encryptionKey) {
        return getClaimsFromToken(token, encryptionKey).getSubject();
    }

    public boolean isTokenExpired(String token, String encryptionKey) {
        try {
            Claims claims = getClaimsFromToken(token, encryptionKey);
            Date expirationDate = claims.getExpiration();
            Date currentTime = new Date();
            log.info("Token expiration time: {}", expirationDate);
            log.info("Current server time: {}", currentTime);
            return currentTime.after(expirationDate);
        } catch (ExpiredJwtException e) {
            log.error("Token has expired", e);
            return true;
        } catch (Exception e) {
            log.error("Error validating token", e);
            return true;
        }
    }

    public boolean userHasRole(String token, String requiredRole, String encryptionKey) {
        try {
            Claims claims = getClaimsFromToken(token, encryptionKey);
            List<String> roles = claims.get("roles", List.class);
            log.info("Roles in token: {}", roles);
            log.info("Required role: {}", requiredRole);
            return roles != null && roles.contains(requiredRole);
        } catch (Exception e) {
            log.error("Error checking user role from token", e);
        }
        return false;
    }

    public String getIPFromToken(String token, String encryptionKey) {
        try {
            Claims claims = getClaimsFromToken(token, encryptionKey);
            return claims.get("IP", String.class);
        } catch (Exception e) {
            log.error("Error extracting IP from token", e);
            return null;
        }
    }

    public String getChannelFromToken(String token, String encryptionKey) {
        try {
            Claims claims = getClaimsFromToken(token, encryptionKey);
            return claims.get("Channel", String.class);
        } catch (Exception e) {
            log.error("Error extracting Channel from token", e);
            return null;
        }
    }

    private Claims getClaimsFromToken(String token, String encryptionKey) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(encryptionKey.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Token Claims: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Error extracting claims from token", e);
            throw new RuntimeException("Invalid token");
        }
    }
}

//    @Autowired
//    AppUserRepository userRepository;
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Value("${issuer}")
//    private String issuer;
//
//
//    public String doGenerateToken(String subject, String remoteIP) {
//
//        StringEncryptor oStringEncryptor = (StringEncryptor)context.getBean("jasyptStringEncryptor");
//
//        AppUser appuser = userRepository.getAppUserByUserName(subject);
//        Claims claims = Jwts.claims().setSubject(subject);
//        String encryptKey = oStringEncryptor.decrypt(appuser.getEncryptionKey());
//        claims.put("roles", userRepository.getAppUserRoleNameByGroup(appuser.getAppUserGroup()));
//        //Add the user encryption key
//        claims.put("auth", encryptKey);
//        //Get the channel information
//        claims.put("Channel", appuser.getChannel());
//        claims.put("IP", "xxxxxx");
//
//        Date currentDate = new Date();
//        LocalDateTime localDateTime = LocalDateTime.now();
//        LocalDateTime localDateTimePlusOneHour = localDateTime.plusHours(2);
//        Date currentDatePlusOneHour = Date.from(localDateTimePlusOneHour.atZone(ZoneId.systemDefault()).toInstant());
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuer(issuer)
//                .setIssuedAt(currentDate)
//                .setExpiration(currentDatePlusOneHour)
//                .signWith(SignatureAlgorithm.HS256, encryptKey)
//                .compact();
//    }
//
//    public String doGenerateToken(String subject, String remoteIP, String signingKey) {
//
//        StringEncryptor oStringEncryptor = context.getBean(StringEncryptor.class);
//
//        AppUser appuser = userRepository.getAppUserByUserName(subject);
//        Claims claims = Jwts.claims().setSubject(subject);
//        String encryptKey = oStringEncryptor.decrypt(appuser.getEncryptionKey());
//        claims.put("roles", userRepository.getAppUserRoleNameByGroup(appuser.getAppUserGroup()));
//        //Add the user encryption key
//        claims.put("auth", encryptKey);
//        //Get the channel information
//        claims.put("Channel", appuser.getChannel());
//        claims.put("IP", "xxxxxx");
//
//        Date currentDate = new Date();
//        LocalDateTime localDateTime = LocalDateTime.now();
//        LocalDateTime localDateTimePlusOneHour = localDateTime.plusHours(2);
//        Date currentDatePlusOneHour = Date.from(localDateTimePlusOneHour.atZone(ZoneId.systemDefault()).toInstant());
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuer(issuer)
//                .setIssuedAt(currentDate)
//                .setExpiration(currentDatePlusOneHour)
//                .signWith(SignatureAlgorithm.HS256, signingKey)
//                .compact();
//    }
//
//    public String getUserNameFromToken(String token, String signingKey) {
//        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//        return claims.getSubject();
//    }
//
//    public boolean validateToken(String token, UserDetails userDetails, String username, String signingKey) throws UnsupportedEncodingException {
//        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token, signingKey));
//    }
//
//    public boolean isTokenExpired(String token, String signingKey) {
//        try {
//            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//            Date expiration = claims.getExpiration();
//            return (new Date()).after(expiration);
//        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException expiredJwtException) {
//        }
//        return true;
//    }
//
//    public String getChannelFromToken(String token, String signingKey) {
//        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//        return (String) claims.get("Channel");
//    }
//
//    public String getIPFromToken(String token, String signingKey) {
//        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//        return (String) claims.get("IP");
//    }
//
//    public String getEncryptionKeyFromToken(String token, String signingKey) {
//        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//        return (String) claims.get("auth");
//    }
//
//    public boolean userHasRole(String token, String role, String signingKey) {
//        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//        ArrayList roles = (ArrayList) claims.get("roles");
//        return roles.contains(role);
//    }
//
//}

