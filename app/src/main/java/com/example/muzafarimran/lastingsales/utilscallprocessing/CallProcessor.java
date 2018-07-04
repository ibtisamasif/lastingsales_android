package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.content.Intent;
import android.content.SyncStats;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.service.CallService;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndTagBoxService;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

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

                                    showDialog(mContext,call);
                                    saveCallLogs(call);

                            }
                        } else {
                            Log.d("iscontact save is ", "NULL");
                            showDialog(mContext,call);
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


                        showDialog(mContext,call);


                        //save call logs

                        saveCallLogs(call);



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

                    showDialog(mContext,call);

                    saveCallLogs(call);




                }
            }

        }
    }

    private static   void showDialog(Context mContext, LSCall call) {

        Intent intent = new Intent(mContext, CallService.class);
        intent.putExtra("no", call.getContactNumber());

        Log.d("save no is ", call.getContactNumber());
        mContext.startService(intent);

    }

    private static void saveCallLogs(LSCall call){

        LSCall lsCall=new LSCall();
        lsCall.setContact(call.getContact());
        lsCall.setBeginTime(call.getBeginTime());
        lsCall.setCallLogId(call.getCallLogId());
        lsCall.setContactName(call.getContactName());
        lsCall.setContactNumber(call.getContactNumber());
        lsCall.setCountOfInquiries(call.getCountOfInquiries());
        lsCall.setType(call.getType());
        lsCall.setSyncStatus(call.getSyncStatus());
        lsCall.setDuration(call.getDuration());
        lsCall.setServerId(call.getServerId());

        if(lsCall.save()>0){
            Log.d("personal calllog","save");
        }

    }


}


