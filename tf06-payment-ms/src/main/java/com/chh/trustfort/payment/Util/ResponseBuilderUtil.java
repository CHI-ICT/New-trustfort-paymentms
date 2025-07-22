package com.chh.trustfort.payment.Util;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ResponseBuilderUtil {

    private static final Gson gson = new Gson();
    private static AesService aesService;


    private String buildEncryptedResponse(String responseCode, String responseMessage, Object data, AppUser appUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", responseCode);
        response.put("responseMessage", responseMessage);
        if (data != null) {
            response.put("data", data);
        }
        return aesService.encrypt(gson.toJson(response), appUser);
    }


}
