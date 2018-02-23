package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.app.ClassManager;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.customview.BottomNavigationViewHelper;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragmentNew;
import com.example.muzafarimran.lastingsales.fragments.InquiryCallDetailsBottomSheetFragmentNew;
import com.example.muzafarimran.lastingsales.listeners.ChipClickListener;
import com.example.muzafarimran.lastingsales.listeners.CloseContactBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listeners.CloseInquiryBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listloaders.HomeLoader;
import com.example.muzafarimran.lastingsales.listloaders.InquiryLoader;
import com.example.muzafarimran.lastingsales.listloaders.LeadsLoader;
import com.example.muzafarimran.lastingsales.listloaders.MoreLoader;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.receivers.HourlyAlarmReceiver;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.recycleradapter.SearchSuggestionAdapter;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.service.DemoSyncJob;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncLastSeen;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import de.halfbit.tinybus.wires.ShakeEventWire;

/**
 * Created by ibtisam on 11/6/2017.
 */

public class NavigationBottomMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Object>>, CloseInquiryBottomSheetEvent, CloseContactBottomSheetEvent, ChipClickListener {
    public static final String TAG = "NavigationBottomMain";

    public static final String KEY_ACTIVE_LOADER = "active_loader";
    public static int ACTIVE_LOADER = -1;
    public static final int INQU_LOADER_ID = 1;
    public static final int HOME_LOADER_ID = 2;
    public static final int LEAD_LOADER_ID = 3;
    public static final int MORE_LOADER_ID = 4;
    public static String KEY_SELECTED_TAB = "key_selected_tab";
    public static String INQUIRIES_TAB = "inquiries_tab";
    private TinyBus bus;
    private List<Object> list = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SessionManager sessionManager;
    private SettingsManager settingsManager;
    Bundle bundle = new Bundle();
    private SearchView searchView;
    private FloatingActionMenu floatingActionMenu;
    private BottomNavigationView navigation;
    private static InquiryCallDetailsBottomSheetFragmentNew inquiryCallDetailsBottomSheetFragment;
    private static ContactCallDetailsBottomSheetFragmentNew contactCallDetailsBottomSheetFragment;
    public static Activity activity;
    private static boolean sheetShowing = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (ACTIVE_LOADER != -1) {
                try {
                    Log.d(TAG, "onNavigationItemSelected: ACTIVE_LOADER: " + ACTIVE_LOADER);
                    if (getSupportLoaderManager().getLoader(ACTIVE_LOADER).isStarted()) { //still crashing here
                        getSupportLoaderManager().getLoader(ACTIVE_LOADER).cancelLoad();
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Exception caught");
                    FirebaseCrash.report(e);
                }
            }
            switch (item.getItemId()) {
//                case R.id.navigation_tasks:
//                    ACTIVE_LOADER = 1;
//                    floatingActionMenu.hideMenu(true);
//                    getSupportLoaderManager().restartLoader(1, bundle, NavigationBottomMainActivity.this).forceLoad();
////                    getSupportLoaderManager().initLoader(1, null, NavigationBottomMainActivity.this);
//                    return true;
                case R.id.navigation_inquiries:
                    ACTIVE_LOADER = INQU_LOADER_ID;
                    floatingActionMenu.hideMenu(true);
                    getSupportLoaderManager().restartLoader(INQU_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                    getSupportLoaderManager().initLoader(INQU_LOADER_ID, null, NavigationBottomMainActivity.this);
                    return true;
                case R.id.navigation_home:
                    ACTIVE_LOADER = HOME_LOADER_ID;
                    floatingActionMenu.hideMenu(true);
                    getSupportLoaderManager().restartLoader(HOME_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_leads:
                    ACTIVE_LOADER = LEAD_LOADER_ID;
                    floatingActionMenu.showMenu(true);
                    getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_more:
                    ACTIVE_LOADER = MORE_LOADER_ID;
                    floatingActionMenu.hideMenu(true);
                    getSupportLoaderManager().restartLoader(MORE_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        initFirst(savedInstanceState);

        setContentView(R.layout.activity_bottom_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        ActionBar actionBar = getSupportActionBar();
        adapter = new MyRecyclerViewAdapter(this, list);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mRecyclerView.setAdapter(adapter);

        initLast();

//        SwipeController swipeController = new SwipeController();
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(mRecyclerView);
//
//        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                super.onDraw(c, parent, state);
//            }
//        });

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        FloatingActionButton floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_add);
        FloatingActionButton floatingActionButtonImport = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);

                Intent intent = new Intent(NavigationBottomMainActivity.this, AddEditLeadActivity.class);
                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
                startActivity(intent);
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(NavigationBottomMainActivity.this, projectToken);
                mixpanel.track("Create lead dialog - opened");

//                Intent intent = new Intent(getContext(), AddEditLeadActivity.class);
//                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
//                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
//                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);

                Intent intent1 = new Intent(NavigationBottomMainActivity.this, AddEditLeadActivity.class);
                intent1.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent1.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
                startActivity(intent1);
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(NavigationBottomMainActivity.this, projectToken);
                mixpanel.track("Create lead dialog - opened");

//                Intent intent = new Intent(getContext(), AddEditLeadActivity.class);
//                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
//                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
//                startActivity(intent);
            }
        });

        onBackToActivity();

    }

    private void onBackToActivity() {
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            Log.d(TAG, "onCreate: Loading Inquiries TAB");
            String tab = bundle1.getString(KEY_SELECTED_TAB);
            if (tab != null) {
                if (tab.equals(INQUIRIES_TAB)) {
                    getSupportLoaderManager().initLoader(INQU_LOADER_ID, null, NavigationBottomMainActivity.this).forceLoad();
                    navigation.setSelectedItemId(R.id.navigation_inquiries);
                }
            } else {
                Log.d(TAG, "onCreate: Bundle Not Null Loading Leads TAB");
                getSupportLoaderManager().initLoader(LEAD_LOADER_ID, null, NavigationBottomMainActivity.this).forceLoad();
                navigation.setSelectedItemId(R.id.navigation_leads);
            }
        } else
            Log.d(TAG, "onCreate: Bundle is Null Loading Leads TAB");
        getSupportLoaderManager().initLoader(LEAD_LOADER_ID, null, NavigationBottomMainActivity.this).forceLoad();
        navigation.setSelectedItemId(R.id.navigation_leads);
    }


    private void initFirst(Bundle savedInstanceState) {
        Log.d(TAG, "initFirst: density: " + getResources().getDisplayMetrics().density);
        activity = this;
        sessionManager = new SessionManager(getApplicationContext());
        bus = TinyBus.from(this.getApplicationContext());
        if (savedInstanceState == null) {
            // Note: ShakeEventWire stays wired when activity is re-created
            //       on configuration change. That's why we register is
            //       only once inside if-statement.

            // wire device shake event provider
            bus.wire(new ShakeEventWire());
        }

        checkForInvalidTime();
        if (!sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
            return;
        } else {
            Intent intent = new Intent(NavigationBottomMainActivity.this, CallDetectionService.class);
            startService(intent);
            if (sessionManager.isFirstRunAfterLogin()) {
                Log.d(TAG, "initFirst: isFirstRun TRUE");
                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                theCallLogEngine.execute();
            }
            sessionManager.setLastAppVisit("" + Calendar.getInstance().getTimeInMillis());
            if (NetworkAccess.isNetworkAvailable(this)) {
                long contactCount = LSContact.count(LSContact.class); // If app is crashed here make sure instant run if off.
                if (contactCount < 1) {
                    Log.d(TAG, "onCreate: LSContact.count " + contactCount);
                    sessionManager.fetchData();
                }
                SyncLastSeen.updateLastSeenToServer(NavigationBottomMainActivity.this);
//                Log.d("rating", "onCreate: setLastAppVisit");
//                long milliSecondsIn30Second = 60000; // 30 seconds for now
//                long now = Calendar.getInstance().getTimeInMillis();
//                long thirtySecondsAgoTimestamp = now - milliSecondsIn30Second;
//                sessionManager.setLastAppVisit("" + thirtySecondsAgoTimestamp);
            }
        }
    }

    private void checkForInvalidTime() {
        String lastAppVisitTime = sessionManager.getLastAppVisit();
        if (lastAppVisitTime != null && !lastAppVisitTime.equals("")) {
            Long lastAppVisitTimeLong = Long.parseLong(lastAppVisitTime);
            Long timeNowLong = Calendar.getInstance().getTimeInMillis();
            Log.d(TAG, "checkForInvalidTime: getLastAppVisit: " + lastAppVisitTimeLong);
            Log.d(TAG, "checkForInvalidTime: getSystemTime  : " + timeNowLong);
            if (timeNowLong < lastAppVisitTimeLong) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NavigationBottomMainActivity.this);
                alert.setTitle("Wrong time");
                alert.setMessage("Your systems date/time needs to be adjusted");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                    finish();
                    }
                });
                alert.show();
            }
        }
    }

    private void initLast() {

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle bundle = new Bundle();
        //The following code logs a SELECT_CONTENT Event when a user clicks on a specific element in your app.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        DemoSyncJob.schedulePeriodic();

        settingsManager = new SettingsManager(this);
        if (settingsManager.getKeyStateHourlyNotification()) {
            boolean hourlyAlarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NavigationBottomMainActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
            if (hourlyAlarmUp) {
                Log.d("myAlarmLog", "Hourly Alarm is already active");
            } else {
                Log.d("myAlarmLog", "Hourly Alarm is activated now");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 10); // For 10am
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NavigationBottomMainActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 2, pi);
            }
        }
        Log.d(TAG, "onCreate: Build.MANUFACTURER: " + Build.MANUFACTURER);
        Log.d(TAG, "onCreate: Build.BRAND: " + Build.BRAND);

//        Toast.makeText(this, "onCreate: Build.MANUFACTURER: " + Build.MANUFACTURER, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "onCreate: Build.BRAND: " + Build.BRAND, Toast.LENGTH_SHORT).show();

        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi") && !"google".equalsIgnoreCase(Build.BRAND) && !settingsManager.getKeyStateProtectedApp()) {
            Log.d(TAG, "onCreate: xiaomi");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Is app killing?").setMessage("Add LastingSales to protected apps list to keep it running in background.")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                startActivity(intent);
                                settingsManager.setKeyStateProtectedApp(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).create().show();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("Letv") && !"google".equalsIgnoreCase(Build.BRAND) && !settingsManager.getKeyStateProtectedApp()) {
            Log.d(TAG, "onCreate: Letv");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Is app killing?").setMessage("Add LastingSales to protected apps list to keep it running in background.")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                                startActivity(intent);
                                settingsManager.setKeyStateProtectedApp(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).create().show();
        }
//        else if (Build.MANUFACTURER.equalsIgnoreCase("Honor") && !"google".equalsIgnoreCase(Build.BRAND) && !settingsManager.getKeyStateProtectedApp()) {
//            Log.d(TAG, "onCreate: Honor");
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Is app killing?").setMessage("Add LastingSales to protected apps list to keep it running in background.")
//                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            try {
//                                Intent intent = new Intent();
//                                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                                startActivity(intent);
//                                settingsManager.setKeyStateProtectedApp(true);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).create().show();
//        }
//        else if (Build.MANUFACTURER.equalsIgnoreCase("Huawei") && !"google".equalsIgnoreCase(Build.BRAND) && !settingsManager.getKeyStateProtectedApp()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Is app killing?").setMessage("Add LastingSales to protected apps list to keep it running in background.")
//                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            try {
//                                Intent intent = new Intent();
//                                intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
//                                if (isCallable(intent)) {
////                                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                                    startActivity(intent);
//                                    settingsManager.setKeyStateProtectedApp(true);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).create().show(); // pistis phone crashed window leaked  android.view.WindowLeaked: Activity com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity has leaked window com.android.internal.policy.impl.PhoneWindow$DecorView{13adf36b V.E..... R.....ID 0,0-683,378} that was originally added here
//        }


//        Intent intentTest = new Intent();
//        String packageName = NavigationBottomMainActivity.this.getPackageName();
//        PowerManager pm = (PowerManager) NavigationBottomMainActivity.this.getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (pm.isIgnoringBatteryOptimizations(packageName))
//                intentTest.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//            else {
//                intentTest.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intentTest.setData(Uri.parse("package:" + packageName));
//            }
//            NavigationBottomMainActivity.this.startActivity(intentTest);
//        }

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.d(TAG, "uncaughtException: ");
                Intent launchIntent = new Intent(getIntent());
                PendingIntent pending = PendingIntent.getActivity(NavigationBottomMainActivity.this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, pending);
                defaultHandler.uncaughtException(thread, ex);
                System.exit(2);
            }
        });

        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        mixpanel.track("Home Screen Opened");

        //Version Control
        VersionManager versionManager = new VersionManager(getApplicationContext());
        if (!versionManager.runMigrations()) {
            // if migration has failed
            Toast.makeText(getApplicationContext(), "Migration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_ACTIVE_LOADER, ACTIVE_LOADER);
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ACTIVE_LOADER = savedInstanceState.getInt(KEY_ACTIVE_LOADER);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
//        Bundle bundle1 = getIntent().getExtras();
//        if (bundle1 != null) {
//            Log.d(TAG, "onCreate: Loading Inquiries TAB");
//            String tab = bundle1.getString(KEY_SELECTED_TAB);
//            if (tab != null) {
//                if (tab.equals(INQUIRIES_TAB)) {
//                    getSupportLoaderManager().initLoader(HOME_LOADER_ID, null, NavigationBottomMainActivity.this).forceLoad();
//                    navigation.setSelectedItemId(R.id.navigation_inquiries);
//                }
//            } else {
//                Log.d(TAG, "onCreate: Bundle Not Null Loading Leads TAB");
//                getSupportLoaderManager().initLoader(4, null, NavigationBottomMainActivity.this).forceLoad();
//                navigation.setSelectedItemId(R.id.navigation_leads);
//            }
//        } else {
//            if (backPressed) {
//                // Do nothing
//            } else {
//                Log.d(TAG, "onCreate: Bundle is Null Loading Leads TAB");
//                getSupportLoaderManager().initLoader(4, null, NavigationBottomMainActivity.this).forceLoad();
//                navigation.setSelectedItemId(R.id.navigation_leads);
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() called");
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            if (ACTIVE_LOADER != -1) {
//            if (getSupportLoaderManager().hasRunningLoaders()) {
                try {
                    if (ACTIVE_LOADER == INQU_LOADER_ID || ACTIVE_LOADER == HOME_LOADER_ID || ACTIVE_LOADER == MORE_LOADER_ID ) {
                        getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                        ACTIVE_LOADER = LEAD_LOADER_ID;
                        floatingActionMenu.showMenu(true);
                        navigation.setSelectedItemId(R.id.navigation_leads);
                    } else if (ACTIVE_LOADER == LEAD_LOADER_ID) {
                        super.onBackPressed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
            }
        }
    }

    @Subscribe
    public void onShakeEvent(ShakeEventWire.ShakeEvent event) {
        // device has been shaken
        Log.d(TAG, "onShakeEvent: Shake Event: " + event);
        sessionManager.fetchData();
        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
        theCallLogEngine.execute();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
        dataSenderAsync.run();
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        mixpanel.track("Shaked");
    }

//    @Subscribe
//    public void onTaskAddedEventModel(TaskAddedEventModel event) {
//        Log.d(TAG, "onTaskAddedEventModel: ");
//        if (ACTIVE_LOADER == INQU_LOADER_ID) {
//            getSupportLoaderManager().restartLoader(INQU_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//            navigation.setSelectedItemId(R.id.navigation_tasks);
//        }
//    }

    @Subscribe
    public void onInquiryDeletedEventModel(InquiryDeletedEventModel event) {
        Log.d(TAG, "onInquiryDeletedEventModel: ");
        if (ACTIVE_LOADER == INQU_LOADER_ID) {
            getSupportLoaderManager().restartLoader(INQU_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_inquiries);
        }
    }

    @Subscribe
    public void onMissedCallEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEventModel: ");
        if (ACTIVE_LOADER == INQU_LOADER_ID) {
            getSupportLoaderManager().restartLoader(INQU_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_inquiries);
        }
    }

    @Subscribe
    public void onSaleContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onSaleContactAddedEventModel: ");
        if (ACTIVE_LOADER == HOME_LOADER_ID) {
            Log.d(TAG, "onSaleContactAddedEventModel: HOME_LOADER_ID");
            getSupportLoaderManager().restartLoader(HOME_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_home);
        }
        if (ACTIVE_LOADER == LEAD_LOADER_ID) {
            Log.d(TAG, "onSaleContactAddedEventModel: LEAD_LOADER_ID");
            getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_leads);
        }
//        Toast.makeText(NavigationBottomMainActivity.this, "LeadContactAddedEventModel", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(ContactDeletedEventModel event) {
        Log.d(TAG, "onLeadContactDeletedEventModel: ");
        if (ACTIVE_LOADER == HOME_LOADER_ID) {
            Log.d(TAG, "onLeadContactDeletedEventModel: HOME_LOADER_ID");
            getSupportLoaderManager().restartLoader(HOME_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_home);
        }
        if (ACTIVE_LOADER == LEAD_LOADER_ID) {
            Log.d(TAG, "onLeadContactDeletedEventModel: LEAD_LOADER_ID");
            getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
            navigation.setSelectedItemId(R.id.navigation_leads);
        }
//        Toast.makeText(NavigationBottomMainActivity.this, "ContactDeletedEventModel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        list.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        list.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
//            case 1:
//                return new TasksListLoader(NavigationBottomMainActivity.this);
            case INQU_LOADER_ID:
                return new InquiryLoader(NavigationBottomMainActivity.this);
            case HOME_LOADER_ID:
                return new HomeLoader(NavigationBottomMainActivity.this);
            case LEAD_LOADER_ID:
                return new LeadsLoader(NavigationBottomMainActivity.this, args);
            case MORE_LOADER_ID:
                return new MoreLoader(NavigationBottomMainActivity.this);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        if (data != null) {
            if (!data.isEmpty()) {
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        list.clear();
        list.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                startActivity(new Intent(NavigationBottomMainActivity.this, AccountActivity.class));
                return true;
            case R.id.action_refresh:
                sessionManager.fetchData();
                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                theCallLogEngine.execute();
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                dataSenderAsync.run();
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
                mixpanel.track("Refreshed");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query != null) {
                    loadHistory(query);
                }
                return false;
            }
        });
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    private void loadHistory(String query) {

        // Cursor
        String[] columns = new String[]{"_id", "text", "drawable", "class", "intentPutId", "intentPutNumber", "type"};
        Object[] temp = new Object[]{0, "default", R.drawable.inquiry_count_round, "", "", "", ""};

        MatrixCursor cursor = new MatrixCursor(columns);

        int count = 0;

        // From LSContacts where number = query
        String myQuery = "SELECT * FROM LS_CONTACT where phone_one like '%" + query + "%' limit 5";
        Collection<LSContact> contactsByNumber = LSContact.findWithQuery(LSContact.class, myQuery);

        for (LSContact contact : contactsByNumber) {
            temp[0] = count;
            temp[1] = contact.getPhoneOne();
            temp[2] = R.drawable.ic_account_circle;
            if (contact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
                temp[4] = contact.getId();
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
            }
            temp[6] = "type_contact";
            cursor.addRow(temp);
            count++;
        }

        // From LSContacts where name = query
        myQuery = "SELECT * FROM LS_CONTACT where contact_name like '%" + query + "%' limit 5";
        Collection<LSContact> contactsByName = LSContact.findWithQuery(LSContact.class, myQuery);

        for (LSContact contact : contactsByName) {
            temp[0] = count;
            temp[1] = contact.getContactName();
            temp[2] = R.drawable.ic_account_circle;
            if (contact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
                temp[4] = contact.getId();
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
            }
            temp[6] = "type_contact";
            cursor.addRow(temp);
            count++;
        }
        // From LSNotes where description = query
        myQuery = "SELECT * FROM LS_NOTE where note_text like '%" + query + "%' limit 5";
        Collection<LSNote> notesByDescription = LSNote.findWithQuery(LSNote.class, myQuery);

        for (LSNote note : notesByDescription) {
            temp[0] = count;
            temp[1] = note.getNoteText();
            temp[2] = R.drawable.ic_notes_blue_48dp;
            temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
            temp[4] = note.getContactOfNote().getId();
            temp[6] = "type_note";
            cursor.addRow(temp);
            count++;
        }
        // From LSInquiry where number = query
        myQuery = "SELECT * FROM LS_INQUIRY where contact_number like '%" + query + "%' limit 5";
        Collection<LSInquiry> inquiriesByNumber = LSInquiry.findWithQuery(LSInquiry.class, myQuery);

        for (LSInquiry inquiry : inquiriesByNumber) {
            temp[0] = count;
            temp[1] = inquiry.getContactNumber();
            temp[2] = R.drawable.inquiry_count_round;
            temp[3] = ClassManager.INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
            temp[4] = null;
            temp[5] = inquiry.getContactNumber();
            temp[6] = "type_inquiry";
            cursor.addRow(temp);
            count++;
        }

        searchView.setSuggestionsAdapter(new SearchSuggestionAdapter(this, cursor, list));
    }

    @Override
    public void onChipClick(String chip) {
        switch (chip) {
            case "All":
                bundle.putString("whichLeads", "All");
                getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InProgress":
                bundle.putString("whichLeads", "InProgress");
                getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "Won":
                bundle.putString("whichLeads", "Won");
                getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                Toast.makeText(this, "WonListened", Toast.LENGTH_SHORT).show();
                break;

            case "Lost":
                bundle.putString("whichLeads", "Lost");
                getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InActive":
                bundle.putString("whichLeads", "InActive");
                getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                Toast.makeText(this, "InActiveListened", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    public void openContactBottomSheetCallback(Long contact_id) {
        contactCallDetailsBottomSheetFragment = ContactCallDetailsBottomSheetFragmentNew.newInstance(contact_id, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        contactCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeContactBottomSheetCallback() {
        if (sheetShowing) {
            if (contactCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "closeContactBottomSheetCallback: is NOT NULL");
                contactCallDetailsBottomSheetFragment.dismiss(); //UncaughtException: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            } else {
                Log.d(TAG, "closeContactBottomSheetCallback: is NULL");
            }
        }
    }

    public void openInquiryBottomSheetCallback(String number) {
        inquiryCallDetailsBottomSheetFragment = InquiryCallDetailsBottomSheetFragmentNew.newInstance(number, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        inquiryCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeInquiryBottomSheetCallback() {
        if (sheetShowing) {
            if (inquiryCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NOT NULL");
                inquiryCallDetailsBottomSheetFragment.dismiss();
            } else {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NULL");
            }
        }
    }
}
