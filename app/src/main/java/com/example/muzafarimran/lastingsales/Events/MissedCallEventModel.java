package com.example.muzafarimran.lastingsales.Events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class MissedCallEventModel {

    public static final int CALL_TYPE_MISSED = 0;
    public static final int CALL_TYPE_INCOMING = 1;
    public static final int CALL_TYPE_OUTGOING = 2;

    private int state;

    public MissedCallEventModel(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Produce
    public MissedCallEventModel geLastCallReceivedEvent() {
        return this;
    }
}