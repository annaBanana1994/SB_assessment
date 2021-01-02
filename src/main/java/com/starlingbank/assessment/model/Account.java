package com.starlingbank.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    String accountUid;
    String accountType;
    String defaultCategory;
    String currency;
    String createdAt;
    String name;
}
