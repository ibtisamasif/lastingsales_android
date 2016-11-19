package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSCall extends SugarRecord {
    private String contactNumber;
    private LSContact contact;
    private String type;
    private String duration;
    private Long beginTime;
    private String audio_path;

    @Ignore
    public static final String CALL_TYPE_OUTGOING = "outgoing";
    @Ignore
    public static final String CALL_TYPE_INCOMING = "incoming";
    @Ignore
    public static final String CALL_TYPE_MISSED = "missed";


    public static List<LSCall> getCallsByType(String type) {
        return LSCall.find(LSCall.class, "type = ? ", type);
    }



    /*

    // COMPARATORS

    public static Comparator<LSCall> ASCENDING_COMPARE_BY_DATA_DOUBLE = new Comparator<AnalysisCell>() {
        public int compare(AnalysisCell one, AnalysisCell other) {

            try {
                Double doubleOne = Double.parseDouble(one.getData());
                Double doubleOther = Double.parseDouble(other.getData());
                return doubleOne.compareTo(doubleOther);
            }
            catch (NumberFormatException e)
            {
                return  0;
            }

        }
    };


    public static Comparator<AnalysisCell> DESCENDING_COMPARE_BY_DATA_DOUBLE = new Comparator<AnalysisCell>() {
        public int compare(AnalysisCell one, AnalysisCell other) {

            try {
                Double doubleOne = Double.parseDouble(one.getData());
                Double doubleOther = Double.parseDouble(other.getData());
                return doubleOther.compareTo(doubleOne);
            }
            catch (NumberFormatException e)
            {
                return  0;
            }
        }
    };

*/

    public LSCall() {
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LSContact getContact() {
        return contact;
    }

    public void setContact(LSContact contact) {
        this.contact = contact;
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

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }
}
