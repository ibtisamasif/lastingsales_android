package com.example.muzafarimran.lastingsales.Events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class SalesContactAddedEventModel {

    public SalesContactAddedEventModel() {
    }

    @Produce
    public SalesContactAddedEventModel geLastCallReceivedEvent() {
        return this;
    }
}
