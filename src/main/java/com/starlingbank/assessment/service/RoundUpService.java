package com.starlingbank.assessment.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlingbank.assessment.model.response.RoundUpResponseMessage;

public interface RoundUpService {
    RoundUpResponseMessage transferWeeksSavings(String accountHolderAccessToken) throws Exception;

}
