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
import com.example.muzafarimran.lastingsales.service.CallService;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndTagBoxService;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class CallProcessor {


public static   String num;

    public static void Process(Context mContext, LSCall call, boolean showNotification) {

        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE

            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING

                Log.d("amir","contact exist in ignore list");
            } else {

                SessionManager sessionManager=new SessionManager(mContext);
                sessionManager.setTmpUserNo(call.getContactNumber());

                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());

                // if it exists
                if (contact != null) {
                    //TODO show after call dialog & save call log
                    Log.d("exist",call.getContactNumber());



                    LSContact lsContact=LSContact.getContactFromNumber(call.getContactNumber());

                    if(lsContact!=null) {
                        if (lsContact.isContactSave().equals("true")) {
                            Log.d("contact already saved" + lsContact.isContactSave(), call.getContactNumber());
                          //  Toast.makeText(mContext, "Condition true", Toast.LENGTH_SHORT).show();
             return;
                        } else {
//                            Toast.makeText(mContext, "condition false", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(mContext, CallService.class);
                            intent.putExtra("no", call.getContactNumber());

                            mContext.startService(intent);


                        }
                    }else{
                        Log.d("result is null","null");
                    }

                } else {

                    //if not exists

                    //


                    num=call.getContactNumber();
                    LSContact saveContact = new LSContact();
                    saveContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(call.getContactNumber()));
                    saveContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                    if (saveContact.save() > 0) {




                        Log.d("amir","save contact no");

                        Intent intent=new Intent(mContext,CallService.class);
                        intent.putExtra("no",call.getContactNumber());

                        Log.d("save no is ",call.getContactNumber());
                        mContext.startService(intent);

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
                    //TODO show after call dialog & save call log

                } else {
                    // new call
                }
            }

        }
    }


}


