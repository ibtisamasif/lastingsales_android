
package com.example.muzafarimran.lastingsales.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.service.CallService;

public class ACallReceiver extends BroadcastReceiver {

    String no;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText( context, "Working", Toast.LENGTH_SHORT ).show();



        String state=intent.getStringExtra( TelephonyManager.EXTRA_STATE );

        if(state==null){
            no=intent.getStringExtra( intent.getStringExtra( Intent.EXTRA_PHONE_NUMBER ) );
            Toast.makeText( context, "Outgoing", Toast.LENGTH_SHORT ).show();
        }

        else  if(state.equals( TelephonyManager.EXTRA_STATE_IDLE )){

            String num=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText( context, no +"   "+num, Toast.LENGTH_SHORT ).show();
           // context.startService( new Intent( context, CallService.class ) );
        }


    }
}

