package com.chh.trustfort.payment.jwt;

import com.chh.trustfort.payment.component.Role; // Use wallet role enum from here
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.AppUserRepository;
import io.jsonwebtoken.*;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JwtTokenUtil implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private AppUserRepository userRepository;

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
        AppUser appUser = userRepository.getAppUserByUserName(subject);
        if (appUser == null) {
            log.error("User not found: {}", subject);
            return null;
        }
        log.info("Generating token for user: {}", appUser.getUserName());

        try {
            // Decrypt the stored encryption key for signing (this key is used for JWT)
            String encryptKey = stringEncryptor.decrypt(appUser.getEncryptionKey());
            log.info("Using Encryption Key for Signing: {}", encryptKey);

            Claims claims = Jwts.claims().setSubject(subject);
            // Retrieve roles from DB and ensure the wallet role is present
            List<String> roles = userRepository.getAppUserRoleNameByGroup(appUser.getAppUserGroup());
            if (roles == null) {
                roles = new ArrayList<>();
            }
            if (!roles.contains(Role.CFREATE_WALLET.getValue())) {
                roles.add(Role.CFREATE_WALLET.getValue());
            }
            claims.put("roles", roles);
            claims.put("auth", encryptKey);
            claims.put("Channel", appUser.getChannel());
            claims.put("IP", remoteIP);
            claims.put("uniqueId", UUID.randomUUID().toString());
            claims.put("issuedAt", System.currentTimeMillis());

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
