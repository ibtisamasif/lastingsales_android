package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;

public class LSIgnoreList extends SugarRecord {

    private String number;

    public LSIgnoreList() {
    }

    public LSIgnoreList(String number) {
        this.number = number;
    }

    public static LSIgnoreList getContactFromNumber(String number) {
        ArrayList<LSIgnoreList> list = null;
        try {
            list = (ArrayList<LSIgnoreList>) LSIgnoreList.find(LSIgnoreList.class, "number = ? ", number);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
