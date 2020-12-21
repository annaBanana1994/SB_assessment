package com.starlingbank.assessment.model;

import lombok.Data;

@Data
public class FeedItemSummary {
    String feedItemUid;
    String categoryUid;
    String currency;
    int amount;
    String direction;
}
