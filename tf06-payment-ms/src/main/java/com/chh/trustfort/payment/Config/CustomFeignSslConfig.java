package com.chh.trustfort.payment.Config;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class CustomFeignSslConfig {

    @Bean
    public Client feignClient() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String s) {}
                    public void checkServerTrusted(X509Certificate[] xcs, String s) {}
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        return new Client.Default(sslContext.getSocketFactory(), (hostname, session) -> true);
    }
}
