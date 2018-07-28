package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.service.CallService;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class CallProcessor {
    public static final String TAG = "CallProcessor";
    private static final long MILLIS_10_MINUTES = 600000;

    public static void Process(Context mContext, LSCall call, boolean showNotification, boolean showDialog) {
        Log.d(TAG, "call amir");
        if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
            Log.d(TAG, "personal call type: " + call.getType());
            Log.d(TAG, "personal show dialog: " + String.valueOf(showNotification));
            Log.d(TAG, "personal show dialog: " + String.valueOf(showDialog));
        } else {
            Log.d(TAG, "not match any condition: else part");
        }

        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE
            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING
                Log.d(TAG, "contact exist in ignore list");
            } else {
                Log.d(TAG, call.getContactNumber());
                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
                // if it exists
                if (contact != null) {
                    //TODO show after call dialog & save call log
                    Log.d(TAG, call.getContactNumber());
                    if (contact.isContactSave() != null) {
                        if (contact.isContactSave().equals("true")) {
                            Log.d(TAG + contact.isContactSave(), call.getContactNumber());
                            Log.d(TAG, call.getContactNumber());
                            saveCallLogs(call);
                            case3(call);
                            return;
                        } else {
                            Log.d(TAG, call.getContactNumber());
                            if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
                                showDialog(mContext, call);
                            }
                            saveCallLogs(call);
                            case3(call);
                        }
                    } else {
                        Log.d(TAG, "iscontactSave is NULL");
                        if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
                            showDialog(mContext, call);
                        }
                        saveCallLogs(call);
                        case3(call);
                    }
                } else {
                    //if not exists
                    LSContact saveContact = new LSContact();
                    saveContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(mContext, call.getContactNumber()));
                    saveContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                    saveContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                    saveContact.setContactName(call.getContactName());
                    saveContact.setContactType(call.getType());

                    if (saveContact.save() > 0) {
                        Log.d(TAG, "save contact no");
                        if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
                            showDialog(mContext, call);
                        }
                        saveCallLogs(call);
                        case3(call);
                    }
                }
            }
        } else { // PERSONAL PHONE
            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING
            } else {
                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
                if (contact != null) {
                    Log.d(TAG, "contact exist");
                    saveCallLogs(call);
                    case3(call);
                } else {
                    if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
                        showDialog(mContext, call);
                    }
                }
                Log.d(TAG, "personal call type" + call.getType());
                Log.d(TAG, "personal show dialog" + String.valueOf(showNotification));
            }
        }
    }

    private static void case3(LSCall call) {
        // incoming/ outgoing calls
        Log.d(TAG, TAG + "calling");
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) || call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) {
            List<LSInquiry> checkInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());
            if (checkInquiry.size() > 0) {
                LSInquiry removeInquiry = LSInquiry.findById(LSInquiry.class, checkInquiry.get(0).getId());
                removeInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                try {
                    if (removeInquiry.save() > 0) {
                        Log.d(TAG, "inquire");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "not exists");
            }
        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)) {
            List<LSInquiry> missInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());
            if (missInquiry.size() > 0) {
                //if exists
                LSInquiry incInquiry = LSInquiry.findById(LSInquiry.class, missInquiry.get(0).getId());
                int getCount = incInquiry.getCountOfInquiries();
                incInquiry.setCountOfInquiries(++getCount);
                if (incInquiry.save() > 0) {
                    Log.d(TAG, "updated/increment");
                } else {
                    Log.d(TAG, "not updated/incremented something went wrong");
                }
            } else {
                //if not exists
                LSInquiry lsInquiry = new LSInquiry();
                lsInquiry.setContactName(call.getContactName());
                lsInquiry.setContactNumber(call.getContactNumber());
                lsInquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
                lsInquiry.setDuration(call.getDuration());
                lsInquiry.setCountOfInquiries(call.getCountOfInquiries());
                lsInquiry.setBeginTime(call.getBeginTime());
                try {
                    if (lsInquiry.save() > 0) {
                        Log.d(TAG, "saved");
                    } else {
                        Log.d(TAG, "not save something went wrong");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (call.getType().equals(LSCall.CALL_TYPE_MISSED)) {
            List<LSInquiry> missInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());
            if (missInquiry.size() > 0) {
                //if exists
                LSInquiry incInquiry = LSInquiry.findById(LSInquiry.class, missInquiry.get(0).getId());
                int getCount = incInquiry.getCountOfInquiries();
                incInquiry.setCountOfInquiries(++getCount);
                if (incInquiry.save() > 0) {
                    Log.d(TAG, "updated/increment");
                } else {
                    Log.d(TAG, "not updated/incremented something went wrong");
                }
            } else {
                //if not exists
                LSInquiry lsInquiry = new LSInquiry();
                lsInquiry.setContactName(call.getContactName());
                lsInquiry.setContactNumber(call.getContactNumber());
                lsInquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
                lsInquiry.setDuration(call.getDuration());
                lsInquiry.setCountOfInquiries(call.getCountOfInquiries());
                lsInquiry.setBeginTime(call.getBeginTime());
                try {
                    if (lsInquiry.save() > 0) {
                        Log.d(TAG, "saved");
                    } else {
                        Log.d(TAG, "not save something went wrong");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (call.getType().equals(LSCall.CALL_TYPE_UNANSWERED)) {
            //Outgoing Unanswered
            // Nothing to do for now.
        }else {
            Log.d(TAG, " CALL TYPE UNKNOWN & UNHANDELED");
        }
    }

    private static void showDialog(Context mContext, LSCall call) {
//        if (showNotification && call.getBeginTime() + MILLIS_10_MINUTES > Calendar.getInstance().getTimeInMillis()) {
//            CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
//        }
        //TODO replace below code with above one in future. To use single service for flyer and for after call dialog.
        Intent intent = new Intent(mContext, CallService.class);
        intent.putExtra("no", call.getContactNumber());
        intent.putExtra("name", call.getContactName());
        Log.d("start dialog service", "function call");
        mContext.startService(intent);
    }

    private static void saveCallLogs(LSCall call) {
        LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());
        if (lsContact != null) {
            lsContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            if (lsContact.save() > 0) {
                Log.d(TAG, "lsContact SAVED");
            }
        }
        if (call.save() > 0) {
            Log.d(TAG, "lsCall Saved");
        }
    }
}


