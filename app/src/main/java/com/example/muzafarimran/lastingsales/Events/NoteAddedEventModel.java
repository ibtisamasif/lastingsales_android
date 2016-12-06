package com.example.muzafarimran.lastingsales.Events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ahmad on 10-Nov-16.
 */

public class NoteAddedEventModel {


    public NoteAddedEventModel() {

    }

    @Produce
    public NoteAddedEventModel geLastCallReceivedEvent() {
        return this;
    }
}