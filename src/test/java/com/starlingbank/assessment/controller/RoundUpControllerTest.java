package com.starlingbank.assessment.controller;

import com.starlingbank.assessment.data.TestData;
import com.starlingbank.assessment.service.RoundUpServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class RoundUpControllerTest {


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
            assertTrue(responseEntity.getBody().toString().contains("successfulTransfer=true"));
            assertTrue(responseEntity.getBody().toString().contains("transferUid=c6a8669e-ee95-4c42-9ef6-4a9b61380164"));
        }

        @Test
        public void roundUp_ExceptionThrown() throws Exception {
            when(roundUpService.transferWeeksSavings(isA(String.class))).thenThrow(Exception.class);

            ResponseEntity<?> responseEntity = roundUpController.roundUp("1234");
            assertEquals(HttpStatus.EXPECTATION_FAILED, responseEntity.getStatusCode());
            assertTrue(responseEntity.getBody().toString().contains("successfulTransfer=false"));
            assertTrue(responseEntity.getBody().toString().contains("transferUid=null"));
        }

}