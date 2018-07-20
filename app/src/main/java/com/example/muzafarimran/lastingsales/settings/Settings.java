package com.example.muzafarimran.lastingsales.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.muzafarimran.lastingsales.R;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        getFragmentManager().beginTransaction().replace(
          android.R.id.content,
                new SettingFragment()

        ).commit();
    }

    public static class SettingFragment extends PreferenceFragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_setting);


        }


        private static Preference.OnPreferenceChangeListener listener=new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(preference instanceof SwitchPreference){
                    SwitchPreference switchPreference=(SwitchPreference)preference;

                    if(switchPreference.getKey().equals("personalCompany")){
                        //
                        Log.d("personalcompany","click");
                    }



                }



                return true;
            }
        };


    }

}
