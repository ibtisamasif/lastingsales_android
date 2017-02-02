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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.adapters.SampleFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.customview.BadgeView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.fragments.AllCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.MoreFragment;
import com.example.muzafarimran.lastingsales.fragments.NonbusinessFragment;
import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.DataSenderNew;
import com.example.muzafarimran.lastingsales.utils.CallRecord;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;
import java.util.Locale;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchCallback, TabSelectedListener {
    private static final String TAG = "NaviDrawerActivity";
    MaterialSearchView searchView;
    SessionManager sessionManager;
    CallRecord callRecord;
    ImageView ivProfileImage;
    TextView tvProfileName, tvProfileNumber;
    boolean shouldShowSearchMenu = false;
    Toolbar toolbar;
    BadgeView badgeInquries;
    ImageView imageViewBadge;
    TabLayout tabLayout;
    TabLayout.Tab tab1;
    TinyBus bus;
//    private tabSelectedListener tabselectedlistener = new tabSelectedListener();
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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
        LinearLayout navHeader = (LinearLayout) navigationView.getHeaderView(0);
        ivProfileImage = (ImageView) navHeader.findViewById(R.id.ivProfileImgNavBar);
        tvProfileName = (TextView) navHeader.findViewById(R.id.tvProfileNameNavBar);
        tvProfileNumber = (TextView) navHeader.findViewById(R.id.tvProfileNumberNavBar);
        tvProfileName.setText(sessionManager.getKeyLoginFirstName()+ " " + sessionManager.getKeyLoginLastName());
        tvProfileNumber.setText(sessionManager.getLoginNumber());

        String url = sessionManager.getKeyLoginImagePath();
        Glide.with(this)
                .load(url)
                .into(ivProfileImage);

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), NavigationDrawerActivity.this));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(1);
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home_white_48dp);
//        tabLayout.getTabAt(1).setIcon(R.drawable.menu_icon_phone);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_white_48dp);

        List<LSInquiry> allInquiries = LSInquiry.getAllInquiriesInDescendingOrder();

        tab1 = tabLayout.getTabAt(0);
        imageViewBadge = new ImageView(getApplicationContext());
        imageViewBadge.setImageResource(R.drawable.ic_phone_missed_white_48dp);
        tab1.setCustomView(imageViewBadge);
        badgeInquries = new BadgeView(getApplicationContext(), imageViewBadge);
        if (allInquiries != null && allInquiries.size() > 0) {
            badgeInquries.setText("" + allInquiries.size());
            badgeInquries.toggle();
            badgeInquries.show();
        } else {
            badgeInquries.hide();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                shouldShowSearchMenu = tab.getPosition() != 0;
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.ic_home_white_48dp);
                        getSupportActionBar().setTitle("Inquiries");
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.menu_icon_phone_selected);
                        getSupportActionBar().setTitle("Home");
//                        ((TextView)(toolbar.findViewById(R.id.title))).setText("CALL LOGS");
                        break;
                    case 2:
//                        tab.setIcon(R.drawable.menu_icon_contact_selected);
                        getSupportActionBar().setTitle("Leads");
                        // ((TextView)(myToolbar.findViewById(R.id.title))).setText("CONTACTS");
                        break;
//                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu_selected_aqua);
//                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("MENU");
//                    break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.menu_icon_home);
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.menu_icon_phone);
                        break;
                    case 2:
//                        tab.setIcon(R.drawable.menu_icon_contact);
                        break;
//                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu);
//                    break;
                }
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
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    //TODO Does this work on fragments only ?
    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "NavionMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            List<LSInquiry> allInquiries = LSInquiry.getAllInquiriesInDescendingOrder();
//            tab1 = tabLayout.getTabAt(1);
//            imageViewBadge.setImageResource(R.drawable.menu_icon_phone);
//            tab1.setCustomView(imageViewBadge);
            //badgeInquries = new BadgeView(getApplicationContext(), imageViewBadge);
            if (allInquiries != null && allInquiries.size() > 0) {
                badgeInquries.setText("" + allInquiries.size());
                badgeInquries.toggle();
                badgeInquries.show();
            } else {
                badgeInquries.hide();
            }
        }
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
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, AllCallsFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Calls");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
//        else if (id == R.id.nav_item_outgoing_call) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, OutgoingCallsFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Outgoing Calls");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//        else if (id == R.id.nav_item_colleague_contacts) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, CollegueFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Colleague Contacts");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
        else if (id == R.id.nav_item_personal_contacts) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, NonbusinessFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Untracked Contacts");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
//        else if (id == R.id.nav_item_followups) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsListFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Followups List");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//        else if (id == R.id.nav_item_notes) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, ContactsListForNotesFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Notes List");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
        else if (id == R.id.nav_item_feedback) {
            DataSenderNew dataSenderNew = new DataSenderNew(getApplicationContext());
            dataSenderNew.execute();
            Toast.makeText(this, "FeedbackScreen", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item_logout) {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public MaterialSearchView getSearchView() {
        return searchView;
    }

    @Override
    public void onTabSelectedEvent(int position, final String tag) {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    TabSelectedListener tabSelectedListener = (TabSelectedListener) ((SampleFragmentPagerAdapter) viewPager.getAdapter()).getItem(position);
                    tabSelectedListener.onTabSelectedEvent(4, "");
                }
                viewPager.removeOnPageChangeListener(this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(position, true);

    }

//    public class tabSelectedListener implements TabLayout.OnTabSelectedListener {
//
//        @Override
//        public void onTabSelected(TabLayout.Tab tab) {
//            switch (tab.getPosition()) {
//                case 0:
//                    tab.setIcon(R.drawable.menu_icon_home_selected_aqua);
//                    break;
//                case 1:
//                    tab.setIcon(R.drawable.menu_icon_phone_selected_aqua);
//                    //((TextView)(myToolbar.findViewById(R.id.title))).setText("CALL LOGS");
//                    break;
//                case 2:
//                    tab.setIcon(R.drawable.menu_icon_contact_selected_aqua);
//                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("CONTACTS");
//                    break;
////                case 3:
////                    tab.setIcon(R.drawable.menu_icon_menu_selected_aqua);
////                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("MENU");
////                    break;
//            }
//        }
//
//        @Override
//        public void onTabUnselected(TabLayout.Tab tab) {
//            switch (tab.getPosition()) {
//                case 0:
//                    tab.setIcon(R.drawable.menu_icon_home);
//                    break;
//                case 1:
//                    tab.setIcon(R.drawable.menu_icon_phone);
//                    break;
//                case 2:
//                    tab.setIcon(R.drawable.menu_icon_contact);
//                    break;
////                case 3:
////                    tab.setIcon(R.drawable.menu_icon_menu);
////                    break;
//            }
//        }
//
//        @Override
//        public void onTabReselected(TabLayout.Tab tab) {
//            //int position = tab.getPosition();
//        }
//    }
}