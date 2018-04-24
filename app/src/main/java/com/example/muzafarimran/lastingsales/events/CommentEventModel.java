package com.example.muzafarimran.lastingsales.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ibtisam on 2/5/2018.
 */

public class CommentEventModel {

    public CommentEventModel() {
    }

    @Produce
    public CommentEventModel geLastCallReceivedEvent(){
        return this;
    }

}
