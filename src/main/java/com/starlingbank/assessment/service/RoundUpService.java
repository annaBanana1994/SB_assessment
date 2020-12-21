package com.starlingbank.assessment.service;


import com.fasterxml.jackson.core.JsonProcessingException;

public interface RoundUpService {
    String transferWeeksSavings(String accountHolderAccessToken) throws JsonProcessingException;

    int calculatingWeeklySavings(String accountUid, String categoryUid, String lastTimeStamp, String currentTimeStamp);
}
