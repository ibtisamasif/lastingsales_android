package com.example.muzafarimran.lastingsales.utilscallprocessing;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;

public class CallTypeManager {
    public String getCallType(String callType, String callDuration) {
        Long callDurationLong = Long.parseLong(callDuration);
        if (callType.equals("1") && callDurationLong > 0L) {           //Incoming
            return LSCall.CALL_TYPE_INCOMING;
        } else if (callType.equals("2") && callDurationLong == 0L) {  //Outgoing UnAnswered
            return LSCall.CALL_TYPE_UNANSWERED;
        } else if (callType.equals("2")) {      //Outgoing Answered
            return LSCall.CALL_TYPE_OUTGOING;
        } else if (callType.equals("3")) {      //Missed
            return LSCall.CALL_TYPE_MISSED;
        } else if (callType.equals("5") || callType.equals("1") || callType.equals("10") && callDurationLong == 0L) {        // Incoming Rejected //TODO fix it
            return LSCall.CALL_TYPE_REJECTED;
        } else if (callType.equals("1") && callDurationLong == 0L) { //TODO fix it and convert it into 1 check later.
            return LSCall.CALL_TYPE_REJECTED;
        }else {
            return null;
        }
    }
}
