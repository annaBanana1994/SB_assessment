package com.starlingbank.assessment.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewSavingsGoalsAccountInfo {
    String name;
    String currency;
    Target target;
    String base64EncodedPhoto;
}
