package com.tnc.app;

import com.tnc.app.services.JweService;

public class AppDecrypt {
    public static void main(String[] args) {
        String jweKey = "JWEKEY3982fbkKSdvCk7ladfadn893bdkjfaASmne00";
        String jweString = "eyJhbGciOiJBMjU2S1ciLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiemlwIjoiREVGIn0.L9caYxIyNE2YjLdB6dLVn79XrdonkFt9Hvr7M-5054tExjVE8kX6hhLqxlCsxfccnWiNOOvnTcHNF1Gm--J2WEpNlOW6No_V.jgke1YEPnjA_aWC53wDM7Q.O3He-wSNmL_M5UUwgyObJavk7b5ADuOSkgxzQopxd2hhFpNBpk0ujSUamQb6iGPjscqPAwgNifEaK9Si3QP3BRIm6Uf_aOgoME2G1NzmzC5-AWdXAYLvsoUHOmFlIdd3L-5ae2WyeMvjxM5EOeXLhehsTXKGQs7V7a-GdSx001tonU6GvvtAB6AiQ2U9cl27.TmnappO0o6WTRnE8liDV1zooH5p89V9kie4JP9FiuBI";

        String payload = JweService.jweDecryptAndGetPayload(jweKey, jweString);
        System.out.println(payload);
    }
}
