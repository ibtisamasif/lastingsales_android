package com.example.muzafarimran.lastingsales.utils;

import android.os.FileObserver;

import com.example.muzafarimran.lastingsales.receivers.CallsStatesReceiver;

import java.io.File;

@Deprecated
public class PathFileObserver extends FileObserver {

    public interface FileObserverInterface{
        public void onEvent(int event , String path);
    }

    private FileObserverInterface mFileObserverInterface;

    static final String TAG = "FILEOBSERVER";
    /**
     * should be ends with "/"
     */
    String rootPath;
    static final int mask = (FileObserver.CREATE |
            FileObserver.DELETE |
            FileObserver.DELETE_SELF |
            FileObserver.MODIFY |
            FileObserver.MOVED_FROM |
            FileObserver.MOVED_TO |
            FileObserver.MOVE_SELF |
            FileObserver.CLOSE_WRITE |
            FileObserver.CLOSE_NOWRITE);

    public PathFileObserver(String root){
        super(root, mask);
        mFileObserverInterface = new CallsStatesReceiver();
        if (! root.endsWith(File.separator)){
            root += File.separator;
        }
        rootPath = root;

    }


    public void onEvent(int event, String path) {

        mFileObserverInterface.onEvent(event , path);

//        switch(event){
//            case FileObserver.CREATE:
//                Log.d(TAG, "CREATE:" + rootPath + path);
//                break;
//            case FileObserver.DELETE:
//                Log.d(TAG, "DELETE:" + rootPath + path);
//                break;
//            case FileObserver.DELETE_SELF:
//                Log.d(TAG, "DELETE_SELF:" + rootPath + path);
//                break;
//            case FileObserver.MODIFY:
//                Log.d(TAG, "MODIFY:" + rootPath + path);
//                break;
//            case FileObserver.MOVED_FROM:
//                Log.d(TAG, "MOVED_FROM:" + rootPath + path);
//                break;
//            case FileObserver.MOVED_TO:
//                Log.d(TAG, "MOVED_TO:" + path);
//                break;
//            case FileObserver.MOVE_SELF:
//                Log.d(TAG, "MOVE_SELF:" + path);
//                break;
//            default:
//                // just ignore
//                break;
//        }
    }

    public void close(){
        super.finalize();
    }
}