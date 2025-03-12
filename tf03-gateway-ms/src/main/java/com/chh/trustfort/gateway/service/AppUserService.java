/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.service;

import com.chh.trustfort.gateway.payload.AppUserRequestPayload;
import com.chh.trustfort.gateway.payload.AppUserStatusUpdatePayload;
import com.chh.trustfort.gateway.payload.AppUserUpdatePayload;
import com.chh.trustfort.gateway.payload.UsernamePayload;

/**
 *
 * @author Daniel Ofoleta
 */
public interface AppUserService {


    String createChannelUser(String token, AppUserRequestPayload requestPayload);

    String updateChannelUserStatus(String token, AppUserStatusUpdatePayload requestPayload);

    String updateChannelUser(String token, AppUserUpdatePayload requestPayload);

    String deleteChannelUser(String token, UsernamePayload requestPayload);

    String getChannelUserList();







}
