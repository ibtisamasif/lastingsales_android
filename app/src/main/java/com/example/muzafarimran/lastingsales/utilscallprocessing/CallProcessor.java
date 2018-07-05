package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.content.Intent;
import android.content.SyncStats;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.service.CallService;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndTagBoxService;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class CallProcessor {


    public static void Process(Context mContext, LSCall call, boolean showNotification) {


        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE

            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING

                Log.d("amir", "contact exist in ignore list");
            } else {



                /* return;*/



              /*  SessionManager sessionManager=new SessionManager(mContext);
                sessionManager.setTmpUserNo(call.getContactNumber());
*/
                Log.d("No from Processor", call.getContactNumber());
                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());

                // if it exists
                if (contact != null) {
                    //TODO show after call dialog & save call log
                    Log.d("exist", call.getContactNumber());

                    Log.d("No from Processor", call.getContactNumber());


                  /*  Intent d = new Intent(mContext, CallService.class);
                    d.putExtra("no", call.getContactNumber());

                    mContext.startService(d);*/


                    LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());

                    if (lsContact != null) {
                        if (lsContact.isContactSave() != null) {
                            if (lsContact.isContactSave().equals("true")) {
                                Log.d("contact already saved" + lsContact.isContactSave(), call.getContactNumber());
                                //  Toast.makeText(mContext, "Condition true", Toast.LENGTH_SHORT).show();
                                Log.d("No from Processor", call.getContactNumber());

                                return;
                            } else {
//                            Toast.makeText(mContext, "condition false", Toast.LENGTH_SHORT).show();
                                Log.d("No. from Processor", call.getContactNumber());

                                if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_OUTGOING) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_INCOMING)) {

                                    showDialog(mContext, call);


                                }
                                saveCallLogs(call);
                                case3(call);

                            }
                        } else {
                            Log.d("iscontactSave is ", "NULL");

                            if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_OUTGOING) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_INCOMING)) {

                                showDialog(mContext, call);


                            }
                            saveCallLogs(call);
                            case3(call);
                        }
                    } else {
                        Log.d("result is null", "null");
                    }

                } else {

                    //if not exists

                    //


                    LSContact saveContact = new LSContact();
                    saveContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(call.getContactNumber()));
                    saveContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                    if (saveContact.save() > 0) {


                        Log.d("amir", "save contact no");

                        if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_OUTGOING) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_INCOMING)) {

                            showDialog(mContext, call);


                        }

                        //save call logs

                        saveCallLogs(call);

                        case3(call);

                        // successfully add num to db

                        //show dialog function


//                        CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
                    } else {
                        //error
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


                } else {

                    if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_OUTGOING) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_INCOMING)) {


                        showDialog(mContext, call);


                    }


                    saveCallLogs(call);

                    case3(call);


                }
            }

        }
    }

    private static void case3(LSCall call) {

//        CallTypeManager CallTypeManager = new CallTypeManager();

        // incoming/ outgoing calls
        Log.d("case3", "calling");
//        if (CallTypeManager != null) {
        if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())) == null) {

            Log.d("null", "null");
            return;

        }

        Log.d("return type", CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())));
        if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_INCOMING) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_OUTGOING)) {

            List<LSInquiry> checkInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());

            if (checkInquiry.size() > 0) {
                LSInquiry removeInquiry = LSInquiry.findById(LSInquiry.class, checkInquiry.get(0).getId());
                try {
                    if (removeInquiry.delete()) {
                        Log.d("remove", "inquire");
                    } else {
                        Log.d("error ", "remove inquiry");
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            } else {
                Log.d("inquiry", "not exists");
            }


        } else if (CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_MISSED) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_UNANSWERED) || CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())).equals(LSCall.CALL_TYPE_REJECTED)) {


            List<LSInquiry> missInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());
            if (missInquiry.size() > 0) {

                //if exists

                LSInquiry incInquiry = LSInquiry.findById(LSInquiry.class, missInquiry.get(0).getId());
                int getCount = incInquiry.getCountOfInquiries();

                incInquiry.setCountOfInquiries(++getCount);
                if (incInquiry.save() > 0) {

                    Log.d("inquiry", "updated/increment");

                } else {
                    Log.d("inquiryy", "not updated/incremented something went wrong");
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
                        Log.d("inquiry", "saved");
                    } else {
                        Log.d("inquiry", "not save something went wrong");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else {
            Log.d("allcondition ", "false");
        }
//            } else {
//                Log.d("calltypemanager is ", "NULL");Z
//        }

    }

    private static void showDialog(Context mContext, LSCall call) {

        Intent intent = new Intent(mContext, CallService.class);
        intent.putExtra("no", call.getContactNumber());

        Log.d("start dialog service", "function call");
        mContext.startService(intent);

    }

    private static void saveCallLogs(LSCall call) {

        LSCall lsCall = new LSCall();
        lsCall.setContact(call.getContact());
        lsCall.setBeginTime(call.getBeginTime());
        lsCall.setCallLogId(call.getCallLogId());
        lsCall.setContactName(call.getContactName());
        lsCall.setContactNumber(call.getContactNumber());
        lsCall.setCountOfInquiries(call.getCountOfInquiries());
        lsCall.setType(CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())));
        lsCall.setSyncStatus(call.getSyncStatus());
        lsCall.setDuration(call.getDuration());
        lsCall.setServerId(call.getServerId());

        if (lsCall.save() > 0) {
            Log.d("calllog", "save");
        }

    }


}


