package com.chh.trustfort.payment.Util;

import com.chh.trustfort.payment.Responses.ApiResponse;
import com.chh.trustfort.payment.constant.ResponseCode;
import com.chh.trustfort.payment.enums.ResponseStatus;
import com.google.gson.Gson;

public class SecureResponseUtil {

    private static final Gson gson = new Gson();

    public static <T> String success(String message, T data) {
        return gson.toJson(ApiResponse.success(
                message, data,
                ResponseCode.SUCCESS.getResponseCode(),
                ResponseStatus.SUCCESS.getValue()
        ));
    }

    public static String error(String message, String responseCode, String status) {
        return gson.toJson(ApiResponse.failure(
                message,
                responseCode,
                status,
                null
        ));
    }

    public static String errorWithDetails(String message, String responseCode, String status, Object error) {
        return gson.toJson(ApiResponse.failure(
                message,
                responseCode,
                status,
                error
        ));
    }

    public static <T> String partialSuccess(String message, T successData, Object errorDetails) {
        return gson.toJson(new ApiResponse<>(
                message,
                successData,
                true,
                ResponseCode.SUCCESS.getResponseCode(),
                ResponseStatus.SUCCESS.getValue(),
                errorDetails
        ));
    }
}
