package com.example.muzafarimran.lastingsales.providers.models;

@Deprecated
public class Call {
    private int id;
    private String contact_number;
    private int contact_id;
    private String type;
    private String duration;
    private String begin_time;
    private String audio_path;

    // public constructor to create an object.
    public Call(String contact_number, int contact_id, String type, String duration,
                String begin_time, String audio_path) {
        //TODO: Assign id here too
        this.contact_number = contact_number;
        this.contact_id = contact_id;
        this.type = type;
        this.duration = duration;
        // could be current time
        this.begin_time = begin_time;
        this.audio_path = audio_path;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }
}
