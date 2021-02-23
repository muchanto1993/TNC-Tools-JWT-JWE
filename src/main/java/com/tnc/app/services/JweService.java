package com.tnc.app.services;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.IntegrityException;
import org.jose4j.lang.JoseException;
import org.jose4j.zip.CompressionAlgorithmIdentifiers;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JweService {

    public static String jweEncrypt(String jweKey, String rawBody) {
        try {
            JsonWebEncryption senderJwe = new JsonWebEncryption();
            senderJwe.setPayload(rawBody);
            senderJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
            senderJwe
                    .setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
            senderJwe.setKey(setJsonWebKeyKey(jweKey).getKey());
            senderJwe.setCompressionAlgorithmHeaderParameter(CompressionAlgorithmIdentifiers.DEFLATE);
            return senderJwe.getCompactSerialization();
        } catch (IntegrityException e) {
            log.error(" Err: ", e);
            return "55";
        } catch (Exception e) {
            return "99";
        }
    }

    private static JsonWebKey setJsonWebKeyKey(String keyString) throws JoseException {
        return JsonWebKey.Factory.newJwk("{\"kty\":\"oct\",\"k\":\"" + keyString + "\"}");
    }

    public static String jweDecryptAndGetPayload(String jweKey, String jweString) {
        try {
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setAlgorithmConstraints(
                    new AlgorithmConstraints(ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.A256KW));
            jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST,
                    ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512));
            jwe.setCompactSerialization(jweString);
            jwe.setKey(setJsonWebKeyKey(jweKey).getKey());
            return jwe.getPayload();
        } catch (IntegrityException e) {
            log.error(" Err: ", e);
            return "55";
        } catch (Exception e) {
            return "99";
        }
    }

}
