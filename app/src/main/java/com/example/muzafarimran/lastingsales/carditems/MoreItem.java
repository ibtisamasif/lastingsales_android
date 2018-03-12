package com.example.muzafarimran.lastingsales.carditems;

/**
 * Created by ibtisam on 11/16/2017.
 */

public class MoreItem {

    public int drawable;
    public String text;
    public String description;
    public Class goAt = null;

    public MoreItem(String setting) {
        text = setting;
    }

    public MoreItem(String setting, Class goAt) {
        text = setting;
        this.goAt = goAt;
    }
}
