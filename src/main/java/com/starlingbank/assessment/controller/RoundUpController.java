package com.starlingbank.assessment.controller;

import com.starlingbank.assessment.service.RoundUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/round_up")
public class RoundUpController {

    // Add weekly automated start

    @Autowired
    RoundUpService roundUpService;

    @PostMapping (path= "round_up_transfer/{accountHolderAccessToken}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> roundUp(@PathVariable("accountHolderAccessToken") String accountHolderAccessToken){
                    try {
                return new ResponseEntity<>(roundUpService.transferWeeksSavings(accountHolderAccessToken), HttpStatus.OK);
            }
            //TODO add further exception handlings ie. server error, bad request, database error
                catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

}
