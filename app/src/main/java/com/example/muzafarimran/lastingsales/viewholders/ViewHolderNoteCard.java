package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditNoteActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderNoteCard extends RecyclerView.ViewHolder {

    private TextView tvNote;
    private ImageView ivDelete;
    private final ConstraintLayout cl;

    public ViewHolderNoteCard(View v) {
        super(v);
        tvNote = v.findViewById(R.id.tvNote);
        ivDelete = v.findViewById(R.id.ivDelete);
        cl = v.findViewById(R.id.cl);
    }

    public void bind(Object item, int position, Context mContext) {
        final LSNote noteItem = (LSNote) item;
        tvNote.setText(noteItem.getNoteText());
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Deleting...", Toast.LENGTH_SHORT).show();
            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_EDIT_EXISTING_NOTE);
                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_NOTE_ID, ""+noteItem.getId());
                mContext.startActivity(intent);
            }
        });
    }
}
