package com.starlingbank.assessment.service;

import com.starlingbank.assessment.data.TestData;
import com.starlingbank.assessment.model.SavingAccountSummary;
import com.starlingbank.assessment.model.response.RoundUpResponseMessage;
import com.starlingbank.assessment.utilities.DefaultData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class RoundUpServiceImplTest {

    @InjectMocks
    RoundUpServiceImpl roundUpService;

    @Mock
    ClientServiceImpl clientService;

    @Before
    public void mockedResponses() throws Exception {
        when(clientService.getAccountHoldersAccounts(isA(String.class))).thenReturn(TestData.getAccountHoldersAccounts_one());
        when(clientService.getSavingsAccount(isA(String.class), isA(String.class))).thenReturn(TestData.getSavingsAccount());
        when(clientService.getLastRoundUpTransferTimeStamp(isA(String.class), isA(String.class), isA(Instant.class))).thenReturn("2020-01-02T15:04:55.599Z");
        when(clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft(isA(String.class), isA(Integer.class))).thenReturn(false);
        when(clientService.transferToSavingsAccount(isA(SavingAccountSummary.class), isA(Integer.class), isA(String.class), isA(UUID.class))).thenReturn(true);
        when(clientService.getWeeksOutGoingTransactions(isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(TestData.getFeedItems());
    }

    @Test
    public void transferWeeksSavings_success_fundsAvailable() throws Exception {
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getInOverdraft());
        assertTrue(responseMessage.getMessage().contains("Round up service transfer status: Funds available"));
        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
    }

    @Test
    public void transferWeeksSavings_success_fundsNotAvailable() throws Exception {
        when(clientService.checkIfRoundUpServicePushesBalanceIntoOverDraft(isA(String.class), isA(Integer.class))).thenReturn(true);
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertTrue(responseMessage.getInOverdraft());
        assertTrue(responseMessage.getMessage().contains("Round up service transfer status: In overdraft"));
        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
    }

    @Test
    public void transferWeeksSavings_success_withTwoAccounts() throws Exception {
        when(clientService.getAccountHoldersAccounts(isA(String.class))).thenReturn(TestData.getAccountHoldersAccounts_two());
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getInOverdraft());
        assertTrue(responseMessage.getMessage().contains("Round up service transfer status: Funds available"));
        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
    }

    @Test
    public void transferWeeksSavings_success_withMultipleAccounts() throws Exception {
        when(clientService.getAccountHoldersAccounts(isA(String.class))).thenReturn(TestData.getAccountHoldersAccounts_four());
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getInOverdraft());
        assertTrue(responseMessage.getMessage().contains("Round up service transfer status: Funds available"));
        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
    }

    @Test
    public void transferWeeksSavings_message_defaultAccountCheckIsTrue() throws Exception {
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getMessage().contains("The account holders details aren't able to use the Round up Service"));
    }

    @Test
    public void transferWeeksSavings_message_noPreviousSavingsTransfers() throws Exception {
        when(clientService.getLastRoundUpTransferTimeStamp(isA(String.class), isA(String.class), isA(Instant.class))).thenReturn(null);

        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertTrue(responseMessage.getMessage().contains("No previous transaction record for the savings account"));
        //No previous transaction record for the savings accountRound up service transfer status: Funds available
    }

    @Test
    public void addToMessage_successful_lineBreakBetweenMessages() throws Exception {
        when(clientService.getLastRoundUpTransferTimeStamp(isA(String.class), isA(String.class), isA(Instant.class))).thenReturn(null);
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertTrue(responseMessage.getMessage().contains(DefaultData.LINE_BREAK));
        assertEquals("No previous transaction record for the savings account\nRound up service transfer status: Funds available", responseMessage.getMessage());
    }

    @Test
    public void addToMessage_successful_oneMessage_noLineBreak() throws Exception {
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getMessage().contains(DefaultData.LINE_BREAK));
    }

    @Test
    public void calculatingWeeklySavings_success_3transactions() throws Exception {
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
        assertEquals(60, responseMessage.getPotentialSavings());
    }

    @Test
    public void calculatingWeeklySavings_success_6transactions() throws Exception {
        when(clientService.getWeeksOutGoingTransactions(isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(TestData.getBiggerFeedItems());
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertTrue(responseMessage.getSuccessfulTransfer());
        assertNotNull(responseMessage.getTransferUid());
        assertEquals(120, responseMessage.getPotentialSavings());
    }

    @Test
    public void calculatingWeeklySavings_success_0transactions() throws Exception {
        when(clientService.getWeeksOutGoingTransactions(isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(TestData.getEmptyList());
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");

        assertFalse(responseMessage.getSuccessfulTransfer());
        assertNull(responseMessage.getTransferUid());
        assertEquals(0, responseMessage.getPotentialSavings());
        assertTrue(responseMessage.getMessage().contains(DefaultData.NO_OUTGOING_TRANSACTIONS));
    }
    @Test(expected = Exception.class)
    public void calculatingWeeklySavings_fail() throws Exception {
        when(clientService.getWeeksOutGoingTransactions(isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenThrow(Exception.class);
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");
    }

    @Test(expected = Exception.class)
    public void transferWeeksSavings_fail_accountHoldersAccessTokenError() throws Exception {
        when(clientService.getAccountHoldersAccounts(isA(String.class))).thenThrow(Exception.class);
        RoundUpResponseMessage responseMessage = roundUpService.transferWeeksSavings("1234");
    }
}
