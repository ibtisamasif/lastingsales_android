package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.Utils.CallRecord;
import com.example.muzafarimran.lastingsales.adapters.SampleFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.fragments.CollegueFragment;
import com.example.muzafarimran.lastingsales.fragments.IncomingCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.MoreFragment;
import com.example.muzafarimran.lastingsales.fragments.NonbusinessFragment;
import com.example.muzafarimran.lastingsales.fragments.OutgoingCallsFragment;
import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Locale;

import de.halfbit.tinybus.TinyBus;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchCallback {

    MaterialSearchView searchView;
    SessionManager sessionManager;
    CallRecord callRecord;
    ImageView ivProfileImage;
    boolean shouldShowSearchMenu = false;
    private tabSelectedListener tabselectedlistener = new tabSelectedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        LinearLayout navHeader = (LinearLayout) navigationView.getHeaderView(0);
        ivProfileImage = (ImageView) navHeader.findViewById(R.id.ivProfileNavBar);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), NavigationDrawerActivity.this));
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, MoreFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Profile");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                intent = new Intent(getApplicationContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                shouldShowSearchMenu = tab.getPosition() != 0;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("CallRecordFile")
                .setRecordDirName("Record_" + new java.text.SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.US))
                .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setShowSeed(true)
                .buildService();
        callRecord.startCallRecordService();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        BackPressedEventModel model = new BackPressedEventModel();
        TinyBus.from(getApplicationContext()).post(model);
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!model.backPressHandled) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        Bundle bundle = new Bundle();
        if (id == R.id.nav_item_incoming_call) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, IncomingCallsFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Incoming Calls");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.nav_item_outgoing_call) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, OutgoingCallsFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Outgoing Calls");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.nav_item_colleague_contacts) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, CollegueFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Colleague Contacts");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.nav_item_personal_contacts) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, NonbusinessFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Personal Contacts");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public MaterialSearchView getSearchView() {
        return searchView;
    }

    public class tabSelectedListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
//                    tab.setIcon(R.drawable.menu_icon_home_selected_aqua);
                    break;
                case 1:
//                    tab.setIcon(R.drawable.menu_icon_phone_selected_aqua);
                    //((TextView)(myToolbar.findViewById(R.id.title))).setText("CALL LOGS");
                    break;
                case 2:
//                    tab.setIcon(R.drawable.menu_icon_contact_selected_aqua);
                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("CONTACTS");
                    break;
                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu_selected_aqua);
                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("MENU");
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
//                    tab.setIcon(R.drawable.menu_icon_home);
                    break;
                case 1:
//                    tab.setIcon(R.drawable.menu_icon_phone);
                    break;
                case 2:
//                    tab.setIcon(R.drawable.menu_icon_contact);
                    break;
                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu);
                    break;
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            //int position = tab.getPosition();
        }
    }
}