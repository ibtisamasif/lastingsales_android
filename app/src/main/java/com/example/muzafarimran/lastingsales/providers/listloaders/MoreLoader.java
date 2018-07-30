package com.example.muzafarimran.lastingsales.providers.listloaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.MoreItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class MoreLoader extends AsyncTaskLoader<List<Object>> {
    public static final String TAG = "MoreLoader";
    private List<Object> mData;

    public MoreLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        List<Object> data = new ArrayList<Object>();

//

        MoreItem moreItemSetting = new MoreItem("SettingsPrefActivity");
        moreItemSetting.drawable = R.drawable.bg_setting_cardcopyxxxhdpi;
        moreItemSetting.description = "Manage your accounts and notification preferences.";

//        MoreItem moreItemCompany = new MoreItem("Company", CompanyActivity.class);
//        moreItemCompany.drawable = R.drawable.bg_ignord_cardxxxhdpi;
//        moreItemCompany.description = "Contacts that you don't want to be tracked i.e friends and family.";

//        data.add(moreItemColleague);
//        data.add(moreItemIgnored);
        data.add(moreItemSetting);
//        data.add(moreItemCompany);

        return data;
    }


    @Override
    public void deliverResult(@Nullable List<Object> data) {
        Log.d(TAG, "deliverResult: ");
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Object> oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: ");
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

//        // Begin monitoring the underlying data source.
//        if (mObserver == null) {
//            mObserver = new SampleObserver();
//            // TODO: register the observer
//        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading: ");
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: ");
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

//        // The Loader is being reset, so we should stop monitoring for changes.
//        if (mObserver != null) {
//            // TODO: unregister the observer
//            mObserver = null;
//        }
    }

    @Override
    public void onCanceled(@Nullable List<Object> data) {
        Log.d(TAG, "onCanceled: ");
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<Object> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/

    // NOTE: Implementing an observer is outside the scope of this post (this example
    // uses a made-up "SampleObserver" to illustrate when/where the observer should
    // be initialized).

    // The observer could be anything so long as it is able to detect content changes
    // and report them to the loader with a call to onContentChanged(). For example,
    // if you were writing a Loader which loads a mData of all installed applications
    // on the device, the observer could be a BroadcastReceiver that listens for the
    // ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the particular
    // Loader whenever the receiver detects that a new application has been installed.
    // Please don’t hesitate to leave a comment if you still find this confusing! :)
//    private SampleObserver mObserver;

}
