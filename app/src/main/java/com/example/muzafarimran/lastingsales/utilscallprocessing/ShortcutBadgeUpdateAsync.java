package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by ibtisam on 9/7/2017.
 */

public class ShortcutBadgeUpdateAsync extends AsyncTask<Void, String, Void> {
    private static final String TAG = "ShortcutBadgeUpdateAsyn";
    List<LSInquiry> allPendingInquiries;
    Context mContext;

    public ShortcutBadgeUpdateAsync(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected Void doInBackground(Void... unused) {
        allPendingInquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//            SystemClock.sleep(200);
        return (null);
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.d(TAG, "onPostExecute: ");
//        Toast.makeText(mContext, "ShortcutBadgeUpdated", Toast.LENGTH_SHORT).show();
        if (allPendingInquiries != null && allPendingInquiries.size() > 0) {
            ShortcutBadger.applyCount(mContext, allPendingInquiries.size());
        }
    }
}