package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/9/2017.
 */

public class ColleagueActivity extends AppCompatActivity {
    private static final String TAG = "ColleagueActivity";
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private TinyBus bus;

    private static ContactCallDetailsBottomSheetFragment contactCallDetailsBottomSheetFragment;
    private static boolean sheetShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colleague);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Colleague Contacts");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, list);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Collection<LSContact> contacts;
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_BUSINESS);
        list.clear();
        list.addAll(contacts);
        adapter.notifyDataSetChanged();
    }


    @Subscribe
    public void onLeadContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onLeadContactAddedEventModel: ");
        Collection<LSContact> contacts;
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_BUSINESS);
        list.clear();
        list.addAll(contacts);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(ContactDeletedEventModel event) {
        Log.d(TAG, "onLeadContactDeletedEventModel: ");
        Collection<LSContact> contacts;
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_BUSINESS);
        list.clear();
        list.addAll(contacts);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openContactBottomSheetCallback(Long contact_id) {
        contactCallDetailsBottomSheetFragment = ContactCallDetailsBottomSheetFragment.newInstance(contact_id, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        contactCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }
}
