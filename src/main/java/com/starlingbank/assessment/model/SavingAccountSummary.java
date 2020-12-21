package com.starlingbank.assessment.model;

import lombok.Data;

@Data
public class SavingAccountSummary {
    String savingsGoalUid;
    String name;
    String currency;
    int targetAmount;
    int savedAmount;
}
