package com.tnc.app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MitraIncomingMsg {

    private String mitraId;
    private String channel;
    private String transactionDetail;

}
