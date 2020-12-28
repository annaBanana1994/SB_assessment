package com.starlingbank.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.starlingbank.assessment.model.*;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import com.starlingbank.assessment.model.payload.AddToSavingsInfo;
import com.starlingbank.assessment.model.payload.Amount;
import com.starlingbank.assessment.model.payload.NewSavingsGoalsAccountInfo;
import com.starlingbank.assessment.utilities.DefaultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private RestTemplate restTemplate;

    // Dont Think you can use this method with custom hearderss
    // RequestEntity<MyRequest> request = RequestEntity
    //     .post("https://example.com/{foo}", "bar")
    //     .accept(MediaType.APPLICATION_JSON)
    //     .body(body);

    private static final Logger LOGGER= LoggerFactory.getLogger(ClientServiceImpl.class);

    private String accessToken;

    @Value("${clientAPI.rootPath}")
    private String rootPath;

    HttpHeaders headers = new HttpHeaders();

    public void createAuthorizationValue(String accountHolderAccessToken){
        this.accessToken = DefaultData.AUTHORIZATION_NAME+accountHolderAccessToken;
    }

    public HttpHeaders buildHeaders() {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", accessToken);
        return headers;
    }

    public Accounts getAccountHoldersAccounts(String accountHolderAccessToken) throws Exception {
        createAuthorizationValue(accountHolderAccessToken);
        // Unsure if calling the method to build headers will work
        // build the request
        HttpEntity request = new HttpEntity(buildHeaders());

        String url = rootPath + "/accounts";
        LOGGER.debug(url);

        LOGGER.info("Making external call for list of Account Holder's accounts");
        // HTTP call
        ResponseEntity<Accounts> response = this.restTemplate.exchange(url, HttpMethod.GET, request, Accounts.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }
    }

    public SavingAccountSummary getSavingsAccount(String accountUid , String currency) throws Exception {
        // build the request
        HttpEntity request = new HttpEntity(buildHeaders());

        String url = rootPath + "/account/" + accountUid + "/savings-goals";

        // HTTP call
        LOGGER.info("Making external call for list of account's savings accounts");
        ResponseEntity<Object> response = this.restTemplate.exchange(url, HttpMethod.GET, request,
                Object.class);

        SavingAccountSummary savingAccountSummary;

            /// If this doesnt work try linked hashMap to get the values rrequired
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode fullResponseNode = mapper.valueToTree(response.getBody());

                ArrayNode object= (ArrayNode) fullResponseNode.get("savingsGoalList");
                ArrayList<JsonNode> listOfSavingsAccounts = new ArrayList();

                for(int i=0;i<object.size();i++){
                    listOfSavingsAccounts.add(object.get(i));
                }

                if (listOfSavingsAccounts.isEmpty()) {
                    LOGGER.debug("Account has no savings accounts");
                    //create new account
                    savingAccountSummary = createFutureAdventuresSavingsAccount(accountUid, currency);
                } else {
                    JsonNode jsonNode = null;
                    for (int i = 0; i < listOfSavingsAccounts.size(); i++) {
                        // Specifying the name of the savings account
                        if (listOfSavingsAccounts.get(i).get("name").asText().equals(DefaultData.SAVINGS_GOALS_NAME))
                            jsonNode = listOfSavingsAccounts.get(i);
                    }
                    if (jsonNode == null) {
                        LOGGER.debug("Account hasn't got correct savings account, name: " + DefaultData.SAVINGS_GOALS_NAME);
                        //create the 'Future Adventures' account
                        savingAccountSummary = createFutureAdventuresSavingsAccount(accountUid, currency);
                    } else {
                        savingAccountSummary = translateJsonNodeIntoSummary(jsonNode);
                    }
                }
                LOGGER.debug("Return savings account "+savingAccountSummary.getSavingsGoalUid());
                return savingAccountSummary;
            } else {
                LOGGER.warn("Client response status: "+response.getStatusCode());
                throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
            }
    }

    public SavingAccountSummary createFutureAdventuresSavingsAccount(String accountUid, String currency) throws Exception {
        NewSavingsGoalsAccountInfo newSavingsGoalsAccountInfo = new NewSavingsGoalsAccountInfo(DefaultData.
                SAVINGS_GOALS_NAME,currency, DefaultData.TARGET,DefaultData.SAVINGS_GOALS_PHOTO );

        //build request
        HttpEntity<NewSavingsGoalsAccountInfo> request = new HttpEntity<>(newSavingsGoalsAccountInfo, buildHeaders());

        String url = rootPath+"/account/"+accountUid+"/savings-goals";

        // HTTP call
        LOGGER.info("Making external call to create savings account");
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.PUT, request,JsonNode.class);

        SavingAccountSummary savingAccountSummary;

        if(response.getStatusCode() == HttpStatus.OK) {
            LOGGER.debug("Savings account successfully created");
            String savingsGoalUid=response.getBody().get("savingsGoalUid").asText();
            savingAccountSummary=getSavingAccount(savingsGoalUid,accountUid);
        }else {
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }
        return savingAccountSummary;
    }

    public SavingAccountSummary getSavingAccount(String savingsGoalUid, String accountUid) throws Exception {
            //build request
            HttpEntity request = new HttpEntity(buildHeaders());

            String url = rootPath + "/account/" + accountUid + "/savings-goals/" + savingsGoalUid + "";

            LOGGER.debug("Making external call to get savings account "+savingsGoalUid);
            // HTTP call
            ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class);
            //Check the response
            if (response.getStatusCode()!= HttpStatus.OK){
                LOGGER.warn("Client response status: "+response.getStatusCode());
                throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
            }
            SavingAccountSummary newSavingAccountSummary = translateJsonNodeIntoSummary(response.getBody());

            return newSavingAccountSummary;
    }
    public SavingAccountSummary translateJsonNodeIntoSummary(JsonNode jsonNode){
        SavingAccountSummary savingAccountSummary = new SavingAccountSummary();
        savingAccountSummary.setCurrency(jsonNode.get("target").get("currency").asText());
        savingAccountSummary.setName(jsonNode.get("name").asText());
        savingAccountSummary.setSavedAmount(jsonNode.get("totalSaved").get("minorUnits").asInt());
        savingAccountSummary.setSavingsGoalUid(jsonNode.get("savingsGoalUid").asText());
        savingAccountSummary.setTargetAmount(jsonNode.get("target").get("minorUnits").asInt());
        return savingAccountSummary;
    }

    public List<FeedItemSummary> getWeeksTransactions(String accountUid, String defaultCategory, String lastTimeStamp,
                                                      String currentTimeStamp) throws Exception {
        // build the request
        HttpEntity request = new HttpEntity(buildHeaders());

        String url = rootPath+"/feed/account/"+accountUid+"/category/"+defaultCategory+"/transactions-between?" +
                "minTransactionTimestamp="+lastTimeStamp+"&maxTransactionTimestamp="+currentTimeStamp;

        LOGGER.info("Making external call to get feed of transaction between "+lastTimeStamp+" and "+currentTimeStamp);
        // HTTP call
        ResponseEntity<Object> response = this.restTemplate.exchange(url, HttpMethod.GET, request,Object.class);
        //Check the response
        if (response.getStatusCode()!= HttpStatus.OK){
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode fullResponseNode = mapper.valueToTree(response.getBody());
        ArrayNode object =(ArrayNode) fullResponseNode.get("feedItems");
        ArrayList<JsonNode> listOfFeedItems = new ArrayList<>();

        for(int i=0;i<object.size();i++){
            listOfFeedItems.add(object.get(i));
        }

        ArrayList<FeedItemSummary> feedItemsSummariesed = new ArrayList<>();

        // Extract the information required and save in the summary model
        for (JsonNode node:listOfFeedItems) {
            FeedItemSummary feedItemSummary = new FeedItemSummary();
            //Only the transactions going out
            if (node.get("direction").asText().equals("OUT")){
                feedItemSummary.setDirection(node.get("direction").asText());
                feedItemSummary.setFeedItemUid(node.get("feedItemUid").asText());
                feedItemSummary.setAmount(node.get("amount").get("minorUnits").asInt());
                feedItemSummary.setCategoryUid(node.get("categoryUid").asText());
                feedItemSummary.setCurrency(node.get("amount").get("currency").asText());
                feedItemsSummariesed.add(feedItemSummary);
            }
        }
        LOGGER.debug("Translating feed item information list into summary version");
        return feedItemsSummariesed;
    }

    public boolean transferToSavingsAccount(SavingAccountSummary savingsAccount, int savingsAddition, String accountUid,
                                         UUID transferId) throws Exception {
        LOGGER.debug("Build request to transfer "+savingsAddition+"in "+savingsAccount.getCurrency()+ " into "+
                savingsAccount.getSavingsGoalUid());
        Amount amount = new Amount(savingsAccount.getCurrency(),savingsAddition);
        AddToSavingsInfo addToSavingsInfo = new AddToSavingsInfo(amount);

        //build request
        HttpEntity<AddToSavingsInfo> request = new HttpEntity<>(addToSavingsInfo, buildHeaders());

        String url = rootPath+"/account/"+accountUid+"/savings-goals/"+savingsAccount.getSavingsGoalUid()+"/add-" +
                "money/"+transferId;

        LOGGER.info("Making external call to transfer money into savings");

        // HTTP call
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.PUT, request,JsonNode.class);

        if(response.getStatusCode()!=HttpStatus.OK) {
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }

        boolean success = response.getBody().get("success").asText().equals("true")?true:false;
        LOGGER.info("Savings Transfer from client side was successful: "+success);
        if(success)
            LOGGER.debug("TransferUid: "+transferId);
        return success;
    }

    public String getLastRoundUpTransferTimeStamp(String accountUid, String savingsGoalUid, Instant instant) throws Exception {
        // build the request
        HttpEntity request = new HttpEntity(buildHeaders());

        // Get feed from past month
        Instant threeWeeksAgo=instant.minus(21, ChronoUnit.DAYS);
        String url = rootPath+"/feed/account/"+accountUid+"/category/"+savingsGoalUid+"" +
                "?changesSince="+threeWeeksAgo;

        LOGGER.info("Making external call for savings account transactions since "+threeWeeksAgo.toEpochMilli());
        // HTTP call
        ResponseEntity<Object> response = this.restTemplate.exchange(url, HttpMethod.GET, request,Object.class);
        //Check the response
        if (response.getStatusCode()!= HttpStatus.OK){
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode fullResponseNode = mapper.valueToTree(response.getBody());
        ArrayNode object= (ArrayNode) fullResponseNode.get("feedItems");

        if(object.isEmpty())
            return null;

        // Get last transaction
        JsonNode lastFeedItem = object.get(object.size()-1);

        // Check last transaction was coming in
        if(lastFeedItem.get("direction").asText().equals("IN"))
            // Can add check of reference how this could be set
            return lastFeedItem.get("transactionTime").asText();
        else {
            //Should have other method here
            LOGGER.warn("Last transaction for savings account was a withdraw");
            throw new Exception();
        }
    }

    public boolean checkIfRoundUpServicePushesBalanceIntoOverDraft(String accountUid, int savingsAddition) throws Exception {
        // build the request
        HttpEntity request = new HttpEntity(buildHeaders());

        String url = rootPath+"/accounts/"+accountUid+"/confirmation-of-funds?targetAmountInMinorUnits="
                +savingsAddition;

        LOGGER.info("Making external call to check if funds available");
        // HTTP call
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.GET, request,JsonNode.class);
        //Check the response
        if (response.getStatusCode()!= HttpStatus.OK){
            LOGGER.warn("Client response status: "+response.getStatusCode());
            throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        }
        boolean inOverdraft = response.getBody().get("accountWouldBeInOverdraftIfRequestedAmountSpent")
                .asText().equals("true")?true:false;
        if(inOverdraft)
            LOGGER.info("Transfering round up savings would take account into its overdraft");
        return inOverdraft;
    }
}
