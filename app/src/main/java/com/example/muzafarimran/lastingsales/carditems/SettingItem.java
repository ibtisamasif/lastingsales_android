package com.example.muzafarimran.lastingsales.carditems;

/**
 * Created by ibtisam on 11/8/2017.
 */

public class SettingItem {
    public int drawable = 0;
    public String text;
    public Class goAt = null;

    public SettingItem(String text, Class goAt, int drawable) {
        this.drawable = drawable;
        this.text = text;
        this.goAt = goAt;
    }
}
