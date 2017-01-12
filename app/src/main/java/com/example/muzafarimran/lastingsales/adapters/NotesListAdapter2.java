package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.EditNoteActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;
import java.util.List;


public class NotesListAdapter2 extends BaseAdapter {
    private ArrayList<LSNote> notesList;
    private Context mContext;
    private LayoutInflater mInflater;

    public NotesListAdapter2(Context mContext,  ArrayList<LSNote> notesList) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.note_list_item2, parent, false);
            viewHolder.tvShortNote = (TextView) convertView.findViewById(R.id.list_item_note_small);
            viewHolder.RlNoteRowLayout = (RelativeLayout) convertView.findViewById(R.id.note_row);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvShortNote.setText(getShortenedString(oneNote.getNoteText()));
        viewHolder.RlNoteRowLayout.setOnClickListener(new ShowEditNoteListener(oneNote, viewHolder.RlNoteRowLayout));
        return convertView;
    }

    public void setList(List<LSNote> allNotes) {
        notesList = (ArrayList<LSNote>) allNotes;
        notifyDataSetChanged();

    }

    private String getShortenedString(String inputString) {
        if (inputString.length() > 999999999) {
            return inputString.substring(0, 999999999) + "...";
        } else {
            return inputString;
        }
    }

    private static class ViewHolder {
        TextView tvShortNote;
        RelativeLayout RlNoteRowLayout;
    }

    private class ShowEditNoteListener implements View.OnClickListener {
        LSNote note;
        private RelativeLayout detailsLayout;

        public ShowEditNoteListener(LSNote note, RelativeLayout detailsLayout) {
            this.note = note;
            this.detailsLayout = detailsLayout;
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, ""+note.getId(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, EditNoteActivity.class);
            intent.putExtra("noteid", ""+note.getId());
            mContext.startActivity(intent);
        }
    }

}