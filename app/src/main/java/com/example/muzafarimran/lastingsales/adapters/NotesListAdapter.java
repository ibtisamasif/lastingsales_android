package com.example.muzafarimran.lastingsales.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class NotesListAdapter extends BaseAdapter {
    private int listItemLayout;
    private ArrayList<LSNote> notesList;
    private Context mContext;
    private Activity activity;
    private LayoutInflater mInflater;
    private LinearLayout noteDetails = null;
    private Boolean expanded = false;

    public NotesListAdapter(Context mContext, int layoutId, ArrayList<LSNote> notesList, Activity activity) {
        listItemLayout = layoutId;
        this.notesList = notesList;
        this.mContext = mContext;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int i) {
        return notesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data tvContactName for this position
        LSNote oneNote = (LSNote) getItem(position);
        LSContact oneContact = oneNote.getContactOfNote();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(listItemLayout, parent, false);
            viewHolder.tvContactName = (TextView) convertView.findViewById(R.id.list_item_notes_name);
            viewHolder.tvNoteTime = (TextView) convertView.findViewById(R.id.list_item_note_time);
            viewHolder.tvShortNote = (TextView) convertView.findViewById(R.id.list_item_note_small);
            viewHolder.tvNoteDate = (TextView) convertView.findViewById(R.id.list_item_note_date);
            viewHolder.tvNoteDetails = (TextView) convertView.findViewById(R.id.tv_note_details);
            viewHolder.llNotesDetailsLayout = (LinearLayout) convertView.findViewById(R.id.note_details_layout);
            viewHolder.llNotesDetailsLayout.setVisibility(View.GONE);
            convertView.setOnClickListener(new ShowDetailedNoteListener(oneNote, viewHolder.llNotesDetailsLayout));
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Calendar cl = Calendar.getInstance();
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        // Populate the data into the template view using the data object
        viewHolder.tvContactName.setText(oneContact.getContactName());
        viewHolder.tvShortNote.setText(getShortenedString(oneNote.getNoteText()));
        viewHolder.tvNoteDetails.setText(oneNote.getNoteText());
        viewHolder.tvNoteDate.setText(date);
        viewHolder.tvNoteTime.setText(time);
        // Return the completed view to render on screen
        return convertView;
    }

    public void setList(List<LSNote> allNotes) {
        notesList = (ArrayList<LSNote>) allNotes;
        notifyDataSetChanged();

    }

    private String getShortenedString(String inputString) {
        if (inputString.length() > 30) {
            return inputString.substring(0, 30) + "...";
        } else {
            return inputString;
        }
    }

    private static class ViewHolder {
        TextView tvContactName;
        TextView tvShortNote;
        TextView tvNoteDate;
        TextView tvNoteTime;
        TextView tvNoteDetails;
        LinearLayout llNotesDetailsLayout;
    }

    private class ShowDetailedNoteListener implements View.OnClickListener {
        LSNote note;
        private LinearLayout detailsLayout;

        public ShowDetailedNoteListener(LSNote note, LinearLayout detailsLayout) {
            this.note = note;
            this.detailsLayout = detailsLayout;
        }

        @Override
        public void onClick(View view) {
            if (expanded && noteDetails != null) {
                noteDetails.setVisibility(View.GONE);
                noteDetails = null;
                expanded = false;
            } else {
                noteDetails = detailsLayout;
                detailsLayout.setVisibility(View.VISIBLE);
                expanded = true;
            }
        }
    }
}