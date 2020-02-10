package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditNewFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.Calendar;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderReminderCard extends RecyclerView.ViewHolder {

    private ConstraintLayout claddButton;
    private ConstraintLayout clReminderData;
    private CardView cardView;
    private TextView followupNoteText;
    private TextView followupDateTimeText;
    private Button bAddFollowupContactDetailsScreen;


    public ViewHolderReminderCard(View v) {
        super(v);
        claddButton = v.findViewById(R.id.claddButton);
        clReminderData = v.findViewById(R.id.clReminderData);
        cardView = v.findViewById(R.id.cv_item);
        followupNoteText = v.findViewById(R.id.followupNoteText);
        followupDateTimeText = v.findViewById(R.id.followupDateTimeText);
        bAddFollowupContactDetailsScreen = v.findViewById(R.id.bAddFollowupContactDetailsScreen);
    }

    public void bind(Object item, int position, Context mContext) {
        final TempFollowUp tempFollowUp = (TempFollowUp) item;

        if (tempFollowUp.getTitle().equals("#-1")) {
            claddButton.setVisibility(View.VISIBLE);
            clReminderData.setVisibility(View.GONE);
            bAddFollowupContactDetailsScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mContext, AddEditNewFollowupActivity.class);
                    myIntent.putExtra(AddEditNewFollowupActivity.ACTIVITY_LAUNCH_MODE, AddEditNewFollowupActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
                    myIntent.putExtra(AddEditNewFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, tempFollowUp.getContact().getId() + "");
                    mContext.startActivity(myIntent);
                }
            });
        } else {
            claddButton.setVisibility(View.GONE);
            clReminderData.setVisibility(View.VISIBLE);
            followupNoteText.setText(tempFollowUp.getTitle());
            Calendar followupTimeDate = Calendar.getInstance();
            followupTimeDate.setTimeInMillis(tempFollowUp.getDateTimeForFollowup());
            String dateTimeForFollowupString;
            dateTimeForFollowupString = followupTimeDate.get(Calendar.DAY_OF_MONTH) + "-"
                    + (followupTimeDate.get(Calendar.MONTH) + 1) + "-" + followupTimeDate.get(Calendar.YEAR)
                    + " at " + followupTimeDate.get(Calendar.HOUR_OF_DAY) + " : " + followupTimeDate.get(Calendar.MINUTE);
            followupDateTimeText.setText(dateTimeForFollowupString);
        }
    }
}
