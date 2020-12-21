package com.starlingbank.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RoundUpServiceImpl implements RoundUpService{

    @Autowired
    ClientService clientService;

    public String transferWeeksSavings(String accountHolderAccessToken) throws JsonProcessingException {
        //Get account information
        List<Account> accountHolderAccounts =clientService.getAccountHoldersAccounts(accountHolderAccessToken);
        //ASSUME just one account for now - due to response
        //for each account added in later but well just take the first one for now

        Account account =accountHolderAccounts.get(0);

        //Haven't added logic yet
//        if(!accountChecks(account))
//            throw exception with error message

        String accountUid=account.getAccountUid();

        //Get savings account
        //check the name reference or soemthing if more/and create if none
        SavingAccountSummary savingsAccount=clientService.getSavingsAccount(accountUid);

        // last time stamp from feed of this savings account //check the reference and everything
        //checks of reference / name if there is one but still not sure on how this follows through
        String lastTimeStamp=clientService.getLastRoundUpTransferTimeStamp(savingsAccount.getSavingsGoalUid());

        Instant instant = Instant.now();
        long currentTimeStamp = instant.toEpochMilli();

        //Haven't added logic yet
//        if(!checkIfTransferHasBeenLongerThanAWeek(lastTimeStamp,currentTimeStamp))
//            //add message that the savings haven't been added to at correct times
//            ;

        //calculate how much going into savings
        int savingsAddition= calculatingWeeklySavings(accountUid, account.getDefaultCategory(), lastTimeStamp, String.valueOf(currentTimeStamp));


        // get balance check - if true - or if warning in overdraft
        String message= (clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft(accountUid,savingsAddition))
                ? "Funds available":"In overdraft";

        String transferId="";
        //Transfer to savings account
        clientService.transferToSavingsAccount(savingsAccount, savingsAddition, accountUid, transferId);
        //result needs to know if success you know

        //success message - with note of overdraft

        //add message
        return null;
    }

    private boolean checkIfTransferHasBeenLongerThanAWeek(String lastTimeStamp, long currentTimeStamp) {
        return true;
    }

    //Haven't added logic yet
    private boolean accountChecks(Account account){
        account.getCurrency(); //check currency maybe?
        account.getName(); // check personal
        return true;
    }

    public int calculatingWeeklySavings(String accountUid, String categoryUid, String lastTimeStamp, String currentTimeStamp){
        //get list of transactions from last savings transfer and now
        List<FeedItemSummary> feedItems =clientService.getWeeksTransactions(accountUid, categoryUid,lastTimeStamp, currentTimeStamp);
        //equals feed item but minimised to amounts as thats all i want

        //get round up amount
        int savingsAddition=0;
        for (FeedItemSummary item:feedItems) {
            savingsAddition =+ 100-(item.getAmount()%100);
        }
        return savingsAddition;
    }

}
