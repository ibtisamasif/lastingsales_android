package com.example.muzafarimran.lastingsales.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class FollowupsListAdapter extends ArrayAdapter<TempFollowUp> {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    private int listItemLayout;

    public FollowupsListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public FollowupsListAdapter(Context context, int layoutId, ArrayList<TempFollowUp> followupsList, Activity activity) {
        super(context, layoutId, followupsList);
        listItemLayout = layoutId;
        inflater = LayoutInflater.from(getContext());
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data tvContactName for this position
        TempFollowUp oneFollowup = getItem(position);
        final LSContact oneContact = oneFollowup.getContact();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(listItemLayout, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "name: " + oneContact.getContactName(), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_item_followups_name);
            viewHolder.followupNote = (TextView) convertView.findViewById(R.id.list_item_followups_note);
            viewHolder.followupDate = (TextView) convertView.findViewById(R.id.list_item_followups_date);
            viewHolder.followupTime = (TextView) convertView.findViewById(R.id.list_item_followups_time);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(oneFollowup.getDateTimeForFollowup());  //here your time in miliseconds
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        // Populate the data into the template view using the data object
        viewHolder.contactName.setText(oneContact.getContactName());
        viewHolder.followupNote.setText(oneFollowup.getTitle());
        viewHolder.followupDate.setText(date);
        viewHolder.followupTime.setText(time);
        // Return the completed view to render on screen
        return convertView;
    }

    // The ViewHolder, only one tvContactName for simplicity and demonstration purposes, you can put all the views inside a row of the list into this ViewHolder
    private static class ViewHolder {
        TextView contactName;
        TextView followupDate;
        TextView followupTime;
        TextView followupNote;
    }
}