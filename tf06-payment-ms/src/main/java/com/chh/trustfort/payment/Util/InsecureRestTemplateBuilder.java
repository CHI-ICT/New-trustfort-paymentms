package com.chh.trustfort.payment.Util;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class InsecureRestTemplateBuilder {

    public static RestTemplate build() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            return new RestTemplate(factory);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create insecure RestTemplate", e);
        }
    }
}
