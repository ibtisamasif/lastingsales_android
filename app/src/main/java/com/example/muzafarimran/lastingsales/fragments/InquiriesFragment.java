package com.example.muzafarimran.lastingsales.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.InquiriesAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InquiriesFragment extends SearchFragment {

    public static final String TAG = "InquiriesFragment";
    InquiriesAdapter inquiriesAdapter;
    ListView listView = null;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public static InquiriesFragment newInstance(int page, String title) {
        InquiriesFragment fragmentFirst = new InquiriesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSInquiry> inquiries) {
        if (inquiriesAdapter != null) {
            inquiriesAdapter.setList(inquiries);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setRetainInstance(true);
        inquiriesAdapter = new InquiriesAdapter(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        bus.unregister(this);
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
            setList(inquiries);
        }
    }

    @Subscribe
    public void onInquiryDeletedEventModel(InquiryDeletedEventModel event) {
        List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        setList(inquiries);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        List<LSInquiry> contacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//        setList(contacts);
        new ListPopulateAsync().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_lost);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_inquiries_delight));
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(inquiriesAdapter);
        listView.setEmptyView(errorScreenView);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if (materialSearchView != null) {
            materialSearchView.setMenuItem(item);
        }
    }

    @Override
    protected void onSearch(String query) {
        inquiriesAdapter.getFilter().filter(query);
    }

    class ListPopulateAsync extends AsyncTask<Void, String, Void> {
        List<LSInquiry> inquiries;
        ProgressDialog progressDialog;

        ListPopulateAsync() {
            super();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Loading data");
            //this method will be running on UI thread
            progressDialog.setMessage("Please Wait...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
            SystemClock.sleep(200);
            return (null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(String... item) {
            Log.d(TAG, "onProgressUpdate: "+item);
        }

        @Override
        protected void onPostExecute(Void unused) {
            setList(inquiries);
            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(getContext(), "onPostExecuteInquries", Toast.LENGTH_SHORT).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
