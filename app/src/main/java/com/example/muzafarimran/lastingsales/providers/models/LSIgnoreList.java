package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

public class LSIgnoreList extends SugarRecord {

    private String number;

    public LSIgnoreList() {
    }

    public LSIgnoreList(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
