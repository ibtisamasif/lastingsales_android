package com.example.muzafarimran.lastingsales.carditems;

/**
 * Created by ibtisam on 11/8/2017.
 */

public class SettingItem {
    public int drawable;
    public String text;
    public Class goAt = null;

    public SettingItem(String setting) {
        text = setting;
    }

    public SettingItem(String setting, Class goAt) {
        text = setting;
        this.goAt = goAt;
    }
}
