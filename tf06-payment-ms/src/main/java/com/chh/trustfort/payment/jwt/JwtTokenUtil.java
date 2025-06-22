package com.chh.trustfort.payment.jwt;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil implements Serializable {

    @Autowired
    private ApplicationContext context;

    public String getUsernameFromToken(String token, String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getChannelFromToken(String token, String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("Channel");
    }

    public String getIPFromToken(String token, String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("IP");
    }

    public String getEncryptionKeyFromToken(String token, String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return (String) claims.get("auth");
    }

    public boolean userHasRole(String token, String role , String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        String roles = (String) claims.get("roles").toString();
        return roles.contains(role);
    }



    public boolean isTokenExpired(String token, String signingKey) {
        StringEncryptor oStringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
        signingKey = oStringEncryptor.decrypt(signingKey);
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        Date expiration = claims.getExpiration();
        return (new Date()).after(expiration);
    }


}
