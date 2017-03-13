package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class IncomingCallEventModel {
    public static final int CALL_TYPE_INCOMING = 1;
    private int state;

    public IncomingCallEventModel(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Produce
    public IncomingCallEventModel geLastCallReceivedEvent() {
        return this;
    }
}