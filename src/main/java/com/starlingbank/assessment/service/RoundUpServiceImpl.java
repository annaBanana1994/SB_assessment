package com.starlingbank.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import com.starlingbank.assessment.model.response.RoundUpResponseMessage;
import com.starlingbank.assessment.utilities.DefaultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RoundUpServiceImpl implements RoundUpService{

    private static final Logger LOGGER= LoggerFactory.getLogger(RoundUpServiceImpl.class);

    @Autowired
    ClientService clientService;

    public RoundUpResponseMessage transferWeeksSavings(String accountHolderAccessToken) throws Exception {
        try {
            LOGGER.info("In transferWeeksSavings method");
            //Create response
            RoundUpResponseMessage response = new RoundUpResponseMessage();

            LOGGER.debug("Going into Client Service Layer: Access Token: "+accountHolderAccessToken);
            //Get account holder's accounts
            Accounts accountHolderAccounts =clientService.getAccountHoldersAccounts(accountHolderAccessToken);


            LOGGER.debug("Locates first account in list returned from client side");
            //ASSUMPTION - Just one account
            Account account = accountHolderAccounts.getAccounts().get(0);

            // Haven't added logic yet

            if(!accountChecks(account)) {
                LOGGER.debug("Account, with accountUid "+account.getAccountUid()+", check result: negative");
                response.setSuccessfulTransfer(false);
                response.setMessage(DefaultData.CHECK_ACCOUNT_FAIL+DefaultData.LINE_BREAK);
                return response;
            }
            LOGGER.debug("Account, with accountUid "+account.getAccountUid()+", check result: positive");

            String accountUid = account.getAccountUid();

            LOGGER.debug("Going into Client Service Layer to retrieve savings account");
            //Get savings account for associated accountUid
            SavingAccountSummary savingsAccount = clientService.getSavingsAccount(accountUid, account.getCurrency());

            // Get current timestamp
            Instant instant = Instant.now();

            LOGGER.debug("Going into Client Service Layer to get last round up transfer timestamp");
            // last time stamp from feed of this savings account
            // Ensures no transactions are missed
            String lastTimeStamp = clientService.getLastRoundUpTransferTimeStamp(accountUid,
                    savingsAccount.getSavingsGoalUid(), instant);

            long currentTimeStamp = instant.toEpochMilli();

            // Will flag up the delay in calling this service
            // Logic not written yet, default to false
            if (checkIfTransferHasBeenLongerThanAWeek(lastTimeStamp, currentTimeStamp)) {
                LOGGER.info("Longer than a week since last savings transfer. Last transfer data: "+lastTimeStamp);
                String message = response.getMessage();
                message+=DefaultData.OVER_A_WEEK+DefaultData.LINE_BREAK;
                response.setMessage(message);
            }

            LOGGER.info("Calculating round up savings ");
            //calculate how much going into savings
            int savingsAddition = calculatingWeeklySavings(accountUid, account.getDefaultCategory(), lastTimeStamp,
                    String.valueOf(currentTimeStamp));
            response.setPotentialSavings(savingsAddition);

            // get balance check
            String overdraftMessage=DefaultData.OVERDRAFT_STATUS;
            overdraftMessage += (clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft(accountUid, savingsAddition))
                    ? DefaultData.IN_OVERDRAFT_TRUE : DefaultData.IN_OVERDRAFT_False;

            String oldMessage=response.getMessage();
            response.setMessage(oldMessage+overdraftMessage+DefaultData.LINE_BREAK);

            //Creating unique TransferUid
            UUID transferUid = UUID.fromString(DefaultData.UUID_INIT);
            int uniqueTransferUid = transferUid.clockSequence();
            LOGGER.debug("Creating transferUid: " +uniqueTransferUid);

            //Transfer to savings account
            boolean success = clientService.transferToSavingsAccount(savingsAccount, savingsAddition, accountUid,
                    uniqueTransferUid);
            response.setSuccessfulTransfer(success);
            LOGGER.info("Round up money saving service success: "+success);
            if(success) {
                response.setTransferUid(uniqueTransferUid);
            }

            LOGGER.debug("Returning response");
            return response;
        } catch (Exception e) {
            LOGGER.warn("Exception thrown.");
            throw new Exception("External call error. Message: "+e.getMessage());
            // accountHolderAccessToken LOG
        }
    }

    private boolean checkIfTransferHasBeenLongerThanAWeek(String lastTimeStamp, long currentTimeStamp) {
        // Need to figure translation back with these
        LOGGER.debug("Check for over week since last transfer default false");
        return false;
    }

    // Put in any required checks of the account
    private boolean accountChecks(Account account){
        // ie. if the account has to be personal for the round-up service to apply
        account.getName(); // check personal
        return true;
    }

    public int calculatingWeeklySavings(String accountUid, String categoryUid, String lastTimeStamp,
                                        String currentTimeStamp) throws Exception {
        //get list of transactions from last savings transfer and now
        LOGGER.debug("Going into Client Service Layer to get list of transactions. CategoryUid: "+categoryUid);
        List<FeedItemSummary> feedItems =clientService.getWeeksTransactions(accountUid, categoryUid,lastTimeStamp,
                currentTimeStamp);
        //equals feed item but minimised to amounts as thats all i want

        //get round up amount
        int savingsAddition=0;
        for (FeedItemSummary item:feedItems) {
            savingsAddition =+ 100-(item.getAmount()%100);
        }
        LOGGER.info("Calculated amount to be transfered to savings");
        return savingsAddition;
    }

}
