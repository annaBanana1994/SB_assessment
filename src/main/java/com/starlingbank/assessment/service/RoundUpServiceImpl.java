package com.starlingbank.assessment.service;

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
                response.setMessage(addToMessage(response.getMessage(),DefaultData.CHECK_ACCOUNT_FAIL+
                        DefaultData.LINE_BREAK));
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

            if (lastTimeStamp==null){
                //default for since the start of 2020
                lastTimeStamp=DefaultData.BEGINNING_2020_TS;
                response.setMessage(addToMessage(response.getMessage(), DefaultData.NO_PREVIOUS_TRANSACTIONS_MESSAGE));
            }

            String currentTimeStamp = instant.toString();

            // Will flag up the delay in calling this service
            // Logic not written yet, default to false
            if (checkIfTransferHasBeenLongerThanAWeek(lastTimeStamp, currentTimeStamp)) {
                LOGGER.info("Longer than a week since last savings transfer. Last transfer data: "+lastTimeStamp);
                String message = response.getMessage();
                message+=DefaultData.OVER_A_WEEK+DefaultData.LINE_BREAK;
                response.setMessage(message);
            }

            //TODO log if none and create response
            LOGGER.info("Calculating round up savings ");
            //calculate how much going into savings
            int savingsAddition = calculatingWeeklySavings(accountUid, account.getDefaultCategory(), lastTimeStamp,
                    currentTimeStamp);
            //If there have been no outgoing transactions in the given time period
            if(savingsAddition==0) {
                LOGGER.info(DefaultData.NO_OUTGOING_TRANSACTIONS+lastTimeStamp);
                response.setMessage(DefaultData.NO_OUTGOING_TRANSACTIONS);
                response.setPotentialSavings(savingsAddition);
                response.setSuccessfulTransfer(false);
                return response;
            }

            response.setPotentialSavings(savingsAddition);

            // Checking if transaction means account goes into overdraft
            String overdraftMessage=DefaultData.OVERDRAFT_STATUS;
            response.setInOverdraft(clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft(accountUid,
                    savingsAddition));

            overdraftMessage += response.getInOverdraft() ? DefaultData.IN_OVERDRAFT_TRUE :
                    DefaultData.IN_OVERDRAFT_False;

            response.setMessage(addToMessage(response.getMessage(),overdraftMessage));

            //Creating unique TransferUid
            UUID transferUid=UUID.randomUUID();
            LOGGER.debug("Creating transferUid: " +transferUid);

            //Transfer to savings account
            boolean success = clientService.transferToSavingsAccount(savingsAccount, savingsAddition, accountUid,
                    transferUid);
            response.setSuccessfulTransfer(success);
            LOGGER.info("Round up money saving service success: "+success);
            if(success) {
                response.setTransferUid(transferUid);
            }

            LOGGER.debug("Returning response");
            return response;
        } catch (Exception e) {
            LOGGER.warn("Exception thrown.");
            throw new Exception("External call error. Message: "+e.getMessage());
            // accountHolderAccessToken LOG
        }
    }

    //TODO different years, different months
    //Maybe just change string back into instant cause then can use interval
    private boolean checkIfTransferHasBeenLongerThanAWeek(String lastTimeStamp, String currentTimeStamp) {
        // should be in same format now

        String[] previousDate = getDateFormatToCompare(lastTimeStamp);
        String[] currentDate = getDateFormatToCompare(currentTimeStamp);

        //2020-01-01T12:34:56.000Z
        //2020-01-07T13:14:52.777Z

        LOGGER.debug("Check for over week since last transfer default false");
        return false;
    }

    private String[] getDateFormatToCompare(String date){
        String[] split=date.split("-");
        split[2]=split[2].substring(0,2);
        return split;
    }

    // Put in any required checks of the account
    private boolean accountChecks(Account account){
        // ie. if the account has to be personal for the round-up service to apply
        account.getName(); // check personal
        return true;
    }

    private int calculatingWeeklySavings(String accountUid, String categoryUid, String lastTimeStamp,
                                        String currentTimeStamp) throws Exception {
        //get list of transactions from last savings transfer and now
        LOGGER.debug("Going into Client Service Layer to get list of transactions. CategoryUid: "+categoryUid);
        List<FeedItemSummary> feedItems =clientService.getWeeksOutGoingTransactions(accountUid, categoryUid,lastTimeStamp,
                currentTimeStamp);
        //equals feed item but minimised to amounts as thats all i want

        //get round up amount
        int savingsAddition=0;

        for (FeedItemSummary item:feedItems) {
            savingsAddition+=100-(item.getAmount()%100);
        }
        LOGGER.info("Calculated amount to be transfered to savings");
        return savingsAddition;
    }

    private String addToMessage(String oldMessage, String additionalMessage){
        return oldMessage==null?additionalMessage:oldMessage+DefaultData.LINE_BREAK+additionalMessage;
    }
}
