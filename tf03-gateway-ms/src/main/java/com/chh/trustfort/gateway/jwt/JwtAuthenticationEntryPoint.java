/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.jwt;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel Ofoleta
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException authException) throws IOException {
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
        // Here you can place any message you want 

//        //Check the number of failed login attempts
//        HttpSession session = httpRequest.getSession();
//        String currentSession = (String) session.getAttribute("Session_ID");
//        if (currentSession == null) {
//            session.setAttribute("Session_ID", session.getId());
//            session.setAttribute("Failed_Count", 1);
//        } else {
//            int currentFailedCount = (Integer)session.getAttribute("Failed_Count");
//            currentFailedCount += 1;
//            session.setAttribute("Failed_Count", currentFailedCount);
//        }
//
//        //Check if the failed count is greater than 3
//        int failedCount = (Integer) session.getAttribute("Failed_Count");
//        if (failedCount > 3) {
//            jwtTokenUtil.lockUser(httpRequest.getRemoteHost());
//        }
        
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
