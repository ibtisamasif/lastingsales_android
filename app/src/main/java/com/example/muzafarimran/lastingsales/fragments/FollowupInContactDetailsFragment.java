package com.example.muzafarimran.lastingsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditFollowUpsActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ibtisam on 12/30/2016.
 */

public class FollowupInContactDetailsFragment extends TabFragment {


    public static final String TAG = "FollowupInContactFra";
    MaterialSearchView searchView;
    TempFollowUp selectedFolloup = null;
    Button addFollowupBtn;
    LinearLayout llFolloupNoteRow;
    LinearLayout llFolloupDateTimeRow;
    TextView tvFollowupNoteText;
    TextView tvFollowupDateTime;
    ImageButton ibEditFollowup;
//    FrameLayout followupsListHolderFrameLayout;
    //    private String number = "";
    private Long contactIDLong;
    private LSContact selectedContact;
    private FollowupsTodayListFragment followupsTodayListFragment;

    public static FollowupInContactDetailsFragment newInstance(int page, String title, Long id) {
        FollowupInContactDetailsFragment fragmentFirst = new FollowupInContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followup_contact_details, container, false); //TODO crash on note 2 outOfMemory

        addFollowupBtn = (Button) view.findViewById(R.id.bAddFollowupContactDetailsScreen);
        llFolloupNoteRow = (LinearLayout) view.findViewById(R.id.followupNoteRow);
        llFolloupDateTimeRow = (LinearLayout) view.findViewById(R.id.followupDateTimeRow);
        ibEditFollowup = (ImageButton) view.findViewById(R.id.ibEditFollowupButton);
        tvFollowupNoteText = (TextView) view.findViewById(R.id.followupNoteText);
        tvFollowupDateTime = (TextView) view.findViewById(R.id.followupDateTimeText);
//        followupsListHolderFrameLayout = (FrameLayout) view.findViewById(R.id.followupsListHolderFrameLayout);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        if (selectedContact != null) {
            addFollowupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(getActivity(), AddEditFollowUpsActivity.class);
                    myIntent.putExtra(AddEditFollowUpsActivity.ACTIVITY_LAUNCH_MODE, AddEditFollowUpsActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
                    myIntent.putExtra(AddEditFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                    startActivity(myIntent);
                }
            });
            ibEditFollowup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(getActivity(), AddEditFollowUpsActivity.class);
                    myIntent.putExtra(AddEditFollowUpsActivity.ACTIVITY_LAUNCH_MODE, AddEditFollowUpsActivity.LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP);
                    myIntent.putExtra(AddEditFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                    myIntent.putExtra(AddEditFollowUpsActivity.TAG_LAUNCH_MODE_FOLLOWUP_ID, selectedFolloup.getId() + "");
                    startActivity(myIntent);
                }
            });
//            followupsTodayListFragment = new FollowupsTodayListFragment();
//            Bundle newBundle = new Bundle();
//            newBundle.putLong("contactID", selectedContact.getId());
//            followupsTodayListFragment.setArguments(newBundle);
//            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//            transaction.replace(R.id.followupsListHolderFrameLayout, followupsTodayListFragment);
//            transaction.commit();
        }
        return view;
    }

    public void updateUi() {
        ArrayList<TempFollowUp> allFollowupsOfThisContact = selectedContact.getAllFollowups();
//        ArrayList<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getAllFollowupsFromContactId(selectedContact.getId()+"");
        Calendar now = Calendar.getInstance();
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                if (oneFollowup.getDateTimeForFollowup() - 30000 > now.getTimeInMillis()) { //TODO not an ethical way to fix this bug.
                    Log.d(TAG, "updateUi time difference: " + (oneFollowup.getDateTimeForFollowup() - now.getTimeInMillis()));
                    selectedFolloup = oneFollowup;
                    break;
                } else {
                    selectedFolloup = null;
                }
            }
        }
        if (selectedFolloup == null) {
            Log.d(TAG, "updateUi: if called");
            llFolloupNoteRow.setVisibility(View.GONE);
            llFolloupDateTimeRow.setVisibility(View.GONE);
            ibEditFollowup.setVisibility(View.GONE);
            addFollowupBtn.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "updateUi: else called");
            addFollowupBtn.setVisibility(View.GONE);
            llFolloupNoteRow.setVisibility(View.VISIBLE);
            llFolloupDateTimeRow.setVisibility(View.VISIBLE);
            ibEditFollowup.setVisibility(View.VISIBLE);
            tvFollowupNoteText.setText(selectedFolloup.getTitle());
            Calendar followupTimeDate = Calendar.getInstance();
            followupTimeDate.setTimeInMillis(selectedFolloup.getDateTimeForFollowup());
            String dateTimeForFollowupString;
            dateTimeForFollowupString = followupTimeDate.get(Calendar.DAY_OF_MONTH) + "-"
                    + (followupTimeDate.get(Calendar.MONTH) + 1) + "-" + followupTimeDate.get(Calendar.YEAR)
                    + " at " + followupTimeDate.get(Calendar.HOUR_OF_DAY) + " : " + followupTimeDate.get(Calendar.MINUTE);
            tvFollowupDateTime.setText(dateTimeForFollowupString);
        }
    }
}
