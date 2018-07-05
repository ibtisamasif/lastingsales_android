package com.example.muzafarimran.lastingsales.service;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;

public class CallService extends Service {


    View view;
    WindowManager manager;
    private String num;
    WindowManager.LayoutParams params;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    public CallService() {
    }


    boolean callingMethod = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        num = intent.getStringExtra("no");


        if (!callingMethod) {
            dialogBox();
        }
        callingMethod = true;

        Log.d("CallService", "OnstartCommand is call");
        return START_STICKY;
    }


    public void dialogBox() {
        Toast.makeText(this, "Number get from intent" + num, Toast.LENGTH_SHORT).show();

        view = LayoutInflater.from(this).inflate(R.layout.aftercallflyer_layout, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT

        );

        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        manager.addView(view, params);

       /* SessionManager sessionManager=new SessionManager(CallService.this);

        String num=sessionManager.getTmpUserNO();*/

        ImageButton close = view.findViewById(R.id.ibClose);

        close.setOnClickListener(v -> {
            Toast.makeText(CallService.this, "You click close button", Toast.LENGTH_SHORT).show();


            LSContact updateContacct = LSContact.getContactFromNumber(num);
            if (updateContacct != null) {
                updateContacct.setContactSave("false");
                updateContacct.save();
                Toast.makeText(CallService.this, "set false", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("setcontact save ", "null");
            }


            manager.removeView(view);
            stopSelf();
        });


        EditText addContactField = view.findViewById(R.id.afterCallAddContactField);
        TextView showNumber = view.findViewById(R.id.afterCallContactNumber);
        TextView showNumber1 = view.findViewById(R.id.tvContactName);
        Button addBtn = view.findViewById(R.id.afterCallAddContactAddBtn);
        CheckBox ignoreCB = view.findViewById(R.id.afterCallAddContactCb);


        if (num != null) {
            showNumber.setText(num);
            showNumber1.setText(num);
        } else {
            showNumber.setText(num);
            showNumber1.setText(num);
        }
        ignoreCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ignoreCB.isChecked()) {
                    addBtn.setText("Ignore");
                    addContactField.setVisibility(View.GONE);
                } else {
                    addContactField.setVisibility(View.VISIBLE);
                    addBtn.setText("Save Contact");
                }

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ignoreCB.isChecked()) {

                    LSIgnoreList ignoreContact = new LSIgnoreList();
                    ignoreContact.setNumber(num);
                    if (ignoreContact.save() > 0) {
                        Log.d("amir", "add to ignore list");
                        manager.removeView(view);
                        stopSelf();
                    } else {
                        Log.d("amir", "error add to ignore list");
                    }

                    //ending....


                } else {


                    if (addContactField.getText().toString().isEmpty()) {

                        Toast.makeText(CallService.this, "Please enter valid name....", Toast.LENGTH_SHORT).show();
                        Log.d("amir validation ", "please enter name!!!");
                    } else {

                        Toast.makeText(CallService.this, "You enter " + addContactField.getText().toString(), Toast.LENGTH_SHORT).show();


                        LSContact updateContacct = LSContact.getContactFromNumber(num);
                        updateContacct.setContactName(addContactField.getText().toString());
                        updateContacct.setContactSave("true");
                        updateContacct.save();


                        if (updateContacct.save() > 0) {
                            Log.d("amir", "update contact");
                            Toast.makeText(CallService.this, "updated", Toast.LENGTH_SHORT).show();
                            manager.removeView(view);
                            stopSelf();
                        } else {
                            Log.d("amir", "updte contact error");
                            Toast.makeText(CallService.this, "update contact error", Toast.LENGTH_SHORT).show();
                            //show error

                        }


                        //code end here.....


                    }
                }

            }
        });


        view.findViewById(R.id.cl).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {

                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        manager.updateViewLayout(view, params);
                        return true;
                }
                return false;
            }

        });
    }


    @Override
    public void onCreate() {

        Log.d("CallService", "OnCreate is call");


       /* Toast.makeText(this, "Number get from intent"+num, Toast.LENGTH_SHORT).show();

        view= LayoutInflater.from( this ).inflate( R.layout.aftercallflyer_layout,null);

        WindowManager.LayoutParams params=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT

        );

        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(view, params);

        SessionManager sessionManager=new SessionManager(CallService.this);

        String num=sessionManager.getTmpUserNO();

        ImageButton close=view.findViewById( R.id.ibClose );

        close.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( CallService.this, "You click close button", Toast.LENGTH_SHORT ).show();


                LSContact updateContacct = LSContact.getContactFromNumber(num);
             if(updateContacct!=null) {
                 updateContacct.setContactSave("false");
                 updateContacct.save();
                 Toast.makeText(CallService.this, "set false", Toast.LENGTH_SHORT).show();
             }else{
                 Log.d("setcontact save ","null");
             }


                manager.removeView(view);
                stopSelf();
            }
        } );



        EditText addContactField=view.findViewById(R.id.afterCallAddContactField);
        TextView showNumber=view.findViewById(R.id.afterCallContactNumber);
        TextView showNumber1=view.findViewById(R.id.tvContactName);
        Button addBtn=view.findViewById(R.id.afterCallAddContactAddBtn);
        CheckBox ignoreCB=view.findViewById(R.id.afterCallAddContactCb);



        if(num!=null) {
            showNumber.setText(num);
            showNumber1.setText(num);
        }
        else {
            showNumber.setText(num);
            showNumber1.setText(num);
        }
        ignoreCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ignoreCB.isChecked()){
                    addBtn.setText("Ignore");
                    addContactField.setVisibility(View.GONE);
                }
                else{
                    addContactField.setVisibility(View.VISIBLE);
                    addBtn.setText("Save Contact");
                }

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ignoreCB.isChecked()){

                    LSIgnoreList ignoreContact = new LSIgnoreList();
                    ignoreContact.setNumber(num);
                    if(ignoreContact.save()>0){
                        Log.d("amir","add to ignore list");
                        manager.removeView(view);
                        stopSelf();
                    }else{
                        Log.d("amir","error add to ignore list");
                    }

                    //ending....



                }else {


                    if (addContactField.getText().toString().isEmpty()) {

                        Toast.makeText(CallService.this, "Please enter valid name....", Toast.LENGTH_SHORT).show();
                        Log.d("amir validation ","please enter name!!!");
                    } else {

                        Toast.makeText(CallService.this, "You enter "+addContactField.getText().toString(), Toast.LENGTH_SHORT).show();




                            LSContact updateContacct = LSContact.getContactFromNumber(num);
                            updateContacct.setContactName(addContactField.getText().toString());
                         updateContacct.setContactSave("true");
                            updateContacct.save();



                            if (updateContacct.save() > 0) {
                                Log.d("amir", "update contact");
                                Toast.makeText(CallService.this, "updated", Toast.LENGTH_SHORT).show();
                                manager.removeView(view);
                                stopSelf();
                            } else {
                                Log.d("amir", "updte contact error");
                                Toast.makeText(CallService.this, "update contact error", Toast.LENGTH_SHORT).show();
                                //show error

                            }


                        //code end here.....


                    }
                }

            }
        });












        view.findViewById(R.id.cl).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {

                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        manager.updateViewLayout( view, params );
                        return true;
                }
                return false;
            }

        });*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "close window", Toast.LENGTH_SHORT).show();

    }
}