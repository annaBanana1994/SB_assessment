package com.starlingbank.assessment.stub;

import com.starlingbank.assessment.model.Account;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ClientStub {

    public Accounts accountsList(){
        Accounts accountsModel = new Accounts();
        List<Account> accounts = new ArrayList<>();
        Account account = new Account("bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc","PRIMARY", "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd",
                "GBP", "2021-01-03T18:36:27.383Z","Personal");
        accounts.add(account);
        accountsModel.setAccounts(accounts);
        return accountsModel;
    }

    //{
    //  "accounts": [
    //    {
    //      "accountUid": "bbccbbcc-bbcc-bbcc-bbcc-bbccbbccbbcc",
    //      "accountType": "PRIMARY",
    //      "defaultCategory": "ccddccdd-ccdd-ccdd-ccdd-ccddccddccdd",
    //      "currency": "GBP",
    //      "createdAt": "2021-01-03T18:36:27.383Z",
    //      "name": "Personal"
    //    }
    //  ]
    //}

}
