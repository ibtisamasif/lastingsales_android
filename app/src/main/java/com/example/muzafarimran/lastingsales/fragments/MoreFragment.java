package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.FollowupsActivity;
import com.example.muzafarimran.lastingsales.activities.LSContactChooserActivity;
import com.example.muzafarimran.lastingsales.activities.LogInActivity;
import com.example.muzafarimran.lastingsales.activities.NotesActivity;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends TabFragment {


    LinearLayout llFollowUp, llNotes, llLogOut;

    SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        llFollowUp = (LinearLayout) view.findViewById(R.id.llFollowUpsMoreScreen);
        llNotes = (LinearLayout) view.findViewById(R.id.llNotesMoreFragment);
        llLogOut = (LinearLayout) view.findViewById(R.id.llLogOutMoreScreen);

        sessionManager = new SessionManager(getContext());


        setOnClickListeners();

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
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManager.logoutUser();

                startActivity(new Intent(getContext(), LogInActivity.class));
                getActivity().finish();
            }
        });
    }


}