package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.Utils.CallRecord;

import java.util.Locale;

import de.halfbit.tinybus.TinyBus;

public class MainActivity extends AppCompatActivity {


//    LastingSalesDatabaseHelper dbh;
    CallRecord callRecord;
    SessionManager sessionManager;
//    private tabSelectedListener tabselectedlistener = new tabSelectedListener();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("");
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        }
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addOnTabSelectedListener(this.tabselectedlistener);
//        tabLayout.getTabAt(0).setIcon(R.drawable.menu_icon_home_selected_aqua);
//        tabLayout.getTabAt(1).setIcon(R.drawable.menu_icon_phone);
//        tabLayout.getTabAt(2).setIcon(R.drawable.menu_icon_contact);
//        tabLayout.getTabAt(3).setIcon(R.drawable.menu_icon_menu);
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
        BackPressedEventModel model = new BackPressedEventModel();
        TinyBus.from(getApplicationContext()).post(model);
        if (!model.backPressHandled) {
            super.onBackPressed();
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
//                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu_selected_aqua);
//                    // ((TextView)(myToolbar.findViewById(R.id.title))).setText("MENU");
//                    break;
//            }
//           /* if (tab.getPosition() != 0){
//                myToolbar.findViewById(R.id.title).setVisibility(View.VISIBLE);
//                myToolbar.findViewById(R.id.lasting_sales_logo).setVisibility(View.GONE);
//                myToolbar.setMinimumHeight(100);
//            }else {
//                myToolbar.findViewById(R.id.lasting_sales_logo).setVisibility(View.VISIBLE);
//                myToolbar.findViewById(R.id.title).setVisibility(View.GONE);
//                myToolbar.setMinimumHeight(350);
//            }*/
//        }

//        @Override
//        public void onTabUnselected(TabLayout.Tab tab) {
//
//            switch (tab.getPosition()) {
//                case 0:
//                    tab.setIcon(R.drawable.menu_icon_home);
//                    break;
//
//                case 1:
//                    tab.setIcon(R.drawable.menu_icon_phone);
//                    break;
//
//                case 2:
//                    tab.setIcon(R.drawable.menu_icon_contact);
//                    break;
//                case 3:
//                    tab.setIcon(R.drawable.menu_icon_menu);
//                    break;
//            }
//        }
//
//        @Override
//        public void onTabReselected(TabLayout.Tab tab) {
//            //int position = tab.getPosition();
//        }
//    }
}