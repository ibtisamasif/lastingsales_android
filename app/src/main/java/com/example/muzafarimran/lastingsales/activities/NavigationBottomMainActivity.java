package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment1;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment2;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment4;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment5;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.app.ClassManager;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.customview.BottomNavigationViewHelper;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.fragments.InquiryCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.listeners.CloseContactBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listeners.CloseInquiryBottomSheetEvent;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.receivers.HourlyAlarmReceiver;
import com.example.muzafarimran.lastingsales.recycleradapter.SearchSuggestionAdapter;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.service.DemoSyncJob;
import com.example.muzafarimran.lastingsales.service.InitService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncUser;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;
import java.util.Collection;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.wires.ShakeEventWire;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ibtisam on 11/6/2017.
 */

public class NavigationBottomMainActivity extends AppCompatActivity implements CloseContactBottomSheetEvent, CloseInquiryBottomSheetEvent {
    public static final String TAG = "NavigationBottomMain";
    private static final String FRAGMENT_TAG_INQUIRIES = "fragment_tag_inquiries";
    private static final String FRAGMENT_TAG_UNLABELED = "fragment_tag_unlabeled";
    private static final String FRAGMENT_TAG_LEADS = "fragment_tag_leads";
    private static final String FRAGMENT_TAG_DEALS = "fragment_tag_deals";
    private static final String FRAGMENT_TAG_MORE = "fragment_tag_more";

    //    public static final String KEY_ACTIVE_LOADER = "active_loader";
//    public static int ACTIVE_LOADER = -1;
//    public static final int INQU_LOADER_ID = 1;
//    public static final int HOME_LOADER_ID = 2;
//    public static final int LEAD_LOADER_ID = 3;
//    public static final int MORE_LOADER_ID = 4;
    public static String KEY_SELECTED_TAB = "key_selected_tab";
    public static String INQUIRIES_TAB = "inquiries_tab";
    public static String BOTTOMSHEET_TAB = "bottomsheet_tab";
    public static String KEY_SELECTED_TAB_BOTTOMSHEET_CONTACT_ID = "bottomsheet_contact_id";
    public static String KEY_SELECTED_TAB_NO_TAB = "no_tab";

    //    private TinyBus bus;
    //    private List<Object> list = new ArrayList<Object>();
//    private MyRecyclerViewAdapter adapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SessionManager sessionManager;
    private SettingsManager settingsManager;
    Bundle bundle = new Bundle();
    private SearchView searchView;
    private android.support.design.widget.FloatingActionButton floatingActionButtonDeal;
    private FloatingActionMenu floatingActionMenuLead;
    private BottomNavigationView navigation;
    private static InquiryCallDetailsBottomSheetFragment inquiryCallDetailsBottomSheetFragment;
    private static ContactCallDetailsBottomSheetFragment contactCallDetailsBottomSheetFragment;
    public static Activity activity;
    private static boolean sheetShowing = false;
    //    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            handleResult(bundle);
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.navigation_tasks:
//                    ACTIVE_LOADER = 1;
//                    floatingActionMenuLead.hideMenu(true);
//                    getSupportLoaderManager().restartLoader(1, bundle, NavigationBottomMainActivity.this).forceLoad();
////                    getSupportLoaderManager().initLoader(1, null, NavigationBottomMainActivity.this);
//                    return true;
                case R.id.navigation_inquiries:
                    switchToFragment1();
//                    ACTIVE_LOADER = INQU_LOADER_ID;
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.hideMenu(true);
//                    getSupportLoaderManager().restartLoader(INQU_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_home:
                    switchToFragment2();
//                    ACTIVE_LOADER = HOME_LOADER_ID;
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.hideMenu(true);
//                    getSupportLoaderManager().restartLoader(HOME_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
//                case R.id.navigation_leads:
//                    switchToFragment3();
////                    ACTIVE_LOADER = LEAD_LOADER_ID;
//                    floatingActionButtonDeal.hide();
//                    floatingActionMenuLead.showMenu(true);
////                    getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
//                    return true;
                case R.id.navigation_deals:
                    switchToFragment4();
//                    ACTIVE_LOADER = LEAD_LOADER_ID;
                    floatingActionMenuLead.hideMenu(true);
                    floatingActionButtonDeal.show();
//                    getSupportLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_more:
                    switchToFragment5();
//                    ACTIVE_LOADER = MORE_LOADER_ID;
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.hideMenu(true);
//                    getSupportLoaderManager().restartLoader(MORE_LOADER_ID, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;

            }
            return false;
        }
    };

    public void switchToFragment1() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace
                (R.id.llFragmentContainer, new BlankFragment1(), FRAGMENT_TAG_INQUIRIES).commitAllowingStateLoss();
//        navigation.setSelectedItemId(R.id.navigation_inquiries);
    }

    public void switchToFragment2() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new BlankFragment2(), FRAGMENT_TAG_UNLABELED).commitAllowingStateLoss();
//        navigation.setSelectedItemId(R.id.navigation_home);
    }

//    public void switchToFragment3() {
//        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.llFragmentContainer, new BlankFragment3(), FRAGMENT_TAG_LEADS).commitAllowingStateLoss();
////        navigation.setSelectedItemId(R.id.navigation_leads);
//    }

    public void switchToFragment4() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new BlankFragment4(), FRAGMENT_TAG_DEALS).commitAllowingStateLoss();
//        navigation.setSelectedItemId(R.id.navigation_deals);
    }

    public void switchToFragment5() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new BlankFragment5(), FRAGMENT_TAG_MORE).commitAllowingStateLoss();
//        navigation.setSelectedItemId(R.id.navigation_more);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        initFirst(savedInstanceState);

        setContentView(R.layout.activity_bottom_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lasting Sales");
        ActionBar actionBar = getSupportActionBar();

//        adapter = new MyRecyclerViewAdapter(this, list);
//        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        mRecyclerView.setAdapter(adapter);

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

        floatingActionMenuLead = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        FloatingActionButton floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_add);
        FloatingActionButton floatingActionButtonImport = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenuLead.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenuLead.close(true);

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
                floatingActionMenuLead.close(true);

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

        floatingActionButtonDeal = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab_add_deal);
        floatingActionButtonDeal.hide();
        floatingActionButtonDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddDealActivity.class);
                startActivity(intent);
            }
        });

        onBackToActivity();

    }

    private void onBackToActivity() {
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            String tab = bundle1.getString(KEY_SELECTED_TAB);
            if (tab != null) {
                if (tab.equals(INQUIRIES_TAB)) {
                    Log.d(TAG, "onCreate: Loading Inquiries TAB");
                    navigation.setSelectedItemId(R.id.navigation_inquiries);
                } else if (tab.equals(BOTTOMSHEET_TAB)) {
                    Log.d(TAG, "onCreate: Loading Leads TAB with bottomsheet open");
                    String contactId = bundle1.getString(KEY_SELECTED_TAB_BOTTOMSHEET_CONTACT_ID);
                    navigation.setSelectedItemId(R.id.navigation_home);
                    openContactBottomSheetCallback(Long.parseLong(contactId));
                }
            }
            else {
                Log.d(TAG, "onCreate: Bundle Not Null TAB unknown Loading Leads TAB");
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }
        else {
            Log.d(TAG, "onCreate: Bundle is Null Loading Leads TAB");
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }


    private void initFirst(Bundle savedInstanceState) {

        Fabric.with(this, new Crashlytics());

//        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        final Bundle bundle = new Bundle();
//        //The following code logs a SELECT_CONTENT Event when a user clicks on a specific element in your app.
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
////        FirebaseCrash.report(new Exception("My first Android non-fatal error"));

//        progressDialog = new ProgressDialog(NavigationBottomMainActivity.this);
//        progressDialog.setTitle("Fetching data");
//        //this method will be running on UI thread
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.setCancelable(false);
        Log.d(TAG, "initFirst: density: " + getResources().getDisplayMetrics().density);
        activity = this;
        sessionManager = new SessionManager(getApplicationContext());
//        bus = TinyBus.from(this.getApplicationContext());
//        if (savedInstanceState == null) {
//            // Note: ShakeEventWire stays wired when activity is re-created
//            //       on configuration change. That's why we register is
//            //       only once inside if-statement.
//
//            // wire device shake event provider
//            bus.wire(new ShakeEventWire());
//        }

        checkForInvalidTime();
        if (!sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
            return;
        } else {
            Intent intent = new Intent(NavigationBottomMainActivity.this, CallDetectionService.class);
            startService(intent);
            if (NetworkAccess.isNetworkAvailable(this)) {
                long contactCount = LSContact.count(LSContact.class); // If app is crashed here make sure instant run is off. // TODO instead of checking for zero contacts check app init.
                if (contactCount < 1) {
                    Log.d(TAG, "onCreate: LSContact.count " + contactCount);
                    Intent intentInitService = new Intent(this, InitService.class);
                    startService(intentInitService);
                }
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

//        DemoSyncJob.schedulePeriodic();
        DemoSyncJob.cancelThisJob();

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

//        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            public void uncaughtException(Thread thread, Throwable ex) {
//                Log.d(TAG, "uncaughtException: ");
//                Intent launchIntent = new Intent(getIntent());
//                PendingIntent pending = PendingIntent.getActivity(NavigationBottomMainActivity.this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                manager.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, pending);
//                defaultHandler.uncaughtException(thread, ex);
//                System.exit(2);
//            }
//        });

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
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        ACTIVE_LOADER = savedInstanceState.getInt(KEY_ACTIVE_LOADER);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
//        bus.register(this);
        // DO not move lastSync to anywhere else.
        if (sessionManager.isUserSignedIn()) {
            sessionManager.setLastAppVisit("" + Calendar.getInstance().getTimeInMillis());
            if (NetworkAccess.isNetworkAvailable(this)) {
                SyncUser.updateUserLastSeenToServer(this);
                SyncUser.getUserDataFromServer(this);
                SyncUser.getLastestAppVersionCodeFromServer(this);

                if (!sessionManager.getKeyIsUserActive()) {
                    Intent i = new Intent(NavigationBottomMainActivity.this, UserInActiveActivity.class);
                    i.putExtra(TrialExpiryActivity.KEY_MESSAGE, "User is deactivated");
                    startActivity(i);
                    finish();
                }

                if (!sessionManager.getKeyIsCompanyActive()) {
                    sessionManager.logoutUser();
                    Intent i = new Intent(NavigationBottomMainActivity.this, CompanyInActiveActivity.class);
                    i.putExtra(TrialExpiryActivity.KEY_MESSAGE, "Company is deactivated");
                    startActivity(i);
                    finish();
                }
                if (!sessionManager.getKeyIsCompanyPaying()) {
                    if (!sessionManager.getKeyIsTrialValid()) {

                        Intent i = new Intent(NavigationBottomMainActivity.this, TrialExpiryActivity.class);
                        i.putExtra("message", "During your free trial period LastingSales created 500 contacts for you, processed 5000 calls");
                        startActivity(i);


                    }
                }

            }

            if (!sessionManager.getCanSync()) {
                Toast.makeText(activity, "Syncing is Paused", Toast.LENGTH_SHORT).show();
            }

            try {
                int versionAvailableOnline = sessionManager.getUpdateAvailableVersion();
                int currentVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
                if (versionAvailableOnline != currentVersionCode) {
                    if (versionAvailableOnline > currentVersionCode) {
                        TextView tvAppUpdate = findViewById(R.id.tvAppUpdate);
                        tvAppUpdate.setVisibility(View.VISIBLE);
                        tvAppUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.lastingsales.agent"));
                                startActivity(intent);
                            }
                        });
//                        Toast.makeText(activity, "update available", Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(NavigationBottomMainActivity.this, UpgradeActivity.class);
//                        i.putExtra("message", "New version of LastingSales is available in PlayStore. Please Update!");
//                        startActivity(i);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        registerReceiver(receiver, new IntentFilter(InitService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() called");
//        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.llFragmentContainer); //TODO NPE
            if (f instanceof BlankFragment2) {
                super.onBackPressed();
            } else {
                navigation.setSelectedItemId(R.id.navigation_home);
                floatingActionMenuLead.showMenu(false);
            }
        }
    }

    @Subscribe
    public void onShakeEvent(ShakeEventWire.ShakeEvent event) {
        // device has been shaken
        Log.d(TAG, "onShakeEvent: Shake Event: " + event);
//        sessionManager.fetchData();
        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
        theCallLogEngine.execute();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
        dataSenderAsync.run();
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        mixpanel.track("Shaked");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                startActivity(new Intent(NavigationBottomMainActivity.this, AccountActivity.class));
                return true;
            case R.id.action_refresh:
                Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
                if (NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Intent intentInitService = new Intent(this, InitService.class);
                    startService(intentInitService);
//                    progressDialog.show();
//                    sessionManager.fetchData();
                } else {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                theCallLogEngine.execute();
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                dataSenderAsync.run();
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
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
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
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
            } else if (contact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                // Dont navigate anywhere on clicking colleagues as discussed
                temp[3] = ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
                temp[4] = contact.getId();
                temp[5] = contact.getPhoneOne();
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
            if (note.getContactOfNote() != null) {
                temp[0] = count;
                temp[1] = note.getNoteText();
                temp[2] = R.drawable.ic_notes_blue_48dp;
                temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
                temp[4] = note.getContactOfNote().getId();
                temp[6] = "type_note";
                cursor.addRow(temp);
                count++;
            } else {
                note.delete();
            }
        }
        // From LSInquiry where number = query
        myQuery = "SELECT * FROM LS_INQUIRY where contact_number like '%" + query + "%' limit 5";
        Collection<LSInquiry> inquiriesByNumber = LSInquiry.findWithQuery(LSInquiry.class, myQuery);

        for (LSInquiry inquiry : inquiriesByNumber) {
            temp[0] = count;
            temp[1] = inquiry.getContactNumber();
            temp[2] = R.drawable.ic_phone_missed_red_24dp;
            temp[3] = ClassManager.INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT;
            temp[4] = null;
            temp[5] = inquiry.getContactNumber();
            temp[6] = "type_inquiry";
            cursor.addRow(temp);
            count++;
        }

        myQuery = "SELECT * FROM LS_DEAL where name like '%" + query + "%' limit 5";
        Collection<LSDeal> dealsByName = LSDeal.findWithQuery(LSDeal.class, myQuery);
        for (LSDeal oneDeal : dealsByName) {
            temp[0] = count;
            temp[1] = oneDeal.getName();
            temp[2] = R.drawable.ic_monetization_on_48dp;
            temp[3] = ClassManager.DEAL_DETAILS_TAB_ACTIVITY;
            temp[4] = oneDeal.getId();
            temp[6] = "type_deal";
            cursor.addRow(temp);
            count++;
        }
        searchView.setSuggestionsAdapter(new SearchSuggestionAdapter(this, cursor));
    }

    private void handleResult(Bundle bundle) {
        if (bundle != null) {
            int resultCode = bundle.getInt(InitService.RESULT);
            if (resultCode == RESULT_OK) {

//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }

                if (sessionManager.isFirstRunAfterLogin()) {
                    Log.d(TAG, "initFirst: isFirstRun TRUE");
                    TheCallLogEngine theCallLogEngine = new TheCallLogEngine(NavigationBottomMainActivity.this);
                    theCallLogEngine.execute();
                }

//                Toast.makeText(NavigationBottomMainActivity.this, "Init complete", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                sessionManager.deleteAllUserData();
//                AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                alert.setTitle("Backup");
//                alert.setMessage("Could not fetch data please try again");
//                alert.setPositiveButton("Try Again", (dialog, which) -> {
//                    if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
//                        Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
//                        alert.show();
//                    } else {
////                        progressDialog.show();
//                        // try fetching again.
//                        Intent intentInitService = new Intent(NavigationBottomMainActivity.this, InitService.class);
//                        startService(intentInitService);
//                        dialog.dismiss();
//                    }
//                });
//                alert.setNegativeButton("Cancel", (dialog, which) -> {
//                    sessionManager.logoutUser();
//                    startActivity(new Intent(NavigationBottomMainActivity.this, LogInActivity.class));
//                    finish();
//                    dialog.dismiss();
//                }).setCancelable(false);
//                alert.show();

//                sessionManager.logoutUser();
//                startActivity(new Intent(NavigationBottomMainActivity.this, LogInActivity.class));
//                finish();

//                Toast.makeText(NavigationBottomMainActivity.this, "Init failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openContactBottomSheetCallback(Long contact_id) {
        contactCallDetailsBottomSheetFragment = ContactCallDetailsBottomSheetFragment.newInstance(contact_id, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        contactCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeContactBottomSheetCallback() {
        if (sheetShowing) {
            if (contactCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "closeContactBottomSheetCallback: is NOT NULL");
                try {
//                    if (Build.VERSION.SDK_INT > 21) {
//                        contactCallDetailsBottomSheetFragment.dismiss(); //UncaughtException: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
//                    } else {
                    contactCallDetailsBottomSheetFragment.dismissAllowingStateLoss();
//                    }
                } catch (IllegalStateException ignored) {
//                    Crashlytics.setUserIdentifier(sessionManager.getLoginNumber());
                    Crashlytics.log(Log.ERROR, TAG, "IllegalStateException caught");
                    Crashlytics.logException(new Exception("closeContactBottomSheetCallback dismiss() called after onSaveInstanceState"));
                }
            } else {
                Log.d(TAG, "closeContactBottomSheetCallback: is NULL");
            }
        }
    }

    public void openInquiryBottomSheetCallback(String number) {
        inquiryCallDetailsBottomSheetFragment = InquiryCallDetailsBottomSheetFragment.newInstance(number, 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        inquiryCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeInquiryBottomSheetCallback() {
        if (sheetShowing) {
            if (inquiryCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NOT NULL");
                try {
//                    if (Build.VERSION.SDK_INT > 21) {
//                        inquiryCallDetailsBottomSheetFragment.dismiss(); //UncaughtException: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
//                    } else {
                    inquiryCallDetailsBottomSheetFragment.dismissAllowingStateLoss();
//                    }
                } catch (IllegalStateException ignored) {
//                    Crashlytics.setUserIdentifier(sessionManager.getLoginNumber());
                    Crashlytics.log(Log.ERROR, TAG, "IllegalStateException caught");
                    Crashlytics.logException(new Exception("closeInquiryBottomSheetCallback dismiss() called after onSaveInstanceState"));
                }
            } else {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NULL");
            }
        }
    }
}
