package com.example.muzafarimran.lastingsales;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class Contact {
    private String name;
    private String number;
    private String tag;

    public Contact()
    {}

    public Contact(String n, String num, String tag)
    {
        this.name = n;
        this.number = num;
        this.tag = tag;

    }

    public String getName(){
        return this.name;
    }

    public String getNumber(){
        return this.number;
    }

    public String getTag(){
        return this.tag;
    }


    @Override
    public int hashCode() {

        int result = name.hashCode();
       // result = 31 * result + number.hashCode();
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj){

        return (((Contact) obj).getTag().equals(this.tag) && ((Contact) obj).getName().equals(this.name));

    }
}
