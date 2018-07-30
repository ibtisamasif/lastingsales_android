package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.CommentItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderCommentCard extends RecyclerView.ViewHolder {

    private final TextView tvCreatedAt;
    //    private ImageView ivDelete;
    private final LinearLayout singleMessageContainer;
    private TextView tvComment;

    public ViewHolderCommentCard(View v) {
        super(v);
        singleMessageContainer = v.findViewById(R.id.singleMessageContainer);
        tvComment = v.findViewById(R.id.tvComment);
        tvCreatedAt = v.findViewById(R.id.tvCreatedAt);
//        ivDelete = v.findViewById(R.id.ivDelete);
    }

    public void bind(Object item, int position, Context mContext) {
        final CommentItem commentItem = (CommentItem) item;
        singleMessageContainer.setGravity(commentItem.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
        tvComment.setText(commentItem.getCommentText());
        tvComment.setBackgroundResource(commentItem.isLeft() ? R.drawable.bubble_b : R.drawable.bubble_a);

        LinearLayout.LayoutParams lllp = (LinearLayout.LayoutParams) tvComment.getLayoutParams();
        if (commentItem.isLeft()) {
            lllp.gravity = Gravity.LEFT;
        } else {
            lllp.gravity = Gravity.RIGHT;
        }
        tvComment.setLayoutParams(lllp);

        tvCreatedAt.setText(commentItem.getCreatedAt());
        tvCreatedAt.setLayoutParams(lllp);

//        ivDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//                alert.setTitle("Delete");
//                alert.setMessage("Are you sure to delete ");
//                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (noteItem.getServerId() != null) {
//                            noteItem.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_DELETE_NOT_SYNCED);
//                            noteItem.save();
//                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
//                            dataSenderAsync.run();
//                            Toast.makeText(mContext, "Will be deleted once synced", Toast.LENGTH_SHORT).show();
//                        } else {
//                            noteItem.delete();
//                            NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
//                            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                            bus.post(mNoteAdded);
//                            Toast.makeText(mContext, "Deleted successfully.", Toast.LENGTH_SHORT).show();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alert.show();
//            }
//        });
//        cl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, AddEditNoteActivity.class);
//                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_EDIT_EXISTING_NOTE);
//                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_NOTE_ID, "" + noteItem.getId());
//                mContext.startActivity(intent);
//            }
//        });
    }
}
