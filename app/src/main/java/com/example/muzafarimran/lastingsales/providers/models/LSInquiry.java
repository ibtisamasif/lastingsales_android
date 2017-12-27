package com.example.muzafarimran.lastingsales.providers.models;

import android.util.Log;

import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ibtisam on 2/1/2017.
 */

public class LSInquiry extends SugarRecord {

    private static final String TAG = "LSInquiry";
    @Ignore
    public static final String INQUIRY_STATUS_PENDING = "pending";
    @Ignore
    public static final String INQUIRY_STATUS_ATTENDED = "attended";

    private String contactName;
    private String contactNumber;
    private LSContact contact;
    private Long beginTime;
    private Long duration; // TODO useless field remove it.
    private int countOfInquiries;
    private String syncStatus;
    private String serverId;
    private Long averageResponseTime;
    private String status;
    private LSContactProfile contactProfile;

    public LSInquiry() {
    }

    public static List<LSInquiry> getAllPendingInquiriesInDescendingOrder() {
        try {
            ArrayList<LSInquiry> allInquiries = (ArrayList<LSInquiry>) Select.from(LSInquiry.class).where(Condition.prop("status").eq(LSInquiry.INQUIRY_STATUS_PENDING)).orderBy("begin_time DESC").list();
            return allInquiries;
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static List<LSInquiry> getAllInquiriesInDescendingOrder() {
        try {
            ArrayList<LSInquiry> allInquiries = (ArrayList<LSInquiry>) Select.from(LSInquiry.class).orderBy("begin_time DESC").list();
            return allInquiries;
        } catch (Exception e) {
            return null;
        }
    }

    public static LSInquiry getPendingInquiryByNumberIfExists(String number) {
        ArrayList<LSInquiry> list = null;
        try {
            list = (ArrayList<LSInquiry>) LSInquiry.find(LSInquiry.class, "contact_number = ? and status = ? ", number, LSInquiry.INQUIRY_STATUS_PENDING);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static LSInquiry getPendingInquiryByBeginDateTimeIfExists(String beginTimeMillis) {
        ArrayList<LSInquiry> list = null;
        ArrayList<LSInquiry> matchedList = new ArrayList<>();
        try {
            //TODO Get All pending inquiries then match with each inquiry.

            list = (ArrayList<LSInquiry>) LSInquiry.find(LSInquiry.class, "status = ? ", LSInquiry.INQUIRY_STATUS_PENDING);
            if (list != null) {
                for (LSInquiry oneInquiry : list) {
                    if (PhoneNumberAndCallUtils.compareDateTimeInMillis(Long.valueOf(beginTimeMillis), oneInquiry.getBeginTime())) {
                        Log.d(TAG, "getPendingInquiryByBeginDateTimeIfExists: MATCHED");
                        matchedList.add(oneInquiry);
                        break;
                    } else {
                        Log.d(TAG, "getPendingInquiryByBeginDateTimeIfExists: NOT MATCHED");
                    }
                }
            }

//            list = (ArrayList<LSInquiry>) LSInquiry.find(LSInquiry.class, "begin_time = ? ", beginTimeMillis);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (matchedList.size() > 0) {
            return matchedList.get(0);
        } else {
            return null;
        }
    }

    public static LSInquiry getInquiryByNumberIfExists(String number) {
        ArrayList<LSInquiry> list = null;
        try {
            list = (ArrayList<LSInquiry>) LSInquiry.find(LSInquiry.class, "contact_number = ? ", number);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public Long getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Long averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LSContactProfile getContactProfile() {
        return contactProfile;
    }

    public void setContactProfile(LSContactProfile contactProfile) {
        this.contactProfile = contactProfile;
    }

    @Override
    public String toString() {
        return "LSInquiry{" +
                "contactName='" + contactName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", contact=" + contact +
                ", beginTime=" + beginTime +
                ", duration=" + duration +
                ", countOfInquiries=" + countOfInquiries +
                ", syncStatus='" + syncStatus + '\'' +
                ", serverId='" + serverId + '\'' +
                ", averageResponseTime=" + averageResponseTime +
                ", status='" + status + '\'' +
                ", contactProfile=" + contactProfile +
                '}';
    }
}
