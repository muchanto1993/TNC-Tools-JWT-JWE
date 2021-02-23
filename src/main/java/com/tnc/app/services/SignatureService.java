package com.tnc.app.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class SignatureService {

    public static String getSignature(String apiId, String apiKey, String transactionType, String requestTimeStamp,
            String httpMethod, String nonce, String requestBody) {

        String requestBodyBase64 = null;
        String signatureRaw = null;
        String signature = null;

        System.out.println("apiId : " + apiId);
        System.out.println("apiKey : " + apiKey);

        requestBodyBase64 = Base64.encodeBase64String(requestBody.getBytes());
        System.out.println("requestBodyBase64 : " + requestBodyBase64);

        signatureRaw = apiId + transactionType + requestTimeStamp + httpMethod + nonce + requestBodyBase64;
        System.out.println("signatureRaw : " + signatureRaw);

        signature = getBase64HmacSha256(signatureRaw, apiKey);
        System.out.println("signature : " + signature);

        requestBodyBase64 = null;
        signatureRaw = null;
        return signature;
    }

    public static String getBase64HmacSha256(String rawData, String apiKey) {
        String result = null;
        Mac sha256_HMAC;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            result = Base64.encodeBase64String(sha256_HMAC.doFinal(rawData.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

}
