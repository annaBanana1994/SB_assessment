package com.starlingbank.assessment.utilities;

import com.starlingbank.assessment.model.NewSavingsGoalsAccountInfo;
import com.starlingbank.assessment.model.Target;


public class DefaultData {


    public final static String SAVINGS_GOALS_NAME="Future Adventures";

    //Currency should be worked out from default category accounts currency
    public final static String SAVINGS_GOALS_CURRENCY="GBP";

    public final static Target TARGET = new Target(SAVINGS_GOALS_CURRENCY,100000);

    public final static NewSavingsGoalsAccountInfo NEW_SAVINGS_GOALS_ACCOUNT_INFO = new NewSavingsGoalsAccountInfo(SAVINGS_GOALS_NAME,
            SAVINGS_GOALS_CURRENCY,TARGET,"string");

}
