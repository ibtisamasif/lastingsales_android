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

    @Ignore
    public static final String CALL_TYPE_REJECTED = "rejected";
    @Ignore
    public static final String CALL_TYPE_UNANSWERED = "unanswered";

    @Ignore
    public static final int INQUIRY_HANDLED = 1;
    @Ignore
    public static final int INQUIRY_NOT_HANDLED = 0;

    private String contactNumber;
    private LSContact contact;
    private String type;
    private Long duration;
    private String contactName;
    private Long beginTime;
    private int inquiryHandledState;
    @Ignore
    private int countOfInquiries;
    private String serverId;
    private String callLogId;
    private String syncStatus;
    private String audioPath;

    public LSCall() {
    }

    public static LSCall getCallHavingLatestCallLogId() {
        ArrayList<LSCall> list = null;
        try {
            list = (ArrayList<LSCall>) LSCall.find(LSCall.class, null, null, null, "call_log_id DESC", "1");
//            list = (ArrayList<LSCall>) LSCall.findWithQuery(LSCall.class, "SELECT MAX(call_log_id) FROM LS_CALL");
//            list = (ArrayList<LSCall>) LSCall.find(LSCall.class, "call_log_id = ?", "MAX");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static boolean ifExist(String id) {
        ArrayList<LSCall> list = null;
        try {
            list = (ArrayList<LSCall>) LSCall.find(LSCall.class, "call_log_id = ?", id);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
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

    public static List<LSCall> getArrangedUniqueCallsWithUnlabeledContacts() {
        List<LSCall> unlabeledCalls = new ArrayList<>();
        List<LSCall> allArrangedCalls = getAllCallsInDescendingOrder();
        for (LSCall oneCall : allArrangedCalls) {
            if (oneCall.getContact() != null && !oneCall.getContact().getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                unlabeledCalls.add(oneCall);
            }
        }
        /*for (LSCall oneCall : calls) {
            LSCall.
        }*/

        return unlabeledCalls;
    }

    @Deprecated
    public static List<LSCall> getCallsByType(String type) {
        return LSCall.find(LSCall.class, "type = ? ", type);
    }

    @Deprecated
    public static List<LSCall> getCallsByInquiryHandledStateInDescendingOrder(int state) {
        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("inquiry_handled_state").eq(state)).orderBy("begin_time DESC").list();
        return allCalls;
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
    public static ArrayList<LSCall> getAllUniqueCallsInDescendingOrder() {

        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) LSCall.findWithQuery(LSCall.class, "Select DISTINCT contact_number from LS_CALL ORDER BY begin_time DESC");
//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>)LSCall.findWithQuery(LSCall.class, "Select * from LS_CALL_RECORDING where sync_status = 'recording_not_synced' and server_id_of_call IS NOT NULL");
//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).orderBy("begin_time DESC").list();
        return allCalls;
    }

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

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String path) {
        this.audioPath = path;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getInquiryHandledState() {
        return inquiryHandledState;
    }

    public void setInquiryHandledState(int inquiryHandledState) {
        this.inquiryHandledState = inquiryHandledState;
    }

    public int getCountOfInquiries() {
        return countOfInquiries;
    }

    public void setCountOfInquiries(int countOfInquiries) {
        this.countOfInquiries = countOfInquiries;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getCallLogId() {
        return callLogId;
    }

    public void setCallLogId(String callLogId) {
        this.callLogId = callLogId;
    }

    @Override
    public String toString() {
        return "LSCall{" +
                "contactNumber='" + contactNumber + '\'' +
                ", contact=" + contact +
                ", type='" + type + '\'' +
                ", duration=" + duration +
                ", contactName='" + contactName + '\'' +
                ", beginTime=" + beginTime +
                ", inquiryHandledState=" + inquiryHandledState +
                ", countOfInquiries=" + countOfInquiries +
                ", serverId='" + serverId + '\'' +
                ", callLogId='" + callLogId + '\'' +
                ", syncStatus='" + syncStatus + '\'' +
                ", audioPath='" + audioPath + '\'' +
                '}';
    }
}