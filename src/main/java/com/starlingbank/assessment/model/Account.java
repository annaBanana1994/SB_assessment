package com.starlingbank.assessment.model;

import lombok.Data;

@Data
public class Account {

    String accountUid;
    String accountType;
    String defaultCategory;
    String currency;
    String createdAt;
    String name;
}
