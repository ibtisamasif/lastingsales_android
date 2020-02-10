package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class OutgoingCallEventModel {


    public static final int CALL_TYPE_OUTGOING = 2;

    private int state;

    public OutgoingCallEventModel(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Produce
    public OutgoingCallEventModel geLastCallReceivedEvent() {
        return this;
    }
}