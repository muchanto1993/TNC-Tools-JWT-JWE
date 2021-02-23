package com.tnc.app.entity.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthClaims {

    private String signatureBase64;
    private String nonce;
    private String requestTimeStamp;

    public AuthClaims() {
    }

    public AuthClaims(String signatureBase64, String nonce, String requestTimeStamp) {
        this.signatureBase64 = signatureBase64;
        this.nonce = nonce;
        this.requestTimeStamp = requestTimeStamp;
    }

}