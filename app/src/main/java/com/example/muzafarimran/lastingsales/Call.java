package com.example.muzafarimran.lastingsales;

/**
 * Created by MUZAFAR IMRAN on 9/19/2016.
 */
public class Call {
    private String name;
    private String number;
    private String type;
    private String time;

    public Call()
    {}

    public Call(String n, String num, String ty, String t)
    {
        this.name = n;
        this.number = num;
        this.type = ty;
        this.time = t;
    }

    public String getName(){
        return this.name;
    }

    public String getNumber(){
        return this.number;
    }

    public String getType(){
        return this.type;
    }

    public String getTime() {
        return this.time;
    }
}
