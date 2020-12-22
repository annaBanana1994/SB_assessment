package com.starlingbank.assessment.utilities;

import com.starlingbank.assessment.model.payload.NewSavingsGoalsAccountInfo;
import com.starlingbank.assessment.model.payload.Target;


public class DefaultData {

    public final static String AUTHORIZATION_NAME ="Bearer ";

    public final static String SAVINGS_GOALS_NAME="Future Adventures";

    //Currency should be worked out from default category accounts currency
    public final static String SAVINGS_GOALS_CURRENCY="GBP";

    public final static Target TARGET = new Target(SAVINGS_GOALS_CURRENCY,100000);

    public final static String SAVINGS_GOALS_PHOTO ="string";

    public final static NewSavingsGoalsAccountInfo NEW_SAVINGS_GOALS_ACCOUNT_INFO = new NewSavingsGoalsAccountInfo(SAVINGS_GOALS_NAME,
            SAVINGS_GOALS_CURRENCY,TARGET,SAVINGS_GOALS_PHOTO );

    public final static String CHECK_ACCOUNT_FAIL = "The account holders details aren't able to use the Round up Service";

    public final static String OVER_A_WEEK ="The last round up transfer into this savings account was over a week ago";

    public final static String LINE_BREAK ="/n";

    public final static String UUID_INIT="c81d4e2e-bcf2-11e6-869b-7df92533d2db";

    public final static String OVERDRAFT_STATUS="Round up service transfer status: ";
    public final static String IN_OVERDRAFT_TRUE ="In overdraft";
    public final static String IN_OVERDRAFT_False="Funds available";

    public final static String EXTERNAL_CALL_ERROR_MESSAGE=" Client API status code: ";

}
