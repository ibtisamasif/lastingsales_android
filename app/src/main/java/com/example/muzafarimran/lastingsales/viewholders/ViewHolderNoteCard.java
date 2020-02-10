package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditNoteActivity;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderNoteCard extends RecyclerView.ViewHolder {

    private final TextView tvCreatedAt;
    private TextView tvNote;
    private ImageView ivDelete;
    private final ConstraintLayout cl;

    public ViewHolderNoteCard(View v) {
        super(v);
        cl = v.findViewById(R.id.cl);
        tvNote = v.findViewById(R.id.tvNote);
        tvCreatedAt = v.findViewById(R.id.tvCreatedAt);
        ivDelete = v.findViewById(R.id.ivDelete);
    }

    public void bind(Object item, int position, Context mContext) {
        final LSNote noteItem = (LSNote) item;
        tvNote.setText(noteItem.getNoteText());
        tvCreatedAt.setText(noteItem.getCreatedAt());
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete ");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (noteItem.getServerId() != null) {
                            noteItem.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_DELETE_NOT_SYNCED);
                            noteItem.save();
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                            dataSenderAsync.run();
                            Toast.makeText(mContext, "Will be deleted once synced", Toast.LENGTH_SHORT).show();
                        } else {
                            noteItem.delete();
                            NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                            TinyBus bus = TinyBus.from(mContext);
                            bus.post(mNoteAdded);
                            Toast.makeText(mContext, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_EDIT_EXISTING_NOTE);
                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_NOTE_ID, "" + noteItem.getId());
                mContext.startActivity(intent);
            }
        });
    }
}
