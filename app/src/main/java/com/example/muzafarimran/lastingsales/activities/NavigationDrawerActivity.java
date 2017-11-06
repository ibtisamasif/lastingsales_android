package com.example.muzafarimran.lastingsales.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.adapters.SampleFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.customview.BadgeView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.fragments.ColleagueFragment;
import com.example.muzafarimran.lastingsales.fragments.IgnoredFragment;
import com.example.muzafarimran.lastingsales.fragments.MoreFragment;
import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.service.DemoSyncJob;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.receivers.HourlyAlarmReceiver;
import com.example.muzafarimran.lastingsales.service.*;
import com.example.muzafarimran.lastingsales.sync.SyncLastSeen;
import com.example.muzafarimran.lastingsales.utils.AgentDataFetchAsync;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utilscallprocessing.ShortcutBadgeUpdateAsync;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchCallback, TabSelectedListener {
    private static final String TAG = "NaviDrawerActivity";
    MaterialSearchView searchView;
    SessionManager sessionManager;
    SettingsManager settingsManager;
    //    CallRecord callRecord;
    ImageView ivProfileImage;
    TextView tvProfileName, tvProfileNumber;
    boolean shouldShowSearchMenu = false;
    Toolbar toolbar;
    BadgeView badgeInquries;
    ImageView imageViewBadge;
    TabLayout tabLayout;
    TabLayout.Tab tab1;
    TinyBus bus;
    private ViewPager viewPager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(PhoneNumberAndCallUtils.numberToInterNationalNumber(NavigationDrawerActivity.this, "03247158218"));
//        if (lsContactProfile != null) {
//            lsContactProfile.delete();
//        }

        DemoSyncJob.schedulePeriodic();

//        AllContactProfilesFetchingEngineAsync profileEngineAsync = new AllContactProfilesFetchingEngineAsync(NavigationDrawerActivity.this);
//        profileEngineAsync.execute();

//        boolean ratingAlarm = (PendingIntent.getBroadcast(getApplicationContext(), 2, new Intent(NavigationDrawerActivity.this, RatingAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
//        if (ratingAlarm) {
//            Log.d("myAlarmLog", "Rating Alarm is already active");
//        } else {
//            Log.d("myAlarmLog", "Rating Alarm is activated now");
//            Calendar calendar2 = Calendar.getInstance();
//            calendar2.set(Calendar.HOUR_OF_DAY, 11); // For 11am
//            calendar2.set(Calendar.MINUTE, 0);
//            calendar2.set(Calendar.SECOND, 0);
//            PendingIntent pi2 = PendingIntent.getBroadcast(NavigationDrawerActivity.this, 2, new Intent(NavigationDrawerActivity.this, RatingAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager am2 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            am2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi2);
//        }

//        boolean dayStartHighlightAlarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent(NavigationDrawerActivity.this, DayStartHighlightAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
//        if (dayStartHighlightAlarmUp) {
//            Log.d("myAlarmLog", "DayStart Alarm is already active");
//        } else {
//            Log.d("myAlarmLog", "DayStart Alarm is activated now");
//            Calendar calendar2 = Calendar.getInstance();
//            calendar2.set(Calendar.HOUR_OF_DAY, 9); // For 9am
//            calendar2.set(Calendar.MINUTE, 0);
//            calendar2.set(Calendar.SECOND, 0);
//            PendingIntent pi2 = PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent(NavigationDrawerActivity.this, DayStartHighlightAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager am2 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            am2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi2);
//        }

        settingsManager = new SettingsManager(this);
        if (settingsManager.getKeyStateHourlyNotification()) {
            boolean hourlyAlarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NavigationDrawerActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
            if (hourlyAlarmUp) {
                Log.d("myAlarmLog", "Hourly Alarm is already active");
            } else {
                Log.d("myAlarmLog", "Hourly Alarm is activated now");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 10); // For 10am
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NavigationDrawerActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 2, pi);
            }
        }

        Log.d(TAG, "onCreate: Build.MANUFACTURER: " + Build.MANUFACTURER);
        Log.d(TAG, "onCreate: Build.BRAND: " + Build.BRAND);

        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            Log.d(TAG, "onCreate: xiaomi");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);


        } else if (Build.BRAND.equalsIgnoreCase("Letv")) {
            Log.d(TAG, "onCreate: Letv");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            startActivity(intent);

        } else if (Build.BRAND.equalsIgnoreCase("Honor")) {
            Log.d(TAG, "onCreate: Honor");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            startActivity(intent);

        }

        if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Is app killing?").setMessage("Add LastingSales to protected apps list to keep it running in background.")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
//                            sp.edit().putBoolean("protected",true).commit();
                        }
                    }).create().show();
        }

//        Intent intentTest = new Intent();
//        String packageName = NavigationDrawerActivity.this.getPackageName();
//        PowerManager pm = (PowerManager) NavigationDrawerActivity.this.getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (pm.isIgnoringBatteryOptimizations(packageName))
//                intentTest.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//            else {
//                intentTest.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intentTest.setData(Uri.parse("package:" + packageName));
//            }
//            NavigationDrawerActivity.this.startActivity(intentTest);
//        }

//        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            public void uncaughtException(Thread thread, Throwable ex) {
//                Log.d(TAG, "uncaughtException: ");
//                Log.d("testlog", "uncaughtException: ");
//                Intent launchIntent = new Intent(getIntent());
//                PendingIntent pending = PendingIntent.getActivity(NavigationDrawerActivity.this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                manager.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, pending);
//                defaultHandler.uncaughtException(thread, ex);
//                System.exit(2); //TODO why 2 i forgot and it seems buggy now?
//                Log.d("testlog", "uncaughtException: exit");
//            }
//        });

        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        mixpanel.track("Home Screen Opened");

        //TODO optimize it
        long contactCount = LSContact.count(LSContact.class);
        if (contactCount < 1) {
            Log.d(TAG, "onCreate: LSContact.count " + contactCount);
            AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(getApplicationContext());
            agentDataFetchAsync.execute();
        }

        //Version Control
        VersionManager versionManager = new VersionManager(getApplicationContext());
        if (!versionManager.runMigrations()) {
            // if migration has failed
            Toast.makeText(getApplicationContext(), "Migration Failed", Toast.LENGTH_SHORT).show();
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle bundle = new Bundle();
        //The following code logs a SELECT_CONTENT Event when a user clicks on a specific element in your app.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));

//        Log.d(TAG, "onCreate: DB name: " + getDatabasePath("sugar_example").getAbsolutePath());

        setContentView(R.layout.activity_navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_notification_small1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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
        } else {
            Intent intent = new Intent(NavigationDrawerActivity.this, CallDetectionService.class);
            startService(intent);
//            TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
//            theCallLogEngine.execute();
//            Log.d(TAG, "TheCallLogEngine: Started from Drawer");
        }

        LinearLayout navHeader = (LinearLayout) navigationView.getHeaderView(0);
        ivProfileImage = (ImageView) navHeader.findViewById(R.id.ivProfileImgNavBar);
        tvProfileName = (TextView) navHeader.findViewById(R.id.tvProfileNameNavBar);
        tvProfileNumber = (TextView) navHeader.findViewById(R.id.tvProfileNumberNavBar);
        tvProfileName.setText(sessionManager.getKeyLoginFirstName() + " " + sessionManager.getKeyLoginLastName());
        tvProfileNumber.setText(sessionManager.getLoginNumber());

        String url = sessionManager.getKeyLoginImagePath();
        Glide.with(this)
                .load(url)
                .error(R.drawable.ic_account_circle)
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
        tab1 = tabLayout.getTabAt(0);
        imageViewBadge = new ImageView(getApplicationContext());
        imageViewBadge.setImageResource(R.drawable.ic_phone_missed_white_48dp);
        tab1.setCustomView(imageViewBadge);
        badgeInquries = new BadgeView(getApplicationContext(), imageViewBadge);
        updateBadge();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                shouldShowSearchMenu = tab.getPosition() != 0;
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.ic_home_white_48dp);
                        getSupportActionBar().setTitle("Inquiries");
                        updateBadge();
                        showInquiryTutorials();
//                        String projectToken = MixpanelConfig.projectToken;
//                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
//                        try {
//                            JSONObject props = new JSONObject();
//                            props.put("Gender", "Female");
//                            props.put("Logged in", false);
//                            mixpanel.track("Unlabeled(Tab)", props);
//                        } catch (JSONException e) {
//                            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//                        }
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.menu_icon_phone_selected);
                        getSupportActionBar().setTitle("Home");
                        updateBadge();
//                        ((TextView)(toolbar.findViewById(R.id.title))).setText("CALL LOGS");
                        break;
                    case 2:
//                        tab.setIcon(R.drawable.menu_icon_contact_selected);
                        getSupportActionBar().setTitle("Sales Leads");
                        updateBadge();
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
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            String tab = bundle1.getString("SELECTED_TAB");
            if (tab != null) {
                if (tab.equals("INQUIRIES_TAB")) {
                    viewPager.setCurrentItem(0, true);
                }
            }
        }
//        // Updating Last Visit
////        Log.d("rating", "onCreate: setLastAppVisit");
////        long milliSecondsIn30Second = 60000; // 30 seconds for now
////        long now = Calendar.getInstance().getTimeInMillis();
////        long thirtySecondsAgoTimestamp = now - milliSecondsIn30Second;
////        sessionManager.setLastAppVisit("" + thirtySecondsAgoTimestamp);
        sessionManager.setLastAppVisit("" + Calendar.getInstance().getTimeInMillis());
        SyncLastSeen.updateLastSeenToServer(NavigationDrawerActivity.this);
    }


    private void showInquiryTutorials() {
        new MaterialShowcaseView.Builder(NavigationDrawerActivity.this)
                .setTarget(badgeInquries)
                .setDismissText("GOT IT")
                .setContentText("Here are your inquiries which you need to call back")
                .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("200") // provide a unique ID used to ensure it is only shown once
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this); //TODO Caused by: java.lang.IllegalStateException: You must call this method from the same thread, in which TinyBus was created. Created: Thread[AsyncTask #2,5,main], current thread: Thread[main,5,main]
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            updateBadge();
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onAnyIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            Log.d(TAG, "Incoming");
            updateBadge();
        }
    }

    @Subscribe
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onAnyOutGoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            Log.d(TAG, "Outgoing");
            updateBadge();
        }
    }

    private void updateBadge() {

        new BadgeUpdateAsync().execute();

//        List<LSInquiry> allPendingInquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//        if (allPendingInquiries != null && allPendingInquiries.size() > 0) {
//            badgeInquries.setText("" + allPendingInquiries.size());
//            badgeInquries.toggle();
//            badgeInquries.show();
//        } else {
//            badgeInquries.hide();
//        }
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
//        if (id == R.id.nav_item_incoming_call) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, AllCallsFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Calls");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
        if (id == R.id.nav_item_business_contacts) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, ColleagueFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Colleague Contacts");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
//            String projectToken = MixpanelConfig.projectToken;
//            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
//            try {
//                JSONObject props = new JSONObject();
//                props.put("Gender", "Female");
//                props.put("Logged in", false);
//                mixpanel.track("Colleague(Drawer)", props);
//            } catch (JSONException e) {
//                Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//            }
        } else if (id == R.id.nav_item_unlabeled_contacts) {
//            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UnlabeledFragment.class.getName());
//            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Unlabeled Leads");
//            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//            intent = new Intent(getApplicationContext(), FrameActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//            String projectToken = MixpanelConfig.projectToken;
//            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
//            try {
//                JSONObject props = new JSONObject();
//                props.put("Logged in", false);
//                mixpanel.track("Unlabeled(Drawer)", props);
//            } catch (JSONException e) {
//                Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//            }
        } else if (id == R.id.nav_item_personal_contacts) {
            bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, IgnoredFragment.class.getName());
            bundle.putString(FrameActivity.ACTIVITY_TITLE, "Ignored Contacts");
            bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
            intent = new Intent(getApplicationContext(), FrameActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
//            String projectToken = MixpanelConfig.projectToken;
//            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
//            try {
//                JSONObject props = new JSONObject();
//                props.put("Gender", "Female");
//                props.put("Logged in", false);
//                mixpanel.track("Ignored(Drawer)", props);
//            } catch (JSONException e) {
//                Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//            }
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
        else if (id == R.id.nav_item_refresh) {
//            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(PhoneNumberAndCallUtils.numberToInterNationalNumber(NavigationDrawerActivity.this, "+92 301 4775234"));
//            if (lsContactProfile != null) {
//                Log.d("ContactProfileProvider", "onNavigationItemSelected: DELETED ");
//                lsContactProfile.delete();
//            }
//            Intent tempIntent = new Intent(this, FireBaseMainActivity.class);
//            startActivity(tempIntent);
            AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(getApplicationContext());
            agentDataFetchAsync.execute();
//            RecordingManager recordingManager = new RecordingManager();
//            recordingManager.execute();
            TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
            theCallLogEngine.execute();
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
            dataSenderAsync.run();
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
            mixpanel.track("Refreshed");
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_item_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (id == R.id.nav_item_logout) {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LogInActivity.class));
            finish();
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
            mixpanel.track("User Logged Out");
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
                Log.d(TAG, "onPageSelected: ");
                if (position == 2) {
                    if (tag.equals("AllLeads")) {
                        TabSelectedListener tabSelectedListener = (TabSelectedListener) ((SampleFragmentPagerAdapter) viewPager.getAdapter()).getItem(position);
                        tabSelectedListener.onTabSelectedEvent(0, "");
                    } else if (tag.equals("InActiveLeads")) {
                        TabSelectedListener tabSelectedListener = (TabSelectedListener) ((SampleFragmentPagerAdapter) viewPager.getAdapter()).getItem(position);
                        tabSelectedListener.onTabSelectedEvent(4, "");
                    }
                }
                viewPager.removeOnPageChangeListener(this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: ");
            }
        });
        viewPager.setCurrentItem(position, true);

    }

    private class BadgeUpdateAsync extends AsyncTask<Void, String, Void> {
        //        ProgressDialog progressDialog;
        List<LSInquiry> allPendingInquiries;

        BadgeUpdateAsync() {
            super();
//            progressDialog = new ProgressDialog(NavigationDrawerActivity.this);
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
            allPendingInquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//            SystemClock.sleep(200);
            return (null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(String... item) {
            Log.d(TAG, "onProgressUpdate: " + item);
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(getContext(), "onPostExecuteWon", Toast.LENGTH_SHORT).show();
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
            if (allPendingInquiries != null && allPendingInquiries.size() > 0) {
                badgeInquries.setText("" + allPendingInquiries.size());
                badgeInquries.toggle();
                badgeInquries.show();
                new ShortcutBadgeUpdateAsync(NavigationDrawerActivity.this).execute();
            } else {
                badgeInquries.hide();
            }
        }
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