package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSCall extends SugarRecord {
    @Ignore
    public static final String CALL_TYPE_OUTGOING = "outgoing";
    @Ignore
    public static final String CALL_TYPE_INCOMING = "incoming";
    @Ignore
    public static final String CALL_TYPE_MISSED = "missed";
    private String contactNumber;
    private LSContact contact;
    private String type;
    private Long duration;
    private String contactName;
    private Long beginTime;
    private String audio_path;

    public LSCall() {
    }

    public static ArrayList<LSCall> getCallsFromNumber(String number) {
        ArrayList<LSCall> list = null;
        try {
            list = (ArrayList<LSCall>) LSContact.find(LSCall.class, "contact_number = ? ", number);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public static ArrayList<LSCall> getUniqueCallsWithoutContacts() {
        ArrayList<LSCall> untaggedCalls = new ArrayList<>();
        ArrayList<LSCall> allCalls = null;
        allCalls = (ArrayList<LSCall>) listAll(LSCall.class);
//        calls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact").eq("null")).orderBy("contact_number DESC").list();
//        calls = (ArrayList<LSCall>) LSCall.findWithQuery(LSCall.class, "SELECT * from LS_CALL ");
//        calls = (ArrayList<LSCall>) LSCall.findWithQuery(LSCall.class, "SELECT * from LS_CALL where contact = null GROUP BY contact_number");
//        calls = (ArrayList<LSCall>) LSCall.find(LSCall.class,"contact = ?","null");
        for (LSCall oneCall : allCalls) {
            if (oneCall.getContact() == null && !oneCall.getType().equals(LSCall.CALL_TYPE_MISSED)) {
                untaggedCalls.add(oneCall);
            }
        }
        /*for (LSCall oneCall : calls) {
            LSCall.
        }*/

        return untaggedCalls;
    }

    public static List<LSCall> getCallsByType(String type) {
        return LSCall.find(LSCall.class, "type = ? ", type);
    }

    public static List<LSCall> getCallsByTypeInDescendingOrder(String type) {
        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("type").eq(type)).orderBy("begin_time DESC").list();
        return allCalls;
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

    public static List<LSCall> getAllCallsInDescendingOrder() {

        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).orderBy("begin_time DESC").list();
        return allCalls;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}