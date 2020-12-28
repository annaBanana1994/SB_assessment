package com.starlingbank.assessment;

import com.starlingbank.assessment.model.response.RoundUpResponseMessage;

import java.util.UUID;

public class TestData {

    public final static RoundUpResponseMessage getRoundUpResponse_success(){
        RoundUpResponseMessage responseMessage =new RoundUpResponseMessage();
        responseMessage.setSuccessfulTransfer(true);
        responseMessage.setTransferUid(UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164"));
        responseMessage.setPotentialSavings(3333);
        responseMessage.setInOverdraft(false);
        return responseMessage;
    }


}
