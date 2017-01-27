package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ibtisam on 1/24/2017.
 */

public class LeadContactDeletedEventModel {
    public LeadContactDeletedEventModel() {
    }

    @Produce
    public LeadContactDeletedEventModel geLastCallReceivedEvent() {
        return this;
    }
}
