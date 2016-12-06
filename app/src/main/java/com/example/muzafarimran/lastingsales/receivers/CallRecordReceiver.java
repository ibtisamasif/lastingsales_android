package com.example.muzafarimran.lastingsales.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.muzafarimran.lastingsales.Utils.CallRecord;

import java.io.File;
import java.io.IOException;

/**
 * Created by aykutasil on 19.10.2016.
 */
public class CallRecordReceiver extends BroadcastReceiver {

    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    private static final String TAG = CallRecordReceiver.class.getSimpleName();
    private static MediaRecorder recorder;
    private static boolean recordstarted = false;
    private static boolean wasRinging = false;
    Bundle bundle;
    File audiofile;
    private CallRecord.Builder mBuilder;
    private String inCall, outCall, state;

    public CallRecordReceiver() {
    }

    public void setmBuilder(CallRecord.Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if ((bundle = intent.getExtras()) == null) {
                Log.e(TAG, "Intent extras are null");
                return;
            }
            state = bundle.getString(TelephonyManager.EXTRA_STATE);
            Log.i(TAG, state == null ? "null" : state);
            if (intent.getAction().equals(ACTION_IN)) {
                Log.i(TAG, "android.intent.action.PHONE_STATE");
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    wasRinging = true;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (wasRinging && !recordstarted) {
                        startRecord("incoming");
                        Log.i(TAG, "start record");
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    wasRinging = false;
                    Log.i(TAG, recordstarted ? "recordstarted is true" : "recordstarted is false");
                    if (recordstarted) {
                        recorder.stop();
                        recorder.reset();
                        recorder.release();
                        recordstarted = false;
                        Log.i(TAG, "stop record");
                    }
                }
            } else if (intent.getAction().equals(ACTION_OUT)) {
                Log.i(TAG, "android.intent.action.NEW_OUTGOING_CALL");
                if ((bundle = intent.getExtras()) != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    if (!recordstarted) {
                        startRecord("outgoing");
                        Log.i(TAG, "start record");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecord(String seed) {
        try {
            //String dateString = getSimpleDateFormat().format(new Date());
            File sampleDir = new File(Environment.getExternalStorageDirectory(), "/" + mBuilder.getRecordDirName());
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
            }
            String file_name = "";
            if (mBuilder.isShowSeed()) {
                file_name = mBuilder.getRecordFileName() + "_" + seed + "_"; // temp dosyaya kayıt edildiği için dosya isminin en sonuna random karakter ekleniyor
            } else {
                file_name = mBuilder.getRecordFileName();
            }
            String suffix = "";
            switch (mBuilder.getOutputFormat()) {
                case MediaRecorder.OutputFormat.AMR_NB: {
                    suffix = ".amr";
                    break;
                }
                case MediaRecorder.OutputFormat.AMR_WB: {
                    suffix = ".amr";
                    break;
                }
                case MediaRecorder.OutputFormat.MPEG_4: {
                    suffix = ".mp4";
                    break;
                }
                case MediaRecorder.OutputFormat.THREE_GPP: {
                    suffix = ".3gp";
                    break;
                }
                default: {
                    suffix = ".amr";
                    break;
                }
            }

            audiofile = File.createTempFile(file_name, suffix, sampleDir);
            recorder = new MediaRecorder();
            recorder.setAudioSource(mBuilder.getAudioSource());
            recorder.setOutputFormat(mBuilder.getOutputFormat());
            recorder.setAudioEncoder(mBuilder.getAudioEncoder());
            recorder.setOutputFile(audiofile.getAbsolutePath());
            recorder.prepare();

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recordstarted = true;
        Log.i(TAG, "record start");
    }
}