package com.chh.trustfort.accounting.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ApiResponse {
//    private String status;
//    private String message;
//    private Object data;
//
//    public ApiResponse(String s) {
//    }
//}
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private String message;
    private Object data;
    private static final Gson gson = new Gson();

    // Optional: constructor for simple response with only message
    public ApiResponse(String message) {
        this.status = "error";
        this.message = message;
        this.data = null;
    }

    // ✅ Static method for error responses
    public static ApiResponse error(String message) {
        return ApiResponse.builder()
                .status("error")
                .message(message)
                .data(null)
                .build();
    }

    // ✅ Static method for success responses (optional)
    public static String success(String message, Object data) {
        ApiResponse response = ApiResponse.builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
        return gson.toJson(response); // ✅ Convert to String
    }
}
