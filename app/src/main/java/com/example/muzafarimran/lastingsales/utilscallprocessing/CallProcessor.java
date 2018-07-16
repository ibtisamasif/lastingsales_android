package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.service.CallService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class CallProcessor {


    public static void Process(Context mContext, LSCall call, boolean showNotification,boolean showDialog) {

        Log.d("processor", "call amir");

        if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {


            Log.d("personal call type",call.getType());
            Log.d("personal show dialog",String.valueOf(showNotification));
            Log.d("personal show dialog",String.valueOf(showDialog));


        }
        else{
            Log.d("not match any condition"," else part");

        }


       // Toast.makeText(mContext, "type "+call.getType(), Toast.LENGTH_SHORT).show();
        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE

            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING

                Log.d("processor", "nothing");


                Log.d("amir", "contact exist in ignore list");
            } else {

                Log.d("processor", "call");


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
                                saveCallLogs(call);
                                case3(call);
                                case5(mContext);
                                return;
                            } else {
//                            Toast.makeText(mContext, "condition false", Toast.LENGTH_SHORT).show();
                                Log.d("No. from Processor", call.getContactNumber());

                                if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {

                                    showDialog(mContext, call);


                                }
                                saveCallLogs(call);
                                case3(call);
                                case5(mContext);

                            }
                        } else {
                            Log.d("iscontactSave is ", "NULL");

                            if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {

                                showDialog(mContext, call);


                            }
                            saveCallLogs(call);
                            case3(call);
                            case5(mContext);
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
                    saveContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                    saveContact.setContactName(call.getContactName());
                    saveContact.setContactType(call.getType());


                    if (saveContact.save() > 0) {


                        Log.d("amir", "save contact no");

                        if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {

                            showDialog(mContext, call);


                        }

                        //save call logs

                        saveCallLogs(call);

                        case3(call);
                        case5(mContext);

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

                    Log.d("personal", "contact exist");



                    saveCallLogs(call);

                    case3(call);
                    case5(mContext);

                } else {

                    if (showDialog && showNotification && call.getType().equals(LSCall.CALL_TYPE_OUTGOING) || call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {



                        showDialog(mContext, call);


                    }



                }

                Log.d("personal call type",call.getType());
                Log.d("personal show dialog",String.valueOf(showNotification));



            }
        }

    }


    private static void case3(LSCall call) {

//        CallTypeManager CallTypeManager = new CallTypeManager();

        // incoming/ outgoing calls
        Log.d("case3", "calling");

        /* Log.d("return type", CallTypeManager.getCallType(call.getType(), String.valueOf(call.getDuration())));
         */
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) || call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) {

            List<LSInquiry> checkInquiry = LSInquiry.find(LSInquiry.class, "contact_number=?", call.getContactNumber());

            if (checkInquiry.size() > 0) {
                LSInquiry removeInquiry = LSInquiry.findById(LSInquiry.class, checkInquiry.get(0).getId());
                removeInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                try {
                    if (removeInquiry.save()>0) {
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


        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)) {


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


        } else if (call.getType().equals(LSCall.CALL_TYPE_MISSED)) {


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
        intent.putExtra("name", call.getContactName());



        Log.d("start dialog service", "function call");
        mContext.startService(intent);

    }

    private static void saveCallLogs(LSCall call) {

        LSContact lsContact = LSContact.getContactFromNumber(call.getContactNumber());
        lsContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        if (lsContact.save() > 0) {
            Log.d("contact updateat", "cur time");
        }

        if (call.save() > 0) {
            Log.d("calllog", "save");
        }

    }


    private static void case5(Context context) {
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();
    }


}


