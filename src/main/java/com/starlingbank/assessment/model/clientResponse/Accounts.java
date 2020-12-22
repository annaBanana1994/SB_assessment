package com.starlingbank.assessment.model.clientResponse;

import com.starlingbank.assessment.model.Account;
import lombok.Data;

import java.util.List;

@Data
public class Accounts {
    List<Account> accounts;
}
