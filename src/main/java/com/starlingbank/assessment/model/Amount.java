package com.starlingbank.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Amount {
    String currency;
    int minorUnits;
}
