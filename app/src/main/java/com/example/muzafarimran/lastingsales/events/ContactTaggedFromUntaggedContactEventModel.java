package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class ContactTaggedFromUntaggedContactEventModel {
    public ContactTaggedFromUntaggedContactEventModel() {
    }

    @Produce
    public ContactTaggedFromUntaggedContactEventModel geLastCallReceivedEvent() {
        return this;
    }
}
