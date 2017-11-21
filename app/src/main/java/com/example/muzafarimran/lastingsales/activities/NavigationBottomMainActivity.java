package com.example.muzafarimran.lastingsales.activities;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.app.ClassManager;
import com.example.muzafarimran.lastingsales.app.ClassNames;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.customview.BadgeView;
import com.example.muzafarimran.lastingsales.customview.BottomNavigationViewHelper;
import com.example.muzafarimran.lastingsales.listeners.ChipClickListener;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.receivers.HourlyAlarmReceiver;
import com.example.muzafarimran.lastingsales.recycleradapter.ExampleAdapter;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.listloaders.HomeLoader;
import com.example.muzafarimran.lastingsales.listloaders.InquiryLoader;
import com.example.muzafarimran.lastingsales.listloaders.LeadsLoader;
import com.example.muzafarimran.lastingsales.listloaders.MoreLoader;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.service.DemoSyncJob;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncLastSeen;
import com.example.muzafarimran.lastingsales.sync.AgentDataFetchAsync;
import com.example.muzafarimran.lastingsales.utilscallprocessing.ShortcutBadgeUpdateAsync;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/6/2017.
 */

public class NavigationBottomMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Object>>, ChipClickListener {
    public static final String TAG = "NavigationBottomMain";

    public static final String KEY_ACTIVE_LOADER = "active_loader";

    public static int ACTIVE_LOADER = -1;

    private TinyBus bus;

    private List<Object> list = new ArrayList<Object>();

    private MyRecyclerViewAdapter adapter;

    private RecyclerView mRecyclerView;

    private Toolbar toolbar;

    private FirebaseAnalytics mFirebaseAnalytics;

    private SessionManager sessionManager;

    private SettingsManager settingsManager;

    Bundle bundle = new Bundle();

    private Menu menu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (ACTIVE_LOADER != -1) {
                if (getSupportLoaderManager().getLoader(ACTIVE_LOADER).isStarted()) { //still crashing here
                    getSupportLoaderManager().getLoader(ACTIVE_LOADER).cancelLoad();
                }
            }
            switch (item.getItemId()) {
                case R.id.navigation_inquiries:
                    ACTIVE_LOADER = 1;
                    getSupportLoaderManager().restartLoader(1, bundle, NavigationBottomMainActivity.this).forceLoad();
//                    getSupportLoaderManager().initLoader(1, null, NavigationBottomMainActivity.this);
                    return true;
                case R.id.navigation_home:
                    ACTIVE_LOADER = 2;
                    getSupportLoaderManager().restartLoader(2, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_leads:
                    ACTIVE_LOADER = 3;
                    getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
                case R.id.navigation_more:
                    ACTIVE_LOADER = 4;
                    getSupportLoaderManager().restartLoader(4, bundle, NavigationBottomMainActivity.this).forceLoad();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);


        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_sample, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(modalbottomsheet);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        adapter = new MyRecyclerViewAdapter(this, list);

        getSupportLoaderManager().initLoader(2, null, NavigationBottomMainActivity.this).forceLoad();

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        sessionManager = new SessionManager(this);

        mRecyclerView.setAdapter(adapter);

        init(this);
    }

    private void init(NavigationBottomMainActivity navigationBottomMainActivity) {

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle bundle = new Bundle();
        //The following code logs a SELECT_CONTENT Event when a user clicks on a specific element in your app.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        } else {
            Intent intent = new Intent(NavigationBottomMainActivity.this, CallDetectionService.class);
            startService(intent);
        }

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
//                PendingIntent pending = PendingIntent.getActivity(NavigationBottomMainActivity.this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                manager.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, pending);
//                defaultHandler.uncaughtException(thread, ex);
//                System.exit(2);
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


        //        Log.d("rating", "onCreate: setLastAppVisit");
//        long milliSecondsIn30Second = 60000; // 30 seconds for now
//        long now = Calendar.getInstance().getTimeInMillis();
//        long thirtySecondsAgoTimestamp = now - milliSecondsIn30Second;
//        sessionManager.setLastAppVisit("" + thirtySecondsAgoTimestamp);
        sessionManager.setLastAppVisit("" + Calendar.getInstance().getTimeInMillis());
        SyncLastSeen.updateLastSeenToServer(NavigationBottomMainActivity.this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVE_LOADER, ACTIVE_LOADER);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ACTIVE_LOADER = savedInstanceState.getInt(KEY_ACTIVE_LOADER);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        list.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        list.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
            case 1:
                return new InquiryLoader(NavigationBottomMainActivity.this);
            case 2:
                return new HomeLoader(NavigationBottomMainActivity.this);
            case 3:
                return new LeadsLoader(NavigationBottomMainActivity.this, args);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);

        this.menu = menu;

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                loadHistory(query);
                return false;
            }
        });

        return true;

//        return super.onCreateOptionsMenu(menu);
    }

    // History
    private void loadHistory(String query) {


        // Cursor
        String[] columns = new String[]{"_id", "text", "drawable", "class"};
        Object[] temp = new Object[]{0, "default", R.drawable.inquiry_count_round, ""};

        MatrixCursor cursor = new MatrixCursor(columns);

        int count = 0;


        // From LSContacts where number = query
        String myQuery = "SELECT * FROM LS_CONTACT where phone_one like '%" + query + "%' limit 5";
        Collection<LSContact> contactsByNumber = LSContact.findWithQuery(LSContact.class, myQuery);

        for (LSContact one : contactsByNumber) {
            temp[0] = count;
            temp[1] = one.getPhoneOne();
            temp[2] = R.drawable.ic_account_circle;
            if (one.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {

                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            }

            cursor.addRow(temp);
            count++;

        }


        // From LSContacts where name = query
        myQuery = "SELECT * FROM LS_CONTACT where contact_name like '%" + query + "%' limit 5";
        Collection<LSContact> contactsByName = LSContact.findWithQuery(LSContact.class, myQuery);

        for (LSContact one : contactsByName) {
            temp[0] = count;
            temp[1] = one.getContactName();
            temp[2] = R.drawable.ic_account_circle;
            if (one.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            } else if (one.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                temp[3] = ClassManager.ABOUT_ACTIVITY;
            }

            cursor.addRow(temp);
            count++;

        }


        // From LSNotes where description = query
        myQuery = "SELECT * FROM LS_NOTE where note_text like '%" + query + "%' limit 5";
        Collection<LSNote> notesByDescription = LSNote.findWithQuery(LSNote.class, myQuery);

        for (LSNote one : notesByDescription) {
            temp[0] = count;
            temp[1] = one.getNoteText();
            temp[2] = R.drawable.ic_notes_black;
            temp[3] = ClassManager.ABOUT_ACTIVITY;
            cursor.addRow(temp);
            count++;

        }


        // From LSInquiry where number = query
        myQuery = "SELECT * FROM LS_INQUIRY where contact_number like '%" + query + "%' limit 5";
        Collection<LSInquiry> inquiriesByNumber = LSInquiry.findWithQuery(LSInquiry.class, myQuery);

        for (LSInquiry one : inquiriesByNumber) {
            temp[0] = count;
            temp[1] = one.getContactNumber();
            temp[2] = R.drawable.inquiry_count_round;
            temp[3] = ClassManager.ABOUT_ACTIVITY;
            cursor.addRow(temp);
            count++;
        }


        // Pass all in one Collection


        // SearchView
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSuggestionsAdapter(new

                ExampleAdapter(this, cursor, list));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, "Searching", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_account:
                startActivity(new Intent(NavigationBottomMainActivity.this, AccountActivity.class));
                return true;
            case R.id.action_refresh:
                AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(getApplicationContext());
                agentDataFetchAsync.execute();
                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                theCallLogEngine.execute();
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                dataSenderAsync.run();
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChipClick(String chip) {
        switch (chip) {
            case "All":
                bundle.putString("whichLeads", "All");
                getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InProgress":
                bundle.putString("whichLeads", "InProgress");
                getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "Won":
                bundle.putString("whichLeads", "Won");
                getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                Toast.makeText(this, "WonListened", Toast.LENGTH_SHORT).show();
                break;

            case "Lost":
                bundle.putString("whichLeads", "Lost");
                getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InActive":
                bundle.putString("whichLeads", "InActive");
                getSupportLoaderManager().restartLoader(3, bundle, NavigationBottomMainActivity.this).forceLoad();
                Toast.makeText(this, "WonListened", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }
}
