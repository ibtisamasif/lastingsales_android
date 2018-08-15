package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.muzafarimran.lastingsales.Manifest;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.adapters.SearchSuggestionAdapter;
import com.example.muzafarimran.lastingsales.app.ClassManager;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.customview.BottomNavigationViewHelper;
import com.example.muzafarimran.lastingsales.events.DealEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.OrganizationEventModel;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.fragments.ContactsAndOrganizationViewPagerFragment;
import com.example.muzafarimran.lastingsales.fragments.DealsFragment;
import com.example.muzafarimran.lastingsales.fragments.InquiriesFragment;
import com.example.muzafarimran.lastingsales.fragments.InquiryCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.fragments.MoreFragment;
import com.example.muzafarimran.lastingsales.listeners.CloseContactBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listeners.CloseInquiryBottomSheetEvent;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.service.CallLogEngineIntentService;
import com.example.muzafarimran.lastingsales.service.InitService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncUser;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;
import java.util.Collection;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import de.halfbit.tinybus.wires.ShakeEventWire;
import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by ibtisam on 11/6/2017.
 */

public class NavigationBottomMainActivity extends AppCompatActivity implements CloseContactBottomSheetEvent, CloseInquiryBottomSheetEvent {
    public static final String TAG = "NavigationBottomMain";
    private static final String FRAGMENT_TAG_INQUIRIES = "fragment_tag_inquiries";
    private static final String FRAGMENT_TAG_CONTACTS = "fragment_tag_unlabeled";
    private static final String FRAGMENT_TAG_DEALS = "fragment_tag_deals";
    private static final String FRAGMENT_TAG_MORE = "fragment_tag_more";

    public static String KEY_SELECTED_TAB = "key_selected_tab";
    public static String INQUIRIES_TAB = "inquiries_tab";
    public static String BOTTOMSHEET_TAB = "bottomsheet_tab";
    public static String KEY_SELECTED_TAB_BOTTOMSHEET_CONTACT_ID = "bottomsheet_contact_id";
    public static String KEY_SELECTED_TAB_NO_TAB = "no_tab";
    public static Activity activity;
    private static InquiryCallDetailsBottomSheetFragment inquiryCallDetailsBottomSheetFragment;
    private static ContactCallDetailsBottomSheetFragment contactCallDetailsBottomSheetFragment;
    private static boolean sheetShowing = false;
    Bundle bundle = new Bundle();
    private FirebaseAnalytics mFirebaseAnalytics;
    private SessionManager sessionManager;
    private SettingsManager settingsManager;
    private SearchView searchView;
    private android.support.design.widget.FloatingActionButton floatingActionButtonDeal;
    private FloatingActionMenu floatingActionMenuLead;
    private BottomNavigationView navigation;
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
                case R.id.navigation_inquiries:
                    switchToFragmentInquiries();
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.hideMenu(true);
                    changeColorOfStatusAndActionBar(FRAGMENT_TAG_INQUIRIES);
                    return true;
                case R.id.navigation_home:
                    switchToFragmentContacts();
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.showMenu(true);
                    changeColorOfStatusAndActionBar(FRAGMENT_TAG_CONTACTS);
                    return true;
                case R.id.navigation_deals:
                    switchToFragmentDeals();
                    floatingActionButtonDeal.show();
                    floatingActionMenuLead.hideMenu(true);
                    changeColorOfStatusAndActionBar(FRAGMENT_TAG_DEALS);
                    return true;
                case R.id.navigation_more:
                    switchToFragmentMore();
                    floatingActionButtonDeal.hide();
                    floatingActionMenuLead.hideMenu(true);
                    changeColorOfStatusAndActionBar(FRAGMENT_TAG_MORE);
                    return true;
            }
            return false;
        }
    };
    private static final int RequestPermissionCode=22647;

    private void changeColorOfStatusAndActionBar(String selectedFragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View myLayout = findViewById(R.id.include);
            Toolbar toolbar = myLayout.findViewById(R.id.toolbar);
            switch (selectedFragment) {
                case FRAGMENT_TAG_INQUIRIES:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case FRAGMENT_TAG_CONTACTS:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.Ls_Color_Primary));
                    window.setStatusBarColor(getResources().getColor(R.color.Ls_Color_PrimaryDark));
                    break;
                case FRAGMENT_TAG_DEALS:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case FRAGMENT_TAG_MORE:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                default:
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
            }
        }
    }

    public void switchToFragmentInquiries() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new InquiriesFragment(), FRAGMENT_TAG_INQUIRIES).commitAllowingStateLoss();
    }

    public void switchToFragmentContacts() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new ContactsAndOrganizationViewPagerFragment(), FRAGMENT_TAG_CONTACTS).commitAllowingStateLoss();
    }

    public void switchToFragmentDeals() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new DealsFragment(), FRAGMENT_TAG_DEALS).commitAllowingStateLoss();
    }

    public void switchToFragmentMore() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.llFragmentContainer, new MoreFragment(), FRAGMENT_TAG_MORE).commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");





        initFirst(savedInstanceState);

        setContentView(R.layout.activity_bottom_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lasting Sales");
        ActionBar actionBar = getSupportActionBar();
        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkPermission()){
                //permission granted
              //  Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                requestPermission();
            }
        }



        initLast();

        floatingActionMenuLead = findViewById(R.id.material_design_android_floating_action_menu);
        FloatingActionButton floatingActionButtonAddOrg = findViewById(R.id.material_design_floating_action_menu_add_org);
        FloatingActionButton floatingActionButtonAdd = findViewById(R.id.material_design_floating_action_menu_add);
        FloatingActionButton floatingActionButtonImport = findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenuLead.setClosedOnTouchOutside(true);

        floatingActionButtonAddOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenuLead.close(true);
                Dialog addOrgDialog = new Dialog(NavigationBottomMainActivity.this);
                addOrgDialog.setContentView(R.layout.add_organization);
                addOrgDialog.setCancelable(true);
                addOrgDialog.show();

                Button insertOrg = addOrgDialog.findViewById(R.id.bSaveAddOrg);
                Button cancel = addOrgDialog.findViewById(R.id.bCancelAddOrg);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addOrgDialog.dismiss();
                    }
                });

                insertOrg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText nameAddOrg = addOrgDialog.findViewById(R.id.etNameAddOrg);
                        EditText emailAddOrg = addOrgDialog.findViewById(R.id.etEmailAddOrg);
                        EditText phoneAddOrg = addOrgDialog.findViewById(R.id.etPhoneAddOrg);

                        if (nameAddOrg.getText().toString().isEmpty()) {
                            nameAddOrg.setError("Please enter  Name!");
                        } else {
                            LSOrganization lsOrganization = new LSOrganization();
                            lsOrganization.setName(nameAddOrg.getText().toString());
                            if (emailAddOrg != null) {
                                lsOrganization.setEmail(emailAddOrg.getText().toString());
                            }
                            if (phoneAddOrg != null) {
                                lsOrganization.setPhone(phoneAddOrg.getText().toString());
                            }
                            lsOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_NOT_SYNCED);

                            if (lsOrganization.save() > 0) {
                                Toast.makeText(NavigationBottomMainActivity.this, "Organization saved", Toast.LENGTH_SHORT).show();
                                addOrgDialog.dismiss();
                                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(addOrgDialog.getContext());
                                dataSenderAsync.run();
                            } else {
                                Toast.makeText(NavigationBottomMainActivity.this, "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
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
            }
        });

        floatingActionButtonDeal = findViewById(R.id.fab_add);
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
            } else {
                Log.d(TAG, "onCreate: Bundle Not Null TAB unknown Loading Leads TAB");
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        } else {
            Log.d(TAG, "onCreate: Bundle is Null Loading Leads TAB");
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }


    private void initFirst(Bundle savedInstanceState) {

        Fabric.with(this, new Crashlytics());

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
                    }
                });
                alert.show();
            }
        }
    }

    private void initLast() {

//        DemoSyncJob.schedulePeriodic();
//        DemoSyncJob.cancelThisJob();

        settingsManager = new SettingsManager(this);


        //////////////////////////////////////////////////
        ////////////  This code is important for custom OEMs it will add app to protected apps list. DO NOT remove the code below infact work on its growth to cover more OEMs.
        //////////////////////////////////////////////////
        Log.d(TAG, "onCreate: Build.MANUFACTURER: " + Build.MANUFACTURER);
        Log.d(TAG, "onCreate: Build.BRAND: " + Build.BRAND);

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

        //////////////////////
        ///////  Optimize app for less killing by adding in ignoring optimize app list for this app (Pure Android)
        ///////////////////
//        Intent intentTest = new Intent();
//        String packageName = NavigationBottomMainActivity.this.getPackageName();
//        PowerManager pm = (PowerManager) NavigationBottomMainActivity.this.getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (pm.isIgnoringBatteryOptimizations(packageName))
//                intentTest.setAction(SettingsActivity.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//            else {
//                intentTest.setAction(SettingsActivity.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
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
                    Intent i = new Intent(NavigationBottomMainActivity.this, UserInActiveActivityJava.class);
                    startActivity(i);
                    finish();
                }
                if (!sessionManager.getKeyIsCompanyActive()) {
                    sessionManager.logoutUser();
                    Intent i = new Intent(NavigationBottomMainActivity.this, CompanyInActiveActivityJava.class);
                    startActivity(i);
                    finish();
                }
                if (!sessionManager.getKeyIsCompanyPaying()) {
                    if (!sessionManager.getKeyIsTrialValid()) {
                        Intent i = new Intent(NavigationBottomMainActivity.this, TrialExpiryActivityJava.class);
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
            if (f instanceof ContactsAndOrganizationViewPagerFragment) {
                super.onBackPressed();
            } else {
                navigation.setSelectedItemId(R.id.navigation_home);
                floatingActionMenuLead.showMenu(true);
            }
        }
    }

    @Subscribe
    public void onShakeEvent(ShakeEventWire.ShakeEvent event) {
        // device has been shaken
        Log.d(TAG, "onShakeEvent: Shake Event: " + event);
//        sessionManager.fetchData();

        //   startService(new Intent(getApplicationContext(), CallLogEngineIntentService.class));

        /* TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
        theCallLogEngine.execute();*/
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
//                    Intent intentInitService = new Intent(this, InitService.class);
//                    startService(intentInitService);
//                    progressDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
                startService(new Intent(getApplicationContext(), CallLogEngineIntentService.class));
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
            temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
            temp[4] = contact.getId();
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
            temp[3] = ClassManager.CONTACT_DETAILS_TAB_ACTIVITY;
            temp[4] = contact.getId();
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

        myQuery = "SELECT * FROM  LS_ORGANIZATION where name like '%" + query + "%' limit 5";
        Collection<LSOrganization> orgByName = LSOrganization.findWithQuery(LSOrganization.class, myQuery);
        for (LSOrganization oneDeal : orgByName) {
            temp[0] = count;
            temp[1] = oneDeal.getName();
            temp[2] = R.drawable.ic_building_48dp;
            temp[3] = ClassManager.ORG_DETAILS_BOTTOM_SHEET_FRAGMENT;
            temp[4] = oneDeal.getId();
            temp[6] = "type_org";
            cursor.addRow(temp);
            count++;
        }
        searchView.setSuggestionsAdapter(new SearchSuggestionAdapter(this, cursor));
    }

    private void handleResult(Bundle bundle) {
        Log.d(TAG, "handleResult: ");
        if (bundle != null) {
            int resultCode = bundle.getInt(InitService.RESULT);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "handleResult: OK");

//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }

                if (sessionManager.isFirstRunAfterLogin()) {
                    Log.d(TAG, "initFirst: isFirstRun TRUE");
                    startService(new Intent(getApplicationContext(), CallLogEngineIntentService.class));
                }
                Toast.makeText(NavigationBottomMainActivity.this, "Init complete", Toast.LENGTH_LONG).show();
                TinyBus.from(this.getApplicationContext()).post(new LeadContactAddedEventModel());
                TinyBus.from(this.getApplicationContext()).post(new OrganizationEventModel());
                TinyBus.from(this.getApplicationContext()).post(new DealEventModel());
//                if (!new SessionManager(getApplicationContext()).getIsFirstTimeLaunch()) {
//                    activity.startActivity(new Intent(activity, TutorialScreenActivity.class));
//                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "handleResult: CANCELED");
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
                Toast.makeText(NavigationBottomMainActivity.this, "Init failed", Toast.LENGTH_LONG).show();
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



    public  void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, WRITE_CALENDAR,READ_CONTACTS,READ_PHONE_STATE,READ_CALL_LOG}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    boolean ReadContact=grantResults[2]==PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhoneState=grantResults[3]==PackageManager.PERMISSION_GRANTED;
                    boolean ReadCallLogs=grantResults[4]==PackageManager.PERMISSION_GRANTED;

                    if (ReadCallLogs && StoragePermission && RecordPermission && ReadContact && ReadPhoneState) {
                        /*Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();*/


                    } else {
                        //  Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                        showPermissionDialog();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_CALENDAR);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_CONTACTS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_CALL_LOG);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED &&
                result4 == PackageManager.PERMISSION_GRANTED;
    }

    public void showPermissionDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("IF you don't give permission then our application will not work properly")
                .setCancelable(false)

                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
        builder.show();
    }
}
