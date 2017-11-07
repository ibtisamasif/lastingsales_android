package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.home.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.home.SeparatorItem;
import com.example.muzafarimran.lastingsales.listloaders.HomeLoader;
import com.example.muzafarimran.lastingsales.listloaders.InquiryLoader;
import com.example.muzafarimran.lastingsales.listloaders.LeadsLoader;
import com.example.muzafarimran.lastingsales.listloaders.MoreLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/6/2017.
 */

public class NavigationBottomMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "NavigationBottomMain";
    private TinyBus bus;
    private SessionManager sessionManager;

    private List<Object> list = new ArrayList<Object>();

    private MyRecyclerViewAdapter adapter;

    private RecyclerView mRecyclerView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inquiries:
                    getSupportLoaderManager().initLoader(1, null, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_home:
                    getSupportLoaderManager().initLoader(2, null, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_leads:
                    getSupportLoaderManager().initLoader(3, null, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_more:
                    getSupportLoaderManager().initLoader(4, null, NavigationBottomMainActivity.this).forceLoad();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        sessionManager = new SessionManager(this);

        Collection<SeparatorItem> listSeparator = new ArrayList<SeparatorItem>();

        SeparatorItem spItem = new SeparatorItem();
        spItem.text = "unlabeled contacts";
        listSeparator.add(spItem);

        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);

        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();

        list.addAll(inquiriesContacts);
        list.addAll(listSeparator);
        list.addAll(unlabeledContacts);

        adapter = new MyRecyclerViewAdapter(this, list);

        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 1:
                return new InquiryLoader(NavigationBottomMainActivity.this);
            case 2:
                return new HomeLoader(NavigationBottomMainActivity.this);
            case 3:
                return new LeadsLoader(NavigationBottomMainActivity.this);
            case 4:
                return new MoreLoader(NavigationBottomMainActivity.this);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        list.clear();
        list.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        list.clear();
        list.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
    }
}
