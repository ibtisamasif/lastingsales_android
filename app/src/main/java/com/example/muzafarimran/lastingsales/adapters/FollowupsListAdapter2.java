package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class FollowupsListAdapter2 extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<TempFollowUp> follouwpsList;

    public FollowupsListAdapter2(Context context, ArrayList<TempFollowUp> follouwpsList) {
        this.context = context;
        this.follouwpsList = follouwpsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return follouwpsList.size();
    }

    @Override
    public Object getItem(int i) {
        return follouwpsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data tvContactName for this position
        TempFollowUp oneFollowup = (TempFollowUp) getItem(position);
        final LSContact oneContact = oneFollowup.getContact();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.folowup_list_item, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "name: " + oneContact.getContactName(), Toast.LENGTH_SHORT).show();
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
        boolean followupExpired = false;
        cl.setTimeInMillis(oneFollowup.getDateTimeForFollowup());  //here your time in miliseconds
        Calendar now = Calendar.getInstance();
        if (cl.getTimeInMillis() < now.getTimeInMillis()) {
            followupExpired = true;
        }
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        // Populate the data into the template view using the data object
        viewHolder.contactName.setText(oneContact.getContactName());
        viewHolder.followupNote.setText(oneFollowup.getTitle());
        viewHolder.followupDate.setText(date);
        viewHolder.followupTime.setText(time);
        if (followupExpired) {
            viewHolder.contactName.setTextColor(Color.GRAY);
            viewHolder.followupNote.setTextColor(Color.GRAY);
            viewHolder.followupDate.setTextColor(Color.GRAY);
            viewHolder.followupTime.setTextColor(Color.GRAY);
        } else {
            viewHolder.contactName.setTextColor(Color.BLACK);
            viewHolder.followupNote.setTextColor(Color.BLACK);
            viewHolder.followupDate.setTextColor(Color.BLACK);
            viewHolder.followupTime.setTextColor(Color.BLACK);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public void setList(List<TempFollowUp> tempFollowUps) {
        this.follouwpsList = (ArrayList<TempFollowUp>) tempFollowUps;
        notifyDataSetChanged();
    }

    // The ViewHolder, only one tvContactName for simplicity and demonstration purposes, you can put all the views inside a row of the list into this ViewHolder
    private static class ViewHolder {
        TextView contactName;
        TextView followupDate;
        TextView followupTime;
        TextView followupNote;
    }
}