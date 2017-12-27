package com.example.muzafarimran.lastingsales.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.IgnoredContact;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Calendar;

@Deprecated
public class AddEditLeadService extends Service {
    public static final String TAG = "AddEditLeadService";
    //    Constants
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";
    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";
    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_NAME = "contact_name";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_CONTACT_TYPE = "contact_type";
    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    String phoneNumberFromLastActivity;
    String contactNameFromLastActivity;
    String preSelectedContactType = LSContact.CONTACT_TYPE_SALES;
    private EditText etContactName;
    private TextView etContactPhone;
    private Button bSave;
    private ImageView bClose;
    private CheckBox cbIgnore;
    private LinearLayout llContactDetailsFollowupScreen;
    private LayoutInflater inflater;
    WindowManager wm;
    Context serviceContext;
    ImageView logoSmall;
    ImageButton closeButtonShrunkView;
    private View largeInflatedView;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams shrunkParams;
    private Button bNo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        super.onCreate();
        serviceContext = this;
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
                contactNameFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_CONTACT_NAME);
                selectedContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            }
        }
        executeServiceWork();
        return START_STICKY;
    }

    private void executeServiceWork() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        largeInflatedView = inflater.inflate(R.layout.add_edit_lead_service_layout, null);
        initializeAllViewsFromThisParentView(largeInflatedView);
//        closeButton = (ImageButton) largeInflatedView.findViewById(R.id.bClose);
//        closeButton.setOnClickListener(getHideButtonClickListener());

//        banner.setOnClickListener(getLargeLogoClickListener());
//        banner.setScaleType(ImageView.ScaleType.FIT_XY);
        params.dimAmount = 0.5f;
        wm.addView(largeInflatedView, params);
    }

    private void initializeAllViewsFromThisParentView(View largeInflatedView) {

        etContactName = (EditText) largeInflatedView.findViewById(R.id.etContactName);
        etContactName.getBackground().setColorFilter(getResources().getColor(R.color.md_white), PorterDuff.Mode.SRC_IN);
        etContactName.getBackground().clearColorFilter();
        etContactPhone = (TextView) largeInflatedView.findViewById(R.id.etContactPhone);
        bSave = (Button) largeInflatedView.findViewById(R.id.bSaveFollowupPopup);
        bClose = (ImageView) largeInflatedView.findViewById(R.id.bClose);
        bNo = (Button) largeInflatedView.findViewById(R.id.bNo);
        cbIgnore = (CheckBox) largeInflatedView.findViewById(R.id.cbIgnore);
        llContactDetailsFollowupScreen = (LinearLayout) largeInflatedView.findViewById(R.id.llContactDetailsAddContactScreen);
        // Mix Panel Event
        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
        try {
            mixpanel.track("Lead From Dialog - Shown");
        } catch (Exception e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
//        if launch mode is tag number then number is gotten out of bundle so it can be searched in
//        phonebook and the number can be populated in the editText Field
        Log.d(TAG, "onCreate: Tag Number");
        preSelectedContactType = selectedContactType;
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(getApplicationContext(), phoneNumberFromLastActivity);
        String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
        etContactPhone.setText(internationalNumber);
        if (contactNameFromLastActivity != null) {
            etContactName.setText(contactNameFromLastActivity);
        } else if (nameFromPhoneBook != null) {
            etContactName.setText(nameFromPhoneBook);
        }
        etContactName.setSelection(etContactName.getText().length());
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
//                stopThisService();
//                System.exit(0);
                Toast.makeText(serviceContext, "Closed!", Toast.LENGTH_SHORT).show();
            }
        });
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
//                stopThisService();
//                System.exit(0);
                Toast.makeText(serviceContext, "Added to Unlabeled!", Toast.LENGTH_SHORT).show();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbIgnore.isChecked()) {
                    Log.d(TAG, "onClick: Checked");
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    IgnoredContact.AddAsIgnoredContact(getApplicationContext(), contactPhone, contactName); //TODO centralize convertion in one class
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("type", "ignored");
                        mixpanel.track("Lead From Dialog - Clicked", props);
                    } catch (Exception e) {
                        Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                    }
                    stopSelf();
//                    stopThisService();
//                    System.exit(0);
                    Toast.makeText(serviceContext, "Finish", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: Not Checked");
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation) {
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(getApplicationContext(), contactPhone);
                        LSContact checkContact;
                        checkContact = LSContact.getContactFromNumber(intlNum);
//                        Log.d("testlog", "onClick: CheckContactType :" + checkContact.getContactType());
                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                                if (checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                    checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                }
                                checkContact.save();
                                String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                                if (checkContactInLocalPhonebook == null) {
                                    //Saving contact in native phonebook as well
                                    PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                                }
                                String projectToken = MixpanelConfig.projectToken;
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("type", "track");
                                    mixpanel.track("Lead From Dialog - Clicked", props);
                                    mixpanel.track("Lead From Dialog");
                                } catch (Exception e) {
                                    Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                                }
                                stopSelf();
//                                stopThisService();
//                                System.exit(0);
                                Toast.makeText(serviceContext, "Finish", Toast.LENGTH_SHORT).show();
                            }
                            stopSelf();
                            Toast.makeText(serviceContext, "Already Exists Converted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                            }
                            tempContact.save();
                            String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                            if (checkContactInLocalPhonebook == null) {
                                //Saving contact in native phonebook as well
                                PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                            }
                            String projectToken = MixpanelConfig.projectToken;
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                            try {
                                JSONObject props = new JSONObject();
                                props.put("type", "track");
                                mixpanel.track("Lead From Dialog - Clicked", props);
                                mixpanel.track("Lead From Dialog");
                            } catch (Exception e) {
                                Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                            }
                            stopSelf();
//                            stopThisService();
//                            System.exit(0);
                            Toast.makeText(serviceContext, "Finish", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    public View.OnTouchListener getOnTouchListenerDragDrop(final View shrunkView) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_MOVE | event.getAction() == MotionEvent.ACTION_DOWN )
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
               /* You can play around with the offset to set where you want the users finger to be on the view. Currently it should be centered.*/
                    int xOffset = v.getWidth() / 2;
                    int yOffset = v.getHeight() / 2;
                    int x = (int) event.getRawX() - xOffset;
                    int y = (int) event.getRawY() - yOffset;
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            x, y,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity.LEFT;
                    wm.updateViewLayout(largeInflatedView, params);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private View.OnClickListener getLargeLogoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }

    private View.OnClickListener getHideButtonClickListener() {
        return
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout largeLayout = (LinearLayout) largeInflatedView;
                        largeLayout.removeAllViews();
                        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View shrunkView = inflater.inflate(R.layout.uiocall_popup_shrunk_layout, null);
                        shrunkParams = new WindowManager.LayoutParams();
                        shrunkParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                        shrunkParams.horizontalMargin = 0;
                        shrunkParams.verticalMargin = 0;
                        shrunkView.setLayoutParams(shrunkParams);
                        shrunkView.setOnClickListener(getShrunkViewOnClickListener());
                        logoSmall = (ImageView) shrunkView.findViewById(R.id.imLogoSmall);
                        logoSmall.setScaleType(ImageView.ScaleType.FIT_XY);
                        closeButtonShrunkView = (ImageButton) shrunkView.findViewById(R.id.closeImageButtonShrunkView);
                        closeButtonShrunkView.setOnClickListener(getCloseButtonListener());
                        ((LinearLayout) largeLayout).addView(shrunkView);
                    }
                };
    }

    private View.OnLongClickListener getShrunkViewLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(getOnTouchListenerDragDrop(v));
                return true;
            }
        };
    }

    private View.OnClickListener getShrunkViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View inflatedView;
                final LinearLayout mainView = (LinearLayout) largeInflatedView;
                mainView.removeAllViews();
                inflatedView = inflater.inflate(R.layout.add_edit_lead_service_layout, null);
                bClose = (ImageButton) inflatedView.findViewById(R.id.bClose);
                bClose.setOnClickListener(getHideButtonClickListener());
                initializeAllViewsFromThisParentView(inflatedView);
//                banner.setScaleType(ImageView.ScaleType.FIT_XY);
//                banner.setOnClickListener(getLargeLogoClickListener());
                ((LinearLayout) largeInflatedView).addView(inflatedView);
            }
        };
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if(keyCode == KeyEvent.KEYCODE_HOME)
//        {
//            stopSelf();
//        }
//    });

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        wm.removeView(largeInflatedView);
        super.onDestroy();
        stopThisService();
        System.exit(0);
    }

    public void stopThisService() {
        ((Service) serviceContext).stopSelf();
    }

    public View.OnClickListener getCloseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
                Toast.makeText(serviceContext, "Clicked Close Button", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void showFeedbackPopup() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View feedbackView = inflater.inflate(R.layout.uiocall_popup_feedback_layout, null);
        RatingBar feedbackRatingBar = (RatingBar) feedbackView.findViewById(R.id.feedbackStarsBar);
        LayerDrawable stars = (LayerDrawable) feedbackRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        feedbackRatingBar.setStepSize(1);
        feedbackRatingBar.setNumStars(5);
        Toast.makeText(getApplicationContext(), "Rating: " + feedbackRatingBar.getRating(), Toast.LENGTH_SHORT).show();
        feedbackRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getApplicationContext(), "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        wm.addView(feedbackView, params);
    }
}