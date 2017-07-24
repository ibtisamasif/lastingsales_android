package com.example.muzafarimran.lastingsales.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.LeadsAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class InProgressFragment extends TabFragment {

    public static final String TAG = "InProgressFragment";
    ListView listView = null;
    LeadsAdapter leadsAdapter;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public static InProgressFragment newInstance(int page, String title) {
        InProgressFragment fragmentFirst = new InProgressFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (leadsAdapter != null) {
            leadsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        leadsAdapter = new LeadsAdapter(getContext(), null, LSContact.SALES_STATUS_INPROGRESS); // TODO remove this line as it populates all contacts have inprogress status including ignored,business
    }

    @Override
    public void onResume() {
        super.onResume();
//        List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//        setList(contacts);
        new ListPopulateAsync().execute();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe(mode = Subscribe.Mode.Main)
    public void onLeadContactAddedEventModel(LeadContactAddedEventModel event) {
        List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        setList(contacts);
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(ContactDeletedEventModel event) {
        List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        setList(contacts);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && leadsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            leadsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        listView = (ListView) view.findViewById(R.id.leads_contacts_list);
        listView.setAdapter(leadsAdapter);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_inprogress);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_inprogress_delight));
        listView.setEmptyView(errorScreenView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.search_options_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        if(materialSearchView!=null) {
//            materialSearchView.setMenuItem(item);
//        }
//    }
//
//    @Override
//    protected void onSearch(String query) {
//        allAdapter.getFilter().filter(query);
//    }

    class ListPopulateAsync extends AsyncTask<Void, String, Void> {
        List<LSContact> contacts;
//        ProgressDialog progressDialog;

        ListPopulateAsync() {
            super();
//            progressDialog = new ProgressDialog(getContext());
//            progressDialog.setTitle("Loading data");
//            //this method will be running on UI thread
//            progressDialog.setMessage("Please Wait...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
            SystemClock.sleep(200);
            return (null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(String... item) {
            Log.d(TAG, "onProgressUpdate: " + item);
        }

        @Override
        protected void onPostExecute(Void unused) {
            setList(contacts);
            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(getContext(), "onPostExecuteInProgress", Toast.LENGTH_SHORT).show();
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
        }
    }
}
