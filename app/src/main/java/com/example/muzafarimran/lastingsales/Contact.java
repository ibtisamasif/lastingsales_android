package com.example.muzafarimran.lastingsales;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class Contact {

    private String name;
    private String number;
    private String tag;
    private String number_messages;
    private String number_calls;
    private String response_time;
    private String last_contact;

    //public Contact() {}

    public Contact(String n, String num, String tag, String number_calls, String number_messages, String response_time, String last_contact)
    {
        this.name            = n;
        this.number          = num;
        this.tag             = tag;
        this.number_calls    = number_calls;
        this.number_messages = number_messages;
        this.response_time   = response_time;
        this.last_contact    = last_contact;

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

    public String getNumber_messages() { return number_messages; }

    public String getNumber_calls() { return number_calls; }

    public String getResponse_time() { return response_time; }

    public String getLast_contact() { return last_contact; }


    @Override
    public int hashCode() {

        int result = name.hashCode();
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj){

        return (((Contact) obj).getTag().equals(this.tag) && ((Contact) obj).getName().equals(this.name));

    }
}
