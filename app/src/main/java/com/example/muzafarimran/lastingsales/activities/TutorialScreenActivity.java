package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;

public class TutorialScreenActivity extends AppCompatActivity {

    ViewPager viewPager;
    int[] layout;
    Button next;
    RadioGroup radioGroup;
    RadioButton isCompanyORPersonalRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_screen);
        viewPager = findViewById(R.id.tutorial_view_pager);
        layout = new int[]{
                R.layout.tutorial_screen1,
                R.layout.tutorial_screen2,
                R.layout.tutorial_screen3
        };
        TutorialAdapter adapter = new TutorialAdapter();
        viewPager.setAdapter(adapter);
        next = findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                Log.d("tutorial", "click next");
                int current = getItem(+1);
                if (current < layout.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    Log.d("tutorial", "jump to nav screen");
                    int checkId = radioGroup.getCheckedRadioButtonId();
                    isCompanyORPersonalRadio = findViewById(checkId);
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setFirstTimeLaunch(true);
                    SettingsManager settingsManager = new SettingsManager(getApplicationContext());
                    if (isCompanyORPersonalRadio.getText().equals("Company Phone")) {
                        settingsManager.setKeyStateIsCompanyPhone(true);
                        Log.d("tutorial", "set to company phone");
                    } else {
                        settingsManager.setKeyStateIsCompanyPhone(false);
                        Log.d("tutorial", "set to personal phone");
                    }
                    startActivity(new Intent(getApplicationContext(), NavigationBottomMainActivity.class));
                    finish();
                }
            }
        });
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public class TutorialAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(layout[position], container, false);
            radioGroup = viewGroup.findViewById(R.id.radio_company_personal_info);
            container.addView(viewGroup);
            return viewGroup;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // super.destroyItem( container, position, object );
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return layout.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
