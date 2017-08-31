package com.example.muzafarimran.lastingsales.providers.models;

import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by ibtisam on 2/24/2017.
 */

@Deprecated
public class LSCallRecording extends SugarRecord {

    private String contactNumber;
    private String serverIdOfCall;
    private String localIdOfCall;
    private String audioPath;
    private String syncStatus;
    private Long beginTime;


    public LSCallRecording() {
    }

    public static LSCallRecording getAllUnsyncedRecordings(){
        ArrayList<LSCallRecording> list = null;
        try {
            list = (ArrayList<LSCallRecording>) LSCallRecording.find(LSCallRecording.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getServerIdOfCall() {
        return serverIdOfCall;
    }

    public void setServerIdOfCall(String serverIdofCall) {
        this.serverIdOfCall = serverIdofCall;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
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
