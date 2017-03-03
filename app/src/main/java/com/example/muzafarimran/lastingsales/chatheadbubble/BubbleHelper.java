package com.example.muzafarimran.lastingsales.chatheadbubble;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;


/**
 * Created by Home
 * on 2/2/2017.
 */
public class BubbleHelper {
    private   BubbleLayout bubbleView;
     private static BubbleHelper mInstance;
    private BubblesManager bubblesManager;
    private Context context ;
    public BubbleHelper(Context context){
        this.context =  context;
        bubblesManager = new BubblesManager.Builder(context).setTrashLayout(R.layout.notification_trash_layout).build();
        bubblesManager.initialize();
    }

public static BubbleHelper getInstance(Context context){
    if(mInstance==null) {
        mInstance = new BubbleHelper(context);
    }
    return mInstance;
}


 public void show(){

    bubbleView = (BubbleLayout) LayoutInflater.from(context)
             .inflate(R.layout.notification_layout, null);
     // this method call when user remove notification layout
     bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
         @Override
         public void onBubbleRemoved(BubbleLayout bubble) {
             Toast.makeText(context, "Removed !", Toast.LENGTH_SHORT).show();
         }
     });
     // this methoid call when cuser click on the notification layout( bubble layout)
     bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

         @Override
         public void onBubbleClick(BubbleLayout bubble) {
             Toast.makeText(context, "Clicked !", Toast.LENGTH_SHORT).show();
         }
     });

     // add bubble view into bubble manager
     bubblesManager.addBubble(bubbleView, 60, 20);

 }
    public void hide(){


        bubblesManager.removeBubble(bubbleView);
    }



}
