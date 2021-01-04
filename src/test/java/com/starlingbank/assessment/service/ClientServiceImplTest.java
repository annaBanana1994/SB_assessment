package com.starlingbank.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.assessment.TestData;
import com.starlingbank.assessment.model.FeedItemSummary;
import com.starlingbank.assessment.model.SavingAccountSummary;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import com.starlingbank.assessment.model.payload.AddToSavingsInfo;
import com.starlingbank.assessment.model.payload.Amount;
import com.starlingbank.assessment.utilities.DefaultData;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ClientServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    ClientServiceImpl clientService;

    private ObjectMapper mapper = new ObjectMapper();

//    @Value("${user.directory}") // Would need testing profile
    private String directory=TestData.userDirectory;

    @BeforeEach
    public void setUp(){
        clientService.createAuthorizationValue("1234");
    }

    @Test
    public void getAccountHoldersAccounts_success() throws Exception {
        HttpEntity request = new HttpEntity(TestData.generateHeaders());

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts", HttpMethod.GET,
                request, Accounts.class)).thenReturn(new ResponseEntity(TestData.accountsList(), HttpStatus.OK));

        Accounts accounts = clientService.getAccountHoldersAccounts("1234");
        assertEquals("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc", accounts.getAccounts().get(0).getAccountUid());
    }
    @Test
    public void getAccountHoldersAccounts_success_withListOfAccounts() throws Exception {
        HttpEntity request = new HttpEntity(TestData.generateHeaders());

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts", HttpMethod.GET,
                request, Accounts.class)).thenReturn(new ResponseEntity(TestData.accountsBigList(), HttpStatus.OK));

        Accounts accounts = clientService.getAccountHoldersAccounts("1234");
        assertTrue(accounts.getAccounts().size()>1);
        assertEquals("1", accounts.getAccounts().get(0).getAccountUid());
        assertEquals("2", accounts.getAccounts().get(1).getAccountUid());
        assertEquals("3", accounts.getAccounts().get(2).getAccountUid());
    }
    @Test(expected = Exception.class)
    public void getAccountHoldersAccounts_fail() throws Exception {
        HttpEntity request = new HttpEntity(TestData.generateHeaders());

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts", HttpMethod.GET,
                request, Accounts.class)).thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));

        Accounts accounts = clientService.getAccountHoldersAccounts("1234");
    }

    @Test
    public void getSavingsAccount_success_nonDefaultSavings_createSavingsAcount() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"savings_list_one.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());
        JsonNode jsonNode2 = mapper.readTree(new File(directory+TestData.jsonDataPath+"createSavingsAccountMessage.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode2, HttpStatus.OK));

        JsonNode jsonNode3 = mapper.readTree(new File(directory+TestData.jsonDataPath+"savingsAccount.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals/1", HttpMethod.GET, getRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode3, HttpStatus.OK));


        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
    }

    @Test
    public void getSavingsAccount_success_nonDefaultSavings_BigList_createSavingsAcount() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"savings_list_three"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());
        JsonNode jsonNode2 = mapper.readTree(new File(directory+TestData.jsonDataPath+"createSavingsAccountMessage.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode2, HttpStatus.OK));

        JsonNode jsonNode3 = mapper.readTree(new File(directory+TestData.jsonDataPath+"savingsAccount.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals/1", HttpMethod.GET, getRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode3, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
    }
    @Test
    public void getSavingsAccount_success_defaultSavingsExist() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"FASavingsList"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
    }
    @Test
    public void getSavingsAccount_success_multipleDefaultSavingsExist_takeLastOne() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"/FASavingsList_multiple"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("3", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
    }
    @Test
    public void translateJsonNodeIntoSummary_allCorrectNodesRead() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"FASavingsList"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
        assertEquals("GBP", savingAccountSummary.getCurrency());
        assertEquals(123456, savingAccountSummary.getSavedAmount());
        assertEquals(123456, savingAccountSummary.getTargetAmount());
    }

    @Test
    public void getSavingsAccount_success_noSavingsAccounts_createSavingsAcount() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"savings_list_empty"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());
        JsonNode jsonNode2 = mapper.readTree(new File(directory+TestData.jsonDataPath+"createSavingsAccountMessage.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode2, HttpStatus.OK));

        JsonNode jsonNode3 = mapper.readTree(new File(directory+TestData.jsonDataPath+"savingsAccount.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals/1", HttpMethod.GET, getRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode3, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.getSavingsAccount("1","GBP");

        assertEquals("1", savingAccountSummary.getSavingsGoalUid());
        assertEquals("Future Adventures", savingAccountSummary.getName());
    }

    @Test (expected = Exception.class)
    public void createFutureAdventuresSavingsAccount_fail_unsuccesfulMessage() throws Exception {
        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());
        JsonNode jsonNode2 = mapper.readTree(new File(directory+TestData.jsonDataPath+"createSavingsAccountMessageFail.json"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(jsonNode2, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.createFutureAdventuresSavingsAccount("1","GBP");
    }

    @Test (expected = Exception.class)
    public void createFutureAdventuresSavingsAccount_fail_BadStatusCode() throws Exception {
        HttpEntity createRequest=new HttpEntity(TestData.generateNewSavingsAccount(), TestData.generateHeaders());

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals", HttpMethod.PUT, createRequest,
                JsonNode.class)).thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));

        clientService.createAuthorizationValue("1234");
        SavingAccountSummary savingAccountSummary = clientService.createFutureAdventuresSavingsAccount("1","GBP");
    }

    @Test
    public void getWeeksOutGoingTransactions_success_listOfTwo() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"weeksTransactions"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/feed/account/1/category/2/transactions-between?minTransactionTimestamp=1&maxTransactionTimestamp=5", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        List<FeedItemSummary> items= clientService.getWeeksOutGoingTransactions("1","2","1","5");
        assertTrue(items.size()==2);
        assertEquals("1",items.get(0).getFeedItemUid());
        assertEquals("2",items.get(1).getFeedItemUid());
    }
    @Test
    public void getWeeksOutGoingTransactions_success_onlyOneOut() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"weekTransactions_direction"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/feed/account/1/category/2/transactions-between?minTransactionTimestamp=1&maxTransactionTimestamp=5", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        List<FeedItemSummary> items= clientService.getWeeksOutGoingTransactions("1","2","1","5");
        assertTrue(items.size()==1);
    }
    @Test
    public void getWeeksOutGoingTransactions_success_ExtractValuesCorrectly() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"weekTransactions_direction"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/feed/account/1/category/2/transactions-between?minTransactionTimestamp=1&maxTransactionTimestamp=5", HttpMethod.GET,
                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        List<FeedItemSummary> items= clientService.getWeeksOutGoingTransactions("1","2","1","5");
        assertEquals("2",items.get(0).getFeedItemUid());
        assertEquals("OUT",items.get(0).getDirection());
        assertEquals(1234567,items.get(0).getAmount());
        assertEquals("GBP",items.get(0).getCurrency());
        assertEquals("ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd",items.get(0).getCategoryUid());
    }

    @Test
    public void transferToSavingsAccount() throws Exception {
        Amount amount = new Amount("GBP",100);
        AddToSavingsInfo addToSavingsInfo = new AddToSavingsInfo(amount);
        HttpEntity request = new HttpEntity(addToSavingsInfo,TestData.generateHeaders());

        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"savingsTransferSuccess"));

        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/account/1/savings-goals/bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc/add-money/aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa", HttpMethod.PUT,
                request, JsonNode.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        assertTrue(clientService.transferToSavingsAccount(TestData.getSavingsAccount(),100,"1", UUID.fromString("aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa")));
    }


    // Unsure how to ensure instant is the same for changeSince. Maybe Mock it?
//    @Test
//    public void getLastRoundUpTransferTimeStamp() throws IOException {
//        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
//        JsonNode jsonNode = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src" +
//                "/test/java/com/starlingbank/assessment/data/..."));
//        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/feed/account/1/category/2?changesSince=..", HttpMethod.GET,
//                getRequest, Object.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));
//
//        clientService.createAuthorizationValue("1234");
//        List<FeedItemSummary> items= clientService.getLastRoundUpTransferTimeStamp();
//
//    }

    @Test
    public void checkIfRoundUpServicePushesBalanceIntoOverDraft_success_false() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"fundsAvailableMessage"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts/1/confirmation-of-funds?targetAmountInMinorUnits=100", HttpMethod.GET,
                getRequest, JsonNode.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        assertFalse(clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft("1",100));
    }
    @Test
    public void checkIfRoundUpServicePushesBalanceIntoOverDraft_success_true() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"fundsNotAvailableMessage"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts/1/confirmation-of-funds?targetAmountInMinorUnits=100", HttpMethod.GET,
                getRequest, JsonNode.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.OK));

        clientService.createAuthorizationValue("1234");
        assertTrue(clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft("1",100));
    }
    @Test (expected = Exception.class)
    public void checkIfRoundUpServicePushesBalanceIntoOverDraft_fail() throws Exception {
        HttpEntity getRequest = new HttpEntity(TestData.generateHeaders());
        JsonNode jsonNode = mapper.readTree(new File(directory+TestData.jsonDataPath+"fundsNotAvailableMessage"));
        when(restTemplate.exchange(DefaultData.ROOT_PATH+"/accounts/1/confirmation-of-funds?targetAmountInMinorUnits=100", HttpMethod.GET,
                getRequest, JsonNode.class)).thenReturn(new ResponseEntity(jsonNode, HttpStatus.BAD_REQUEST));

        clientService.createAuthorizationValue("1234");
        clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft("1",100);
    }
}