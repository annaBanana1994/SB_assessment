package com.starlingbank.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Target {
    String currency;
    int minorUnits;
}
