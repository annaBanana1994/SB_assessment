package com.starlingbank.assessment.data;

import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import com.starlingbank.assessment.model.payload.NewSavingsGoalsAccountInfo;
import com.starlingbank.assessment.model.response.RoundUpResponseMessage;
import com.starlingbank.assessment.utilities.DefaultData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestData {

    public final static RoundUpResponseMessage getRoundUpResponse_success(){
        RoundUpResponseMessage responseMessage =new RoundUpResponseMessage();
        responseMessage.setSuccessfulTransfer(true);
        responseMessage.setTransferUid(UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164"));
        responseMessage.setPotentialSavings(3333);
        responseMessage.setInOverdraft(false);
        return responseMessage;
    }
    public static Accounts getAccountHoldersAccounts_one() {
        Accounts accountsList = new Accounts();
        List<Account> accounts = new ArrayList<>();
        Account account = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2021-01-02T15:04:55.599Z","Personal");
        accounts.add(account);
        accountsList.setAccounts(accounts);
        return accountsList;
    }
    public static Accounts getAccountHoldersAccounts_two() {
        Accounts accountsList = new Accounts();
        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2021-01-02T15:04:55.599Z","Personal");
        Account account2 = new Account("aaccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2020-01-02T15:04:55.599Z","Personal");
        accounts.add(account1);
        accounts.add(account2);
        accountsList.setAccounts(accounts);
        return accountsList;
    }
    public static Accounts getAccountHoldersAccounts_four() {
        Accounts accountsList = new Accounts();
        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2021-01-02T15:04:55.599Z","Personal");
        Account account2 = new Account("aaccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2020-01-02T15:04:55.599Z","Personal");
        Account account3 = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2021-01-02T15:04:55.599Z","Personal");
        Account account4 = new Account("aaccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY",
                "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd", "GBP",
                "2020-01-02T15:04:55.599Z","Personal");
        accounts.add(account1);
        accounts.add(account2);
        accountsList.setAccounts(accounts);
        return accountsList;
    }
    public static SavingAccountSummary getSavingsAccount() {
        SavingAccountSummary savingAccountSummary = new SavingAccountSummary();
        savingAccountSummary.setTargetAmount(100);
        savingAccountSummary.setSavingsGoalUid("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc");
        savingAccountSummary.setSavedAmount(0);
        savingAccountSummary.setCurrency("GBP");
        savingAccountSummary.setName(DefaultData.SAVINGS_GOALS_NAME);
        return savingAccountSummary;
    }
    public static List<FeedItemSummary> getFeedItems() {
        List<FeedItemSummary> list = new ArrayList<>();
        FeedItemSummary item1 = generateFeedItem("OUT","GBP", "bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbee",380);
        FeedItemSummary item2 = generateFeedItem("OUT","GBP", "aaccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbff",380);
        FeedItemSummary item3 = generateFeedItem("OUT","GBP", "ddccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbgg",380);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        return list;
    }
    private static FeedItemSummary generateFeedItem(String direction, String currency, String catUid, String feedUid, int amount){
        FeedItemSummary item = new FeedItemSummary();
        item.setDirection(direction);
        item.setCurrency(currency);
        item.setCategoryUid(catUid);
        item.setFeedItemUid(feedUid);
        item.setAmount(amount);
        return item;
    }
    public static List<FeedItemSummary> getBiggerFeedItems() {
        List<FeedItemSummary> list = getFeedItems();
        list.add(generateFeedItem("OUT","GBP", "bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbee",380));
        list.add(generateFeedItem("OUT","GBP", "aaccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc", "bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbff",380));
        list.add(generateFeedItem("OUT","GBP", "ddccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbgg",380));
        return list;
    }
    public static List<FeedItemSummary> getEmptyList(){
        List<FeedItemSummary> list = new ArrayList<>();
        return list;
    }

    // Client Stubbing
    public static Accounts accountsList(){
        Accounts accountsModel = new Accounts();
        List<Account> accounts = new ArrayList<>();
        Account account = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY", "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd",
                "GBP", "2021-01-03T18:36:27.383Z","Personal");
        accounts.add(account);
        accountsModel.setAccounts(accounts);
        return accountsModel;
    }

    public static HttpHeaders generateHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", DefaultData.AUTHORIZATION_NAME+"1234");
        return headers;
    }

    public static NewSavingsGoalsAccountInfo generateNewSavingsAccount(){
        NewSavingsGoalsAccountInfo newSavingsGoalsAccountInfo = new NewSavingsGoalsAccountInfo(DefaultData.
                SAVINGS_GOALS_NAME,DefaultData.SAVINGS_GOALS_CURRENCY, DefaultData.TARGET,DefaultData.SAVINGS_GOALS_PHOTO );
        return newSavingsGoalsAccountInfo;
    }
}
