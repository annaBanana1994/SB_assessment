package com.starlingbank.assessment.controller;

import com.starlingbank.assessment.TestData;
import com.starlingbank.assessment.service.RoundUpServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
class RoundUpControllerTest {

    @InjectMocks
    RoundUpController roundUpController;

    @Mock
    RoundUpServiceImpl roundUpService;

    @Test
    public void controllerPresent(){
        assertThat(roundUpController).isNotNull();
    }

    @Test
    public void roundUp_success() throws Exception {
        when(roundUpService.transferWeeksSavings(isA(String.class))).thenReturn(TestData.getRoundUpResponse_success());

        ResponseEntity<?> responseEntity = roundUpController.roundUp("1234");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        //TODO Add success part of string and transferUid
        assertTrue(responseEntity.getBody().toString().contains("true"));
        assertTrue(responseEntity.getBody().toString().contains("12345"));
    }

    @Test
    public void roundUp_ExceptionThrown() throws Exception {
        when(roundUpService.transferWeeksSavings(isA(String.class))).thenThrow(Exception.class);

        ResponseEntity<?> responseEntity = roundUpController.roundUp("1234");
        assertEquals(HttpStatus.EXPECTATION_FAILED, responseEntity.getStatusCode());
        //TODO Add success part of string and transferUid
        assertTrue(responseEntity.getBody().toString().contains("false"));
    }
}