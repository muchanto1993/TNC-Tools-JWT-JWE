package com.tnc.app.model.features.checkstatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InMsg {

    private String requestTimeStamp;

    private String transactionId;

    private TransactionData transactionData;

    @Getter
    @Setter
    public static class TransactionData {

        private String channel;

        private String transactionId;

        private String transactionType;
    }

}
