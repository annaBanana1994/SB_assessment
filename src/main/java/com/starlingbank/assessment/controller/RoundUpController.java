package com.starlingbank.assessment.controller;

import com.starlingbank.assessment.model.response.RoundUpResponseMessage;
import com.starlingbank.assessment.service.ClientServiceImpl;
import com.starlingbank.assessment.service.RoundUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/round_up")
public class RoundUpController {

    private static final Logger LOGGER= LoggerFactory.getLogger(RoundUpController.class);

    // Add weekly automated start

    @Autowired
    RoundUpService roundUpService;

    @PostMapping (path= "/transfer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RoundUpResponseMessage> roundUp(@RequestBody String accountHolderAccessToken){
        try {
            LOGGER.info("Round up Service end point hit");
            return new ResponseEntity<>(roundUpService.transferWeeksSavings(accountHolderAccessToken), HttpStatus.OK);
        }
            //add more specific exception to provide further details server error, bad request
        catch (Exception e) {
            LOGGER.warn("Exception thrown.");
            LOGGER.info(e.getMessage());
            RoundUpResponseMessage response = new RoundUpResponseMessage();
            response.setSuccessfulTransfer(false);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.EXPECTATION_FAILED);
        }
    }
}
