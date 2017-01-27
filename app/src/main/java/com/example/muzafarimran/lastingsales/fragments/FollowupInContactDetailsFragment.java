package com.example.muzafarimran.lastingsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddNewFollowUpsActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ibtisam on 12/30/2016.
 */

public class FollowupInContactDetailsFragment extends TabFragment{


    public static final String TAG = "FollowupInContactDetailsFragment";
    public static final String CONTACT_ID = "contact_id";
    MaterialSearchView searchView;
    TempFollowUp selectedFolloup = null;
    private String number = "";
    private LSContact selectedContact;
    Button addFollowupBtn;
    LinearLayout llFolloupNoteRow;
    LinearLayout llFolloupDateTimeRow;
    TextView tvFollowupNoteText;
    TextView tvFollowupDateTime;
    ImageButton ibEditFollowup;
    FrameLayout followupsListHolderFrameLayout;
    private FollowupsTodayListFragment followupsTodayListFragment;

    public static FollowupInContactDetailsFragment newInstance(int page, String title , String number) {
        FollowupInContactDetailsFragment fragmentFirst = new FollowupInContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putString("someNumber", number);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followup_contact_details, container, false);

        addFollowupBtn = (Button) view.findViewById(R.id.bAddFollowupContactDetailsScreen);
        llFolloupNoteRow = (LinearLayout) view.findViewById(R.id.followupNoteRow);
        llFolloupDateTimeRow = (LinearLayout) view.findViewById(R.id.followupDateTimeRow);
        tvFollowupNoteText = (TextView) view.findViewById(R.id.followupNoteText);
        tvFollowupDateTime = (TextView) view.findViewById(R.id.followupDateTimeText);
        ibEditFollowup = (ImageButton) view.findViewById(R.id.ibEditFollowupButton);
        followupsListHolderFrameLayout = (FrameLayout) view.findViewById(R.id.followupsListHolderFrameLayout);

        Bundle bundle = this.getArguments();
        number = bundle.getString("someNumber");
        if(number!=null) {
            selectedContact = LSContact.getContactFromNumber(number);
        addFollowupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), AddNewFollowUpsActivity.class);
                myIntent.putExtra(AddNewFollowUpsActivity.ACTIVITY_LAUNCH_MODE, AddNewFollowUpsActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
                myIntent.putExtra(AddNewFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                startActivity(myIntent);
            }
        });

        ArrayList<TempFollowUp> allFollowupsOfThisContact = selectedContact.getAllFollowups();
//        ArrayList<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getAllFollowupsFromContactId(selectedContact.getId()+"");
        Calendar now = Calendar.getInstance();
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                if (oneFollowup.getDateTimeForFollowup() > now.getTimeInMillis()) {
                    selectedFolloup = oneFollowup;
                    break;
                }
            }
        }
        if (selectedFolloup == null) {
            llFolloupNoteRow.setVisibility(View.GONE);
            llFolloupDateTimeRow.setVisibility(View.GONE);
            ibEditFollowup.setVisibility(View.GONE);
        } else {
            tvFollowupNoteText.setText(selectedFolloup.getTitle());
            Calendar followupTimeDate = Calendar.getInstance();
            followupTimeDate.setTimeInMillis(selectedFolloup.getDateTimeForFollowup());
            String dateTimeForFollowupString;
            dateTimeForFollowupString = followupTimeDate.get(Calendar.DAY_OF_MONTH) + "-"
                    + (followupTimeDate.get(Calendar.MONTH) + 1) + "-" + followupTimeDate.get(Calendar.YEAR)
                    + " at " + followupTimeDate.get(Calendar.HOUR_OF_DAY) + " : " + followupTimeDate.get(Calendar.MINUTE);
            tvFollowupDateTime.setText(dateTimeForFollowupString);
            addFollowupBtn.setVisibility(View.GONE);
        }
        ibEditFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), AddNewFollowUpsActivity.class);
                myIntent.putExtra(AddNewFollowUpsActivity.ACTIVITY_LAUNCH_MODE, AddNewFollowUpsActivity.LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP);
                myIntent.putExtra(AddNewFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                myIntent.putExtra(AddNewFollowUpsActivity.TAG_LAUNCH_MODE_FOLLOWUP_ID, selectedFolloup.getId() + "");
                startActivity(myIntent);
            }
        });

                //Bundle bundle = new Bundle();
                //bundle.putString(NotesByContactsFragment.CONTACT_ID, selectedContact.getId().toString());
                followupsTodayListFragment = new FollowupsTodayListFragment();
                //followupsTodayListFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.followupsListHolderFrameLayout, followupsTodayListFragment);
                transaction.commit();

        }
        return view;
    }
}
