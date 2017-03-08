package com.example.muzafarimran.lastingsales.receivers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSCallRecording;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.List;

/**
 * Created by ibtisam on 3/7/2017.
 */

public class RecordingManager extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "RecordingManager";
    private Context mContext;

//    public RecordingManager(Context context) {
//        mContext = context;
//    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // get all unsynced recording
        List<LSCallRecording> recordingList = null;
        if (LSCallRecording.count(LSCallRecording.class) > 0) {
            recordingList = LSCallRecording.find(LSCallRecording.class, "sync_status = ?", SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
            Log.d(TAG, "addRecordingsToServer: count : " + recordingList.size());
            for (LSCallRecording oneRecording : recordingList) {
                Log.d(TAG, "Found Call");

                // find associated call of recording

                List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
                if (allCalls != null && allCalls.size() > 0) {
                    for (LSCall oneCall : allCalls) {
                        if(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(oneCall.getBeginTime()).equals(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(oneRecording.getBeginTime()))){
                            Log.d(TAG, "doInBackground: RecordingTime: "+oneRecording.getBeginTime());
                            Log.d(TAG, "doInBackground:      CallTime: "+oneCall.getBeginTime());
                            if(oneCall.getServerId() != null){
                                Log.d(TAG, "doInBackground: ServerIdOfCallFromLSCALL: "+oneCall.getServerId());
                                oneRecording.setServerIdOfCall(oneCall.getServerId());
                                oneRecording.save();
                                Log.d(TAG, "doInBackground: ServerIdOfCallFromLSCallRecording: "+oneRecording.getServerIdOfCall());

//                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                dataSenderAsync.execute();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
