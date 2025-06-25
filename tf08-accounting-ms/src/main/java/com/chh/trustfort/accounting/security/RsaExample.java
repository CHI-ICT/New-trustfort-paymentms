package com.chh.trustfort.accounting.security;

/**
 *
 * @author dofoleta
 */
//pimport javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaExample {

//    public static boolean verify(String data, String signature, String publicKeyStr) throws Exception {
//        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyFactory.generatePublic(spec);
//        
//
//
//        Signature sig = Signature.getInstance("SHA256withRSA");
//        sig.initVerify(publicKey);
//        sig.update(data.getBytes());
//
//        byte[] signatureBytes = Base64.getMimeDecoder().decode(signature.trim());
//        return sig.verify(signatureBytes);
//    }
//
//    public static void main(String[] args) throws Exception {
//        String data = "Hello, World!";
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4HyQ01jz/6+9M/HX9NMWD126924xVzQsHgS3tVQkxsAy+zKyY9lHbrkXhdCz8fEWvodu/KIsNKuAxjMSUgArQIIkq44g2Bxe3iR9sxBRmjWBlkjWvzUavoSxc9EV9UMLY4mUxWWZATGHIhasmQyLvPpqyKrCtoqXPONfuES31eRgMBk6r2+hGNOmGL852x3EIBBmlYVe1QEEW4rMxaEaaxDHlAv1sSMfmf89PCtUdLFDB7iCHlrz+CMFY38nwvRWoAECJXgh98wvt3zoQb17PZ8xpT3Wo+MuoTEj5gxyoVCUTx2d1vlk2rTpHfUUwwf/VC/U6EA44F3Gx0RjW2ZsdQIDAQAB";  // Signature generated from React Native
//        String signature = "MCJqEXSLyrux2ADicWIF/dWHAuRfokQnIXSNCGiHUxpfkJBDLGZogIPlZidcpydkhcYSNtKL5wUYzsKtFH4olGr9Obc/jY9gTAURePIC0u5RmjIcqsUnjY2ZGzWUjLDapTSDqdqAqLErOHuwmX5UVVHmYKARXTlyMTtG83D3faqz6v6p2BzyoidONGLofMzNHXidzmF+FOJrRnK0dbpCsg18e7G/e8tE8E83zcXpuvfj9bjiK4vmK1YC5NxTBnIk5jDXux+yGvMhN4sy20W0aspy00pOCIJVsw0JHKPfHLZweGuqiCP1G4RYl8eAvk0Beb2Q/LKvbuOi6vsQu96t9Ww==";  // Your public key
//
//        boolean isVerified = verify(data, signature, publicKey);
//        System.out.println("Signature Verified: " + isVerified);
//    }
//} 
    public static void main(String... argv) throws Exception {
        //First generate a public/private key pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        //The private key can be used to sign (not encrypt!) a message. The public key holder can then verify the message.
        String message = "09034883109";
        
        System.out.println(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
 
//        String strPriKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        String strPriKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbolgVDag1bQofadn3FJgd9Q7qnfVGgvpQasksLKqFKsSNoLaFHB5eNenxvq0LS8Un3VfqlIbT1z78DEJBy8FT2jVN9BHMt2vB3Nz6aWqIx2xJjgDThZ2E9LXA8DtXWnLfGWoRrjWiSVEkUGxAhwzykQjeBZ2wha+9mvQhscGRIgVatMGAgydrrgN7tIwNulf0vXjiaNauAgb9LcStHvhotU4E57yJPkdiKPcHCZ0lJILZj5oGCmuLHcKEKkcGYIIYn+vzD+hPrAOgt9iupfLV6L0l10k9Wt0WXCF7YeHjoi4w99Ge1d2Rq39F0/qIeen42ERGLzSl1H4pyUAnQNDlAgMBAAECggEALEIdhFcu54qLn2VNq7r6i0JeLJAg6R1kK6xCa0KEd3wzCweSAAaACFCSp8GOmjDA71Y9oyL7uJB0g97ElMtpvfvGvtVSnoHmremuGbQSrLTxcXVlfMF5iw7ABQ8MAQpislka1c5RyEXLQmVwc2U0ZGKkP+ce8c0Crgu7sz1Gi4vLLxEtptdN6uv/Bg1avQID5V6o10XW0tKLJ0wKGKd37bLU1vSXcUHA2f2fTe8HYt8E7qHw1yTIAvfPibdsHs43YRxqyXSHOOWrdiuLWABif/zzasnWBOEwcsX5SBY8Sg1nxmcJMhMt0ZcmOY37Hb6geGNTC59ee/f7htr+biJNQQKBgQDqs6c9+Gtay0vaPj7EBhhDXOdcrKN2wV6jRD8sLJg7YIkIxnY7XO83+Q068J7GZGm4Y793/KSJZ42pW+H0Boi6KazEW2igI1hBIRpY64JTsLtSrnR9nFOUl+8F2G4t+q/r2w6iultsq2WQsGztQ/ua/0T+1m9IVrtnOk1cMJwMVQKBgQCpwd/b2sVw3brpMBp3b18lSnsM8zzWHVPmIqD3rw54gAme4ABsZHWaLSTIYzKZTIC3nnlCXtB5NlCr8q9K2coK1mf7UpkANCwHrP/QrJoWugsR7UsTZ3UgW4DRiwfAmSFEOFbwtSNzn1ceTpOIrTUUC/iDomceHIrtfu/RwudCUQKBgBFv2XwEKc2sDu2e9EDSBxlyHIpJFTfsc+CDYIIiqgi3SlcJj50ncWpiKXZ4jANHUIdvebkrXvoKZH7xoZ7koWHbWMeZ1vMWzSbBk9iJXrxjA/fHmEYAyEDg7NQtWTo3RryqILM8TzCKp1gRk3YpN9czIegg/FGOiAvKTBcOUXmlAoGAHx/c24KRENCWpIqdT6ir2Tp+tTEjxl46M8fFm6QNEyrmmKqZaUZCCbKdO+B0NodAUmpL8U9RCehbgitM2Hx1/uawyfOq+c6XBBFEFg6PcftKbE8FpNkjMHuWctT65uUspEVwK1IK3fgpEPV3N2gKg1JujjQIlAzHrCPJ9Lu3iwECgYEAwkjRubuAqee91yPbShflyZP7j8lNqJHm9SnK6pJizaQ1Fa4RNR2uoMdQzdrMV9hS0KP1MCxgHFSgaTcFxmtjo8Sd+0Dupnr3UWY/t8yAqpbvYL93Mi3sk2F3XIvl4PL0v1NySAgFU5B2vn7QIn7OxqRVOSGqNj6MUUbR8B4dTy4=";
        byte[] privateKeyBytes = Base64.getDecoder().decode(strPriKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);
        
        
        //Let's sign our message
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();

        System.out.println(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));

        System.out.println(Base64.getEncoder().encodeToString(signature));
        
//        String strPubKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String strPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArswH8lQ1taYjdVvrewDg/n9JSqay3PLZuXelmd7mB7EBkC0axSbwlAy+GJeGXOsb5RU8m8ZtSsw63kqTOp2ueRvzIcYVjHbq1mSyYkCWt/6TD5HR8buxcR+r3Exc+SXyei9eUdjLQtgFjHt1GWRMxBxiGihVLrujQxXiVFMj6e7VGFUYqR6FcGIButQq2jasuooaAMrJdskOwan/T7UyqloagvlWufAo+CekJDaqw1lmO1aRQOjGfcKhgu1ITWerxaerYL3CN8tnUkkJahygX0w1KoUnSSkjNHdNyglYRPX427wZCXxxHVFfHMC+AEJe8sx4T8Y1kAzX2PFj/gjAKQIDAQAB";

        byte[] publicKeyBytes = Base64.getDecoder().decode(strPubKey);
        X509EncodedKeySpec pspec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory pkeyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = pkeyFactory.generatePublic(pspec);
        
//        String strSignature = Base64.getEncoder().encodeToString(signature);
        String strSignature = "BnTEYqq7wfFoEdpRO85mjYW2uU3YgGcHwlq54UooQGNXE0u0PJHch3Cvss34AquUlQ+0Pwx/u0ObIq42jIhOzTXVCJcQTqwITshhCgB98njq0gM4SZWfdP0A7+fdK0LkUjFreJREclo8RSKTX80UuVSCWdd71sMyGzdW4cYGfoLU0OR9rTuZN4FqAGV2YGlBfhFNux8UIjnHr8+IIiC4pkOds/NRJgLezNGK4WxZWOPEpdoMgCZpSq+OL9Yqrji0w8oOU5JLYsv28+ycqTgsmU46VfQ5aggz27u63lJbSELrpCX9/GPOrHg3I3kZTSRL8YF6YbI+tJlyJbLfbwYuSg==";

        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(message.getBytes(StandardCharsets.UTF_8));
        
        byte[] signatureBytes = Base64.getMimeDecoder().decode(strSignature);

        
        
        boolean isCorrect = publicSignature.verify(signatureBytes);

        System.out.println("Signature correct: " + isCorrect);

        //Let's check the signature
//        Signature publicSignature = Signature.getInstance("SHA256withRSA");
//        publicSignature.initVerify(pair.getPublic());
//        publicSignature.update(message.getBytes(StandardCharsets.UTF_8));
//        boolean isCorrect = publicSignature.verify(signature);
//
//        System.out.println("Signature correct: " + isCorrect);
    }
}
