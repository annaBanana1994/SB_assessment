package com.starlingbank.assessment.model.response;

import lombok.Data;

@Data
public class RoundUpResponseMessage {
    Boolean successfulTransfer;
    Boolean inOverdraft;
    String message;
    int potentialSavings;
    int transferUid;
}
