/*
 * To change this license authorization, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.jwt;

import com.google.gson.Gson;
import static com.chh.trustfort.gateway.component.ApiPath.HEADER_STRING;
import static com.chh.trustfort.gateway.component.ApiPath.TOKEN_PREFIX;
import com.chh.trustfort.gateway.payload.TokenData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Daniel Ofoleta
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException {
        String authorization = httpRequest.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        logger.error("Remote IP: " + httpRequest.getRemoteHost());
        if (authorization != null && authorization.startsWith(TOKEN_PREFIX)) {
            authToken = authorization.replace(TOKEN_PREFIX, "");
            try {

                TokenData oTokenData = new TokenData();
                try {
                    String[] pieces = authToken.split("\\.");
                    String b64payload = pieces[1];
                    String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
                    oTokenData = gson.fromJson(jsonString, TokenData.class);
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                }

                username = oTokenData.getSub();
            } catch (IllegalArgumentException e) {
                logger.error("An error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("Token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid. ");
            }
        } else {
            logger.warn("Couldn't find bearer string, will ignore the header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            System.out.println(httpRequest.getContextPath());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            if (jwtTokenUtil.validateToken(authToken, userDetails,username,)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                logger.info("Authenticated user " + username + ", setting security context ");
                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
        }

        try {
            chain.doFilter(httpRequest, httpResponse);
        } catch (IOException | ServletException  exception) {
            logger.warn(exception.getMessage());
        }
    }

}
