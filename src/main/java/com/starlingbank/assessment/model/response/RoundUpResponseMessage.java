package com.starlingbank.assessment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class RoundUpResponseMessage {
    Boolean successfulTransfer;
    Boolean inOverdraft;
    String message;
    int potentialSavings;
    UUID transferUid;
}
