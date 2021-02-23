package com.tnc.app;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.app.entity.common.AuthClaims;
import com.tnc.app.entity.common.Response;
import com.tnc.app.model.MitraIncomingMsg;
import com.tnc.app.model.features.checkstatus.InMsg;
import com.tnc.app.services.JweService;
import com.tnc.app.services.SignatureService;
import com.tnc.app.support.JsonUtils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!ß
 *
 */
public class AppCheckStatus {
    public static void main(String[] args) {
        String apiId = "834br98bakDfjkdf092QnfasHdf09432";
        String apiKey = "APIKEYvm20rj3nfd9q2vl329fb489g3298dh2bd72bsd";
        String jwtKey = "JWTKEY0jb3498hfn398fhSnkJksmwfsSs421CSid9822";
        String jweKey = "JWEKEY3982fbkKSdvCk7ladfadn893bdkjfaASmne00";

        String transactionType = "MTR-CHECK-STATUS";
        String method = "POST";

        String mitraId = "IDAMTG0101";
        String channel = "6010";

        // Create Unix Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000);
        String requestTimeStamp = "" + timestamp.getTime();
        System.out.println("requestTimeStamp : " + requestTimeStamp);

        // Create TransactionDetail
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentTimeStamp = format.format(new Date());
        System.out.println("currentTimeStamp : " + currentTimeStamp);

        InMsg trnDetail = new InMsg();
        trnDetail.setRequestTimeStamp(currentTimeStamp);
        trnDetail.setTransactionId(requestTimeStamp.substring(2, 10));

        String originalTransactionId = "11810457";
        String originalTransactionType = "MTR-TRANSFER-ONLINE";
        String originalChannel = "6010";

        InMsg.TransactionData transactionData = new InMsg.TransactionData();
        transactionData.setChannel(originalChannel);
        transactionData.setTransactionId(originalTransactionId);
        transactionData.setTransactionType(originalTransactionType);

        trnDetail.setTransactionData(transactionData);

        System.out.println("transactionDetail :  " + JsonUtils.objectToJsonPrettyString(trnDetail));

        // convert trnDetail to String
        String trnDetailRaw = JsonUtils.objectToJsonString(trnDetail);
        System.out.println("transactionDetailRaw : " + trnDetailRaw);

        // Encrypt
        String trnDetailEnc = JweService.jweEncrypt(jweKey, trnDetailRaw);
        System.out.println("transactionDetailEnc : " + trnDetailEnc);

        // Create Nonce
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        randomUUIDString = randomUUIDString.replace("-", "");
        System.out.println("randomUUIDString : " + randomUUIDString);

        // Create Body
        MitraIncomingMsg reqBody = new MitraIncomingMsg();
        reqBody.setMitraId(mitraId);
        reqBody.setChannel(channel);
        reqBody.setTransactionDetail(trnDetailEnc);

        System.out.println("requestBody :  " + JsonUtils.objectToJsonPrettyString(reqBody));

        // convert trnDetail to String
        String requestBodyRaw = JsonUtils.objectToJsonString(reqBody);
        System.out.println("requestBodyRaw : " + requestBodyRaw);

        // Create Signature
        String signatureBase64 = SignatureService.getSignature(apiId, apiKey, transactionType, requestTimeStamp, method,
                randomUUIDString, requestBodyRaw);
        System.out.println("signatureBase64 : " + signatureBase64);

        // Create JWT
        AuthClaims authClaims = new AuthClaims();
        authClaims.setNonce(randomUUIDString);
        authClaims.setRequestTimeStamp(requestTimeStamp);
        authClaims.setSignatureBase64(signatureBase64);

        String value = JsonUtils.objectToJsonString(authClaims);
        System.out.println("authClaims : " + value);

        Key key = null;
        try {
            key = new HmacKey(jwtKey.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(value);
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setHeader("typ", "JWT");

        String jwt = null;
        try {
            jwt = jws.getCompactSerialization();
        } catch (JoseException e) {
            e.printStackTrace();
        }
        System.out.println("JWT : " + jwt);

        String authString = apiId + jwt;
        System.out.println("AuthString : " + authString);

        String authorizationHeader = "Bearer " + authString;
        System.out.println("Authorization : " + authorizationHeader);

        String apiUrl = "http://10.55.54.130:30120/v1/inquiryrekeningmitra";

        int timeout = 35000;
        RestTemplate access = new RestTemplate(getClientHttpRequestFactory(timeout));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", authorizationHeader);
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyRaw, requestHeaders);
        ResponseEntity<Object> restResult = access.exchange(apiUrl, HttpMethod.POST, requestEntity, Object.class);

        System.out.println("Response Payload");
        System.out.println(JsonUtils.objectToJsonPrettyString(restResult.getBody()));
        try {
            Response rsp = new ObjectMapper().readValue(JsonUtils.objectToJsonString(restResult.getBody()),
                    Response.class);
            String transactionDetail = JweService.jweDecryptAndGetPayload(jweKey, rsp.getTransactionDetail());

            System.out.println("Hasil Decrypt transactionDetail");
            Object rspObj = new ObjectMapper().readValue(transactionDetail, Object.class);
            System.out.println(JsonUtils.objectToJsonPrettyString(rspObj));

            String signatureCheck = SignatureService.getBase64HmacSha256(apiId + rsp.getTransactionDetail(), apiKey);
            System.out.println("SignatureRsp[" + rsp.getSignature() + "]");
            System.out.println("SignatureCek[" + signatureCheck + "]");
            if (signatureCheck.equals(rsp.getSignature())) {
                System.out.println("Signature MATCHED");
            } else {
                System.out.println("Signature NOT MATCHED");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static ClientHttpRequestFactory getClientHttpRequestFactory(int timeout) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
