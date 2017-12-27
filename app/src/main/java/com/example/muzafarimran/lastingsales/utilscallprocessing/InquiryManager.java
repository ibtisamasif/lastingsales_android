package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.listeners.LSContactProfileCallback;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.ContactProfileProvider;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class InquiryManager {
    private static LSInquiry inquiry;
    public static final String TAG = "InquiryManager";

    public static void RemoveByContact(Context context, LSContact tempContact) {
        //update inquiry as well if exists
        inquiry = LSInquiry.getInquiryByNumberIfExists(tempContact.getPhoneOne());
        if (inquiry != null) {
            inquiry.setContact(tempContact);
            inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
            inquiry.save();
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
            dataSenderAsync.run();
            // Update launcher icon count
            new ShortcutBadgeUpdateAsync(context).execute();
            InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
            TinyBus bus = TinyBus.from(context);
            bus.post(mCallEvent);
        }
    }

    static void remove(Context context, LSCall call) {
        inquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (inquiry != null && inquiry.getAverageResponseTime() <= 0) {
            Calendar now = Calendar.getInstance();
            inquiry.setAverageResponseTime(now.getTimeInMillis() - inquiry.getBeginTime());
            inquiry.setStatus(LSInquiry.INQUIRY_STATUS_ATTENDED);
            if (inquiry.getSyncStatus().equals(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED)) {
                inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
            } else {
                inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            }
            inquiry.save();
            // Update launcher icon count
            new ShortcutBadgeUpdateAsync(context).execute();
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Inquiry Followed");
            InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
            TinyBus bus = TinyBus.from(context);
            bus.post(mCallEvent);
        }
    }

    static void createOrUpdate(Context context, LSCall call) {
        inquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (inquiry != null) {
            inquiry.setCountOfInquiries(inquiry.getCountOfInquiries() + 1);
            inquiry.save();
            // Update launcher icon count
            new ShortcutBadgeUpdateAsync(context).execute();
        } else {
            inquiry = new LSInquiry();
            inquiry.setContactNumber(call.getContactNumber());
            inquiry.setContactName(call.getContactName());
            inquiry.setContact(call.getContact());
            inquiry.setBeginTime(call.getBeginTime());
//            inquiry.setDuration(call.getDuration());
            inquiry.setCountOfInquiries(1);
            inquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
            inquiry.setAverageResponseTime(0L);
            inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            inquiry.save();
            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(inquiry.getContactNumber());
            if (lsContactProfile != null) {
                Log.d(TAG, "createOrUpdate: getFromLSContactProfile");
                inquiry.setContactProfile(lsContactProfile);
                inquiry.save();
                LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber()); //TODO Can be fetched from previous screen will improve performance and save battery
                if (lsContact != null) {
                    lsContact.setContactProfile(lsContactProfile);
                    lsContact.save();
                }
            } else {
                Log.d(TAG, "createOrUpdate: get LsContactProfile from ContactProfileProvider");
                //TODO try to fetch from server instantly from server to show pictures in Inquiries and unlabeled lists instantly and efficiently

                ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
                contactProfileProvider.getContactProfile(call.getContactNumber(), new LSContactProfileCallback() {
                    @Override
                    public void onSuccess(LSContactProfile result) {
                        Log.d(TAG, "onResponse: lsContactProfile: " + result);
                        if (result != null) {
                            if (inquiry != null) {
                                inquiry.setContactProfile(result);
                                inquiry.save();
                                Log.d(TAG, "onSuccess: inquiry: " + inquiry.toString());
                            }
                            LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());
                            if (lsContact != null) {
                                lsContact.setContactProfile(result);
                                lsContact.save();
                                Log.d(TAG, "onSuccess: lsContact: " + lsContact.toString());
                            }
                        }
                    }
                });

//            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
//            LSContactProfile lsContactProfile2 = contactProfileProvider.getContactProfile(call.getContactNumber());
//            if (lsContactProfile2 != null) {
//                newInquiry.setContactProfile(lsContactProfile2);
//                newInquiry.save();
//                LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());
//                if (lsContact != null) {
//                    lsContact.setContactProfile(lsContactProfile2);
//                    lsContact.save();
//                }
//            }
            }
            // Update launcher icon count
            new ShortcutBadgeUpdateAsync(context).execute();
        }
        InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
        TinyBus bus = TinyBus.from(context);
        bus.post(mCallEvent);
    }

    public static void createOrUpdate(Context context, String status_of_inquiry, long beginTimeFromServer, String contactNumber) {
        if (status_of_inquiry.equals("pending")) {
            inquiry = LSInquiry.getPendingInquiryByBeginDateTimeIfExists(Long.toString(beginTimeFromServer));
            if (inquiry != null) {
                Log.d(TAG, "onResponse: EXISTS: " + inquiry.getContactNumber());
//                inquiry.setCountOfInquiries(inquiry.getCountOfInquiries() + 1);
//                inquiry.save();
            } else {
                Log.d(TAG, "onResponse: DOESNT EXIST");
                LSContact lsContact = LSContact.getContactFromNumber(contactNumber);
                if (lsContact != null) {
                    inquiry = new LSInquiry();
                    inquiry.setContactNumber(contactNumber);
//                                    inquiry.setContactName("From Server");
                    inquiry.setContact(lsContact);
                    inquiry.setBeginTime(beginTimeFromServer);
                    inquiry.setCountOfInquiries(1);
                    inquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
                    inquiry.setAverageResponseTime(0L);
                    inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED);
                    inquiry.save();
                    LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(inquiry.getContactNumber());
                    if (lsContactProfile != null) {
                        Log.d(TAG, "createOrUpdate: getFromLSContactProfile");
                        inquiry.setContactProfile(lsContactProfile);
                        inquiry.save();
                        lsContact.setContactProfile(lsContactProfile);
                        lsContact.save();

                    } else {
                        Log.d(TAG, "createOrUpdate: get LsContactProfile from ContactProfileProvider");
                        //TODO try to fetch from server instantly from server to show pictures in Inquiries and unlabeled lists instantly and efficiently

                        ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
                        contactProfileProvider.getContactProfile(contactNumber, new LSContactProfileCallback() {
                            @Override
                            public void onSuccess(LSContactProfile result) {
                                Log.d(TAG, "onResponse: lsContactProfile: " + result);
                                if (result != null) {
                                    if (inquiry != null) {
                                        inquiry.setContactProfile(result);
                                        inquiry.save();
                                        Log.d(TAG, "onSuccess: inquiry: " + inquiry.toString());
                                        LSContact lsContact = LSContact.getContactFromNumber(inquiry.getContactNumber());
                                        if (lsContact != null) {
                                            lsContact.setContactProfile(result);
                                            lsContact.save();
                                            Log.d(TAG, "onSuccess: lsContact: " + lsContact.toString());
                                        }
                                    }
                                }
                            }
                        });

//            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
//            LSContactProfile lsContactProfile2 = contactProfileProvider.getContactProfile(call.getContactNumber());
//            if (lsContactProfile2 != null) {
//                newInquiry.setContactProfile(lsContactProfile2);
//                newInquiry.save();
//                LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());
//                if (lsContact != null) {
//                    lsContact.setContactProfile(lsContactProfile2);
//                    lsContact.save();
//                }
//            }
                    }
                    // Update launcher icon count
                    new ShortcutBadgeUpdateAsync(context).execute();
                }
            }
            InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
            TinyBus bus = TinyBus.from(context);
            bus.post(mCallEvent);
        }
    }
}
