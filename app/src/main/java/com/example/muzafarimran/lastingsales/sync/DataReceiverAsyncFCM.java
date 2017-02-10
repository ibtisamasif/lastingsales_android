package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.example.muzafarimran.lastingsales.SessionManager;

/**
 * Created by ibtisam on 2/7/2017.
 */
@Deprecated
public class DataReceiverAsyncFCM extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "DataReceiverAsyncFCM";
    Context mContext;
    SessionManager sessionManager;

    public DataReceiverAsyncFCM(Context context) {
        super();
        mContext = context;
        sessionManager = new SessionManager(mContext);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Object... objects) {
        return null;




    }



    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
