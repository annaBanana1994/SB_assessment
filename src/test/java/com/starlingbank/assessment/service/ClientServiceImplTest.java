package com.starlingbank.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.assessment.data.TestData;
import com.starlingbank.assessment.model.SavingAccountSummary;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import com.starlingbank.assessment.utilities.DefaultData;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClientServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    ClientServiceImpl clientService;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp(){
        clientService.createAuthorizationValue("1234");
    }

    @Test
    public void getAccountHoldersAccounts_success() throws Exception {
        HttpEntity request = new HttpEntity(TestData.generateHeaders());

        // Should be able to translate json file straight in
//        JsonNode jsonNode = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src/test/java/com/starlingbank/assessment/data/savings_list_one.json"));

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts", HttpMethod.GET,
                request, Accounts.class)).thenReturn(new ResponseEntity(TestData.accountsList(), HttpStatus.OK));

        Accounts accounts = clientService.getAccountHoldersAccounts("1234");
        Assert.assertEquals("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc", accounts.getAccounts().get(0).getAccountUid());
    }
    //TODO
    // 1 list of two
    // 2 fail

    //create success
    @Test
    public void getSavingsAccount_success() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src" +
                "/test/java/com/starlingbank/assessment/data/savings_list_one.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());
        JsonNode jsonNode2 = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src" +
                "/test/java/com/starlingbank/assessment/data/createSavingsAccountMessage.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode2, HttpStatus.OK));

        JsonNode jsonNode3 = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src" +
                "/test/java/com/starlingbank/assessment/data/savingsAccount.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals/1", HttpMethod.GET, getRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode3, HttpStatus.OK));

        //TODO
        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        Assert.assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        Assert.assertEquals("Future Adventures", savingAccountSummary.getName());
    }

    //TODO
    // 3 savings but not DefaultData.SAVINGS_GOALS_NAME
    // 4 savings list with multiple DefaultData.SAVINGS_GOALS_NAME
    // 5 Test all transactions in assert
    //        SavingAccountSummary savingAccountSummary = new SavingAccountSummary();
    //        savingAccountSummary.setCurrency(jsonNode.get("target").get("currency").asText());
    //        savingAccountSummary.setName(jsonNode.get("name").asText());
    //        savingAccountSummary.setSavedAmount(jsonNode.get("totalSaved").get("minorUnits").asInt());
    //        savingAccountSummary.setSavingsGoalUid(jsonNode.get("savingsGoalUid").asText());
    //        savingAccountSummary.setTargetAmount(jsonNode.get("target").get("minorUnits").asInt());


    //createFutureAdventuresSavingsAccount
//        if(response.getStatusCode() == HttpStatus.OK&&response.getBody().get("success").asBoolean()==true) {
//        LOGGER.debug("Savings account successfully created");
//        String savingsGoalUid=response.getBody().get("savingsGoalUid").asText();
//        savingAccountSummary=getSavingAccount(savingsGoalUid,accountUid);
    @Test
    // 6 do a success
    // 7 response.getBody().get("success").asBoolean()==true // response false
    // 8 do a fail
    // 9 assert the summary
    public void createFutureAdventuresSavingsAccount() {

    }


    //getSavingAccount
        // if (response.getStatusCode()!= HttpStatus.OK){
        //                LOGGER.warn("Client response status: "+response.getStatusCode());
        //                throw new Exception(DefaultData.EXTERNAL_CALL_ERROR_MESSAGE + response.getStatusCode());
        //            }
        //            SavingAccountSummary newSavingAccountSummary = translateJsonNodeIntoSummary(response.getBody());
        //
        //            return newSavingAccountSummary;
    @Test
    //10 -do a fail
    // 11 verify translate called
    // 12 and asssert the summary
    public void getSavingAccount() {
    }



    @Test
    public void translateJsonNodeIntoSummary() {
    }

    @Test
    public void getWeeksOutGoingTransactions() {
    }

    @Test
    public void transferToSavingsAccount() {
    }

    @Test
    public void getLastRoundUpTransferTimeStamp() {
    }

    @Test
    public void checkIfRoundUpServicePushesBalanceIntoOverDraft() {
    }
}