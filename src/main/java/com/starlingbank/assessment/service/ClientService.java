package com.starlingbank.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;

import java.time.Instant;
import java.util.List;

public interface ClientService {

    List<Account> getAccountHoldersAccounts(String accountHolderAccessToken) throws Exception;

    SavingAccountSummary getSavingsAccount(String accountUid, String currency) throws Exception;

    List<FeedItemSummary> getWeeksTransactions(String accountUid, String defaultCategory, String lastTimeStamp, String currentTimeStamp) throws Exception;

    boolean transferToSavingsAccount(SavingAccountSummary savingsAccount, int savingsAddition, String accountUid, int transferUid) throws Exception;

    //Check refernce if it has one
    String getLastRoundUpTransferTimeStamp(String accountUid, String savingsGoalUid, Instant instant) throws Exception;

    boolean checkIfRoundUpServicePushesBalanceIntoOverDraft(String accountUid, int savingsAddition) throws Exception;
}


