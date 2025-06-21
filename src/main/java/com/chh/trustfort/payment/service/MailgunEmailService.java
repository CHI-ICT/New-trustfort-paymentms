package com.chh.trustfort.payment.service;//package com.chh.trustfort.payment.service;
//
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MailgunEmailService {
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//    public void sendEmail(String to, String subject, String text) {
//        try {
//            HttpResponse<String> response = Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
//                    .basicAuth("api", apiKey)
//                    .field("from", "Trustfort Wallet <no-reply@" + domain + ">")
//                    .field("to", to)
//                    .field("subject", subject)
//                    .field("text", text)
//                    .asString();
//
//            System.out.println("Mailgun response: " + response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
