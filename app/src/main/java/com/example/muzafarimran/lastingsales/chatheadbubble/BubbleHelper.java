package com.example.muzafarimran.lastingsales.chatheadbubble;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

public class BubbleHelper extends AppCompatActivity{
    private BubbleLayout bubbleView;
    private TextView tvNoteTextUIOCallPopup;
    private static BubbleHelper mInstance;
    private BubblesManager bubblesManager;
    private Context context;

    public BubbleHelper(Context context) {
        this.context = context;
        bubblesManager = new BubblesManager.Builder(context).setTrashLayout(R.layout.notification_trash_layout).build();
        bubblesManager.initialize();
    }

    public static BubbleHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BubbleHelper(context);
        }
        return mInstance;
    }

    public void show(Long noteIdLong) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.notification_layout, null);
        tvNoteTextUIOCallPopup = (TextView)bubbleView.findViewById(R.id.tvNoteTextUIOCallPopup);

        LSNote tempNote = null;
        if (noteIdLong != null) {
            tempNote = LSNote.findById(LSNote.class, noteIdLong);
            tvNoteTextUIOCallPopup.setText(tempNote.getNoteText());
        }

        // this method call when user removes notification layout
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                Toast.makeText(context, "Removed !", Toast.LENGTH_SHORT).show();
            }
        });
        // this methoid call when cursor clicks on the notification layout( bubble layout)
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                Toast.makeText(context, "Clicked !", Toast.LENGTH_SHORT).show();
            }
        });

        // add bubble view into bubble manager
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    public void hide() {
        bubblesManager.removeBubble(bubbleView);
    }

}
