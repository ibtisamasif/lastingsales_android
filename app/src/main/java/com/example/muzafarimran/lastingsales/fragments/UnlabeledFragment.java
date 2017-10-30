package com.example.muzafarimran.lastingsales.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.UnlabeledAdapter;
import com.example.muzafarimran.lastingsales.providers.loaders.UnlabeledLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnlabeledFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<LSContact>>{

    private static final String TAG = "UntaggedCallFragment";
    UnlabeledAdapter unlabeledAdapter;
    ListView listView = null;
    private List<LSContact> untaggedContacts = new ArrayList<>();
    private Bus mBus;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;
    private MaterialSearchView searchView;

    public static UnlabeledFragment newInstance(int page, String title) {
        UnlabeledFragment fragmentFirst = new UnlabeledFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (unlabeledAdapter != null) {
            unlabeledAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setRetainInstance(true);
        unlabeledAdapter = new UnlabeledAdapter(getContext());
        unlabeledAdapter.setList(untaggedContacts);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onUntaggedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            updateContactssList();
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            updateContactssList();
        }
    }

    @Subscribe
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onOutgoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            updateContactssList();
        }
    }

    private void updateContactssList() {

        List<LSContact> untaggedContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
        this.untaggedContacts = untaggedContacts;
        setList(untaggedContacts);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
//        updateContactssList();
        getLoaderManager().initLoader(1, null, this).forceLoad();
//        new ListPopulateAsync().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_unlabeled, container, false);


//        ExpandableHeightListView expandableListView = (ExpandableHeightListView) view.findViewById(R.id.calls_list);
//
//        expandableListView.setAdapter(unlabeledAdapter);
//
//        // This actually does the magic
//        expandableListView.setExpanded(true);
//
//        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
//        errorScreenView.setErrorImage(R.drawable.delight_inactive);
//        errorScreenView.setErrorText(this.getResources().getString(R.string.em_unlabeled_delight));
//        expandableListView.setEmptyView(errorScreenView);


        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(unlabeledAdapter);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_inactive);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_unlabeled_delight));
        listView.setEmptyView(errorScreenView);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                unlabeledAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                unlabeledAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(searchView)
                .setDismissText("GOT IT")
                .setContentText("These are your customers you have talked to and needed to get marked them as leads.")
                .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("300") // provide a unique ID used to ensure it is only shown once
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<LSContact>> onCreateLoader(int i, Bundle bundle) {
        return new UnlabeledLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<LSContact>> loader, List<LSContact> lsContacts) {
        Log.d(TAG, "onLoadFinished: ");
        unlabeledAdapter.setList(lsContacts);
    }

    @Override
    public void onLoaderReset(Loader<List<LSContact>> loader) {
        Log.d(TAG, "onLoaderReset: ");
        unlabeledAdapter.setList(new ArrayList<LSContact>());
    }

//    class ListPopulateAsync extends AsyncTask<Void, String, Void> {
//        List<LSContact> contacts;
////        ProgressDialog progressDialog;
//
//        ListPopulateAsync() {
//            super();
////            progressDialog = new ProgressDialog(getContext());
////            progressDialog.setTitle("Loading data");
////            //this method will be running on UI thread
////            progressDialog.setMessage("Please Wait...");
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d(TAG, "onPreExecute: ");
////            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... unused) {
//            contacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
////            SystemClock.sleep(200);
//            return (null);
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void onProgressUpdate(String... item) {
//            Log.d(TAG, "onProgressUpdate: " + item);
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            setList(contacts);
//            Log.d(TAG, "onPostExecute: ");
////            Toast.makeText(getContext(), "onPostExecuteUnlabeled", Toast.LENGTH_SHORT).show();
////            if (progressDialog != null && progressDialog.isShowing()) {
////                progressDialog.dismiss();
////            }
//        }
//    }
}