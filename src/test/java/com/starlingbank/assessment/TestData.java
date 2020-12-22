package com.starlingbank.assessment;

import com.starlingbank.assessment.model.response.RoundUpResponseMessage;

public class TestData {

    public final static RoundUpResponseMessage getRoundUpResponse_success(){
        RoundUpResponseMessage responseMessage =new RoundUpResponseMessage();
        responseMessage.setSuccessfulTransfer(true);
        responseMessage.setTransferUid(12345);
        responseMessage.setPotentialSavings(3333);
        responseMessage.setInOverdraft(false);
        return responseMessage;
    }


}
