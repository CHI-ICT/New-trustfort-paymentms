package com.chh.trustfort.payment.payload;

import com.chh.trustfort.payment.constant.Channel;
import lombok.Data;

@Data
public class OtpRequestPayload {
    private Long userId;
    private String username;
    private Channel channel;
}
