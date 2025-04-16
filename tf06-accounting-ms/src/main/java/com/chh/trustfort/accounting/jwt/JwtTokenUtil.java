/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.jwt;

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

    @Autowired
    AppUserRepository userRepository;
    
    @Autowired
    private ApplicationContext context;
    
    @Value("${issuer}")
    private String issuer;
    
    
    public String doGenerateToken(String subject, String remoteIP) {

        StringEncryptor oStringEncryptor = (StringEncryptor)context.getBean("jasyptStringEncryptor");

        AppUser appuser = userRepository.getAppUserByUserName(subject);
        Claims claims = Jwts.claims().setSubject(subject);
        String encryptKey = oStringEncryptor.decrypt(appuser.getEncryptionKey());
        claims.put("roles", userRepository.getAppUserRoleNameByGroup(appuser.getAppUserGroup()));
        //Add the user encryption key
        claims.put("auth", encryptKey);
        //Get the channel information 
        claims.put("Channel", appuser.getChannel());
        claims.put("IP", "xxxxxx");

        Date currentDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTimePlusOneHour = localDateTime.plusHours(2);
        Date currentDatePlusOneHour = Date.from(localDateTimePlusOneHour.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(currentDate)
                .setExpiration(currentDatePlusOneHour)
                .signWith(SignatureAlgorithm.HS256, encryptKey)
                .compact();
    }

    public String doGenerateToken(String subject, String remoteIP, String signingKey) {

        StringEncryptor oStringEncryptor = context.getBean(StringEncryptor.class);

        AppUser appuser = userRepository.getAppUserByUserName(subject);
        Claims claims = Jwts.claims().setSubject(subject);
        String encryptKey = oStringEncryptor.decrypt(appuser.getEncryptionKey());
        claims.put("roles", userRepository.getAppUserRoleNameByGroup(appuser.getAppUserGroup()));
        //Add the user encryption key
        claims.put("auth", encryptKey);
        //Get the channel information 
        claims.put("Channel", appuser.getChannel());
        claims.put("IP", "xxxxxx");

        Date currentDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTimePlusOneHour = localDateTime.plusHours(2);
        Date currentDatePlusOneHour = Date.from(localDateTimePlusOneHour.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(currentDate)
                .setExpiration(currentDatePlusOneHour)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }

    public String getUserNameFromToken(String token, String signingKey) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails, String username, String signingKey) throws UnsupportedEncodingException {
        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token, signingKey));
    }

    public boolean isTokenExpired(String token, String signingKey) {
        try {
            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();
            return (new Date()).after(expiration);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException expiredJwtException) {
        }
        return true;
    }

    public String getChannelFromToken(String token, String signingKey) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("Channel");
    }

    public String getIPFromToken(String token, String signingKey) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("IP");
    }

    public String getEncryptionKeyFromToken(String token, String signingKey) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("auth");
    }

    public boolean userHasRole(String token, String role, String signingKey) {
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        ArrayList roles = (ArrayList) claims.get("roles");
        return roles.contains(role);
    }

}
