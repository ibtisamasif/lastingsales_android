package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.AboutActivity;
import com.example.muzafarimran.lastingsales.activities.FollowupsActivity;
import com.example.muzafarimran.lastingsales.activities.NotesActivity;

//Fragment agent Profile
public class MoreFragment extends TabFragment {

    private LinearLayout llFollowUp, llNotes,llAbout;
    SessionManager sessionManager;
    private TextView tvNameOfUserMoreScreen;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        tvNameOfUserMoreScreen = (TextView) view.findViewById(R.id.tvNameOfUserMoreScreen);
        llFollowUp = (LinearLayout) view.findViewById(R.id.llFollowUpsMoreScreen);
        llNotes = (LinearLayout) view.findViewById(R.id.llNotesMoreFragment);
        llAbout = (LinearLayout) view.findViewById(R.id.llAboutMoreFragment);
//        llLogOut = (LinearLayout) view.findViewById(R.id.llLogOutMoreScreen);
        sessionManager = new SessionManager(getContext());
        tvNameOfUserMoreScreen.setText(sessionManager.getKeyLoginFirstName() + " " + sessionManager.getKeyLoginLastName());
        setOnClickListeners();
        setHasOptionsMenu(true);
        return view;
    }

    private void setOnClickListeners() {
        llFollowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FollowupsActivity.class));
            }
        });
        llNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NotesActivity.class));
            }
        });
        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });
//        llLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                sessionManager.logoutUser();
////                startActivity(new Intent(getContext(), LogInActivity.class));
////                getActivity().finish();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}