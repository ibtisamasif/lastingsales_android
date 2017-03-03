package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by ibtisam on 2/24/2017.
 */

public class LSCallRecording extends SugarRecord {

    private String serverIdOfCall;
    private String localIdOfCall;
    private String audioPath;
    private String syncStatus;

    public LSCallRecording() {
    }

    public static LSCallRecording getRecordingByAudioPath(String path){
        ArrayList<LSCallRecording> list = null;
        try {
            list = (ArrayList<LSCallRecording>) LSCallRecording.find(LSCallRecording.class, "audio_path = ? ", path);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
    public String getLocalIdOfCall() {
        return localIdOfCall;
    }

    public void setLocalIdOfCall(String localIdOfCall) {
        this.localIdOfCall = localIdOfCall;
    }

    public String getServerIdOfCall() {
        return serverIdOfCall;
    }

    public void setServerIdOfCall(String serverIofCall) {
        this.serverIdOfCall = serverIofCall;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String path) {
        this.audioPath = path;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}