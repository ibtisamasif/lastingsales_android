package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class PersonalContactAddedEventModel {

    public PersonalContactAddedEventModel() {
    }

    @Produce
    public PersonalContactAddedEventModel geLastCallReceivedEvent() {
        return this;
    }
}