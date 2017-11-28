package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/9/2017.
 */

public class IgnoredActivity extends AppCompatActivity {
    private static final String TAG = "IgnoredActivity";
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private TinyBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignore);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ignored Contacts");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Collection<LSContact> contacts;
//        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
//
//        list.addAll(contacts);

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
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);

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

    @Subscribe
    public void onSaleContactAddedEventModel(LeadContactAddedEventModel event) {
        Collection<LSContact> contacts;
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
        list.clear();
        list.addAll(contacts);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "LeadContactAddedEventModel", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(ContactDeletedEventModel event) {
        Collection<LSContact> contacts;
        contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
        list.clear();
        list.addAll(contacts);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "ContactDeletedEventModel", Toast.LENGTH_SHORT).show();
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
}
