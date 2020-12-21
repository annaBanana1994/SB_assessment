package com.starlingbank.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.starlingbank.assessment.model.*;
import com.starlingbank.assessment.utilities.DefaultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    //look into
    // URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
    //          .path("/{id}")
    //          .buildAndExpand(createdStudent.getId())
    //          .toUri();
    //
    //        return ResponseEntity.created(uri)
    //          .body(createdStudent);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${clientAPI.accessToken}")
    private String accessToken;

    @Value("${clientAPI.rootPath}")
    private String rootPath;

    public List<Account> getAccountHoldersAccounts(String accountHolderAccessToken) {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        // build the request
        HttpEntity request = new HttpEntity(headers);

        String url = rootPath+"/accounts";

        // HTTP call
        ResponseEntity<List> response = this.restTemplate.exchange(url, HttpMethod.GET, request,List.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public SavingAccountSummary getSavingsAccount(String accountUid) throws JsonProcessingException {

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        // build the request
        HttpEntity request = new HttpEntity(headers);

        String url = rootPath+"/account/{"+accountUid+"}/savings-goals";

        // HTTP call
        ResponseEntity<List> response = this.restTemplate.exchange(url, HttpMethod.GET, request,List.class);

        SavingAccountSummary savingAccountSummary = new SavingAccountSummary();

        if(response.getStatusCode() == HttpStatus.OK) {
            List<JsonNode> listOfSavings = response.getBody();
            JsonNode jsonNode= null;
            if(listOfSavings.isEmpty()){
                //create new account
                savingAccountSummary=createFutureAdventuresSavingsAccount(accountUid);
            }else {
                for(int i=0;i<listOfSavings.size();i++){
                    // Specifying the name of the savings account
                    if(listOfSavings.get(i).get("name").asText()==DefaultData.SAVINGS_GOALS_NAME)
                        jsonNode=listOfSavings.get(i);
                }
                if (jsonNode==null)
                    //create the 'Future Adventures' account
                    savingAccountSummary=createFutureAdventuresSavingsAccount(accountUid);
                else {
                    savingAccountSummary=translateJsonNodeIntoSummary(jsonNode);
                }
            }
            return savingAccountSummary;
        } else {
             //exception with message
            return null;
        }
    }

    public SavingAccountSummary createFutureAdventuresSavingsAccount(String accountUid){
        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        // build the request
        NewSavingsGoalsAccountInfo newSavingsGoalsAccountInfo = DefaultData.NEW_SAVINGS_GOALS_ACCOUNT_INFO;

        //build request
        HttpEntity<NewSavingsGoalsAccountInfo> request = new HttpEntity<>(newSavingsGoalsAccountInfo, headers);

        String url = rootPath+"/account/{"+accountUid+"}/savings-goals";

        // HTTP call
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.PUT, request,JsonNode.class);
        SavingAccountSummary savingAccountSummary;
        if(response.getStatusCode() == HttpStatus.OK) {
            String savingsGoalUid=response.getBody().get("savingsGoalUid").asText();
            savingAccountSummary=getSavingAccount(savingsGoalUid,accountUid);
        }else {
            //error message
            return null;
        }
        return savingAccountSummary;
    }
    public SavingAccountSummary getSavingAccount(String savingsGoalUid, String accountUid){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        //build request
        HttpEntity request = new HttpEntity(headers);

        String url = rootPath+"/account/{"+accountUid+"}/savings-goals/{"+savingsGoalUid+"}";

        // HTTP call
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.GET, request,JsonNode.class);

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
                                                      String currentTimeStamp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        // build the request
        HttpEntity request = new HttpEntity(headers);


        String url = rootPath+"/feed/account/{"+accountUid+"}/category/{"+defaultCategory+"}/transactions-between?" +
                "minTransactionTimestamp="+lastTimeStamp+"&maxTransactionTimestamp"+currentTimeStamp;

        // HTTP call
        ResponseEntity<List> response = this.restTemplate.exchange(url, HttpMethod.GET, request,List.class);
        List<JsonNode> feedItemsFull = response.getBody();

        ArrayList<FeedItemSummary> feedItemsSummariesed = new ArrayList<>();
        //Direction has to be out
        for (JsonNode node:feedItemsFull) {
            FeedItemSummary feedItemSummary = new FeedItemSummary();
            //Only the transactions going out
            if (node.get("direction").asText()=="OUT"){
                feedItemSummary.setDirection(node.get("direction").asText());
                feedItemSummary.setFeedItemUid(node.get("feedItemUid").asText());
                feedItemSummary.setAmount(node.get("amount").get("minorUnits").asInt());
                feedItemSummary.setCategoryUid(node.get("categoryUid").asText());
                feedItemSummary.setCurrency(node.get("amount").get("currency").asText());
                feedItemsSummariesed.add(feedItemSummary);
            }
        }
        return feedItemsSummariesed;
    }


    public void transferToSavingsAccount(SavingAccountSummary savingsAccount, int savingsAddition, String accountUid,
                                         String transferId) {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",accessToken);

        Amount amount = new Amount(savingsAccount.getCurrency(),savingsAddition);
        AddToSavingsInfo addToSavingsInfo = new AddToSavingsInfo(amount);
        //build request
        HttpEntity<AddToSavingsInfo> request = new HttpEntity<>(addToSavingsInfo, headers);

        String url = rootPath+"/account/{"+accountUid+"}/savings-goals/{"+savingsAccount.getSavingsGoalUid()+"{/add-" +
                "money/{"+transferId+"}";

        // HTTP call
        ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.PUT, request,JsonNode.class);

//        response.getBody().get("success").asText()=="true";
        //return boolean of success message
    }

    public String getLastRoundUpTransferTimeStamp(String savingsGoalUid) {
        // /api/v2/feed/account/{}/category/{}?changesSince.. date of month previous
        // get last one
        //check if its 7 days or not

        //log if missed out one some
        return null;
    }

    public boolean checkIfRoundUpServicePushesBalanceIntoOverDraft(String accountUid, int savingsAddition) {
        // api/v2/accounts/{}/confirmation-of-funds?targetAmountInMinorUnits


        return false;
    }


}
