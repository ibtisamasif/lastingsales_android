package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import de.halfbit.tinybus.TinyBus;

public class FrameActivity extends AppCompatActivity implements SearchCallback {

    public static final String FRAGMENT_NAME_STRING = "fragment_name";
    public static final String FRAGMENT_BUNDLE_PASS = "fragment_bundle";
    public static final String INFLATE_OPTIONS_MENU = "inflate_options_menu";
    public static final String ACTIVITY_TITLE = "activity_title";
    MaterialSearchView searchView;
    boolean inflateOptionsMenu = true;
    private String fragmentName = null;
    private String activityTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragmentName = bundle.getString(FRAGMENT_NAME_STRING);
            inflateOptionsMenu = bundle.getBoolean(INFLATE_OPTIONS_MENU);
            activityTitle = bundle.getString(ACTIVITY_TITLE);
        }
        if (fragmentName != null) {
            Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentName);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.mainFrameLayoutFrameActivity, fragment);
            transaction.commit();
        } else {
            Toast.makeText(this, "No Fragment to Launch!", Toast.LENGTH_SHORT).show();
        }
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        getSupportActionBar().setTitle(activityTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (inflateOptionsMenu) {
            getMenuInflater().inflate(R.menu.search_options_menu, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            searchView.setMenuItem(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackPressedEventModel model = new BackPressedEventModel();
        TinyBus.from(getApplicationContext()).post(model);
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if (!model.backPressHandled) {
            super.onBackPressed();
        }
    }

    @Override
    public MaterialSearchView getSearchView() {
        return searchView;
    }
}