package com.starlingbank.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;

import java.util.List;

public interface ClientService {

    List<Account> getAccountHoldersAccounts(String accountHolderAccessToken);

    SavingAccountSummary getSavingsAccount(String accountUid) throws JsonProcessingException;

    List<FeedItemSummary> getWeeksTransactions(String accountUid, String defaultCategory, String lastTimeStamp, String currentTimeStamp);

    void transferToSavingsAccount(SavingAccountSummary savingsAccount, int savingsAddition, String accountUid, String transferId);

    String getLastRoundUpTransferTimeStamp(String savingsGoalUid);

    boolean checkIfRoundUpServicePushesBalanceIntoOverDraft(String accountUid, int savingsAddition);
}


