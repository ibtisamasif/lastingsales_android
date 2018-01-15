package com.example.muzafarimran.lastingsales.chatheadbubble;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.IgnoredContact;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Calendar;

public class AddEditLeadServiceBubbleHelper extends AppCompatActivity {
    private static final String TAG = "AddEditLeadServiceBubbl";
    private static AddEditLeadServiceBubbleHelper mInstance;
    private SessionManager sessionManager;
    private BubbleLayout bubbleView;
    private BubblesManager bubblesManager;
    private Context context;
    private static RequestQueue queue;

    private EditText etPersonName;
    private TextView etContactPhone;
    private Button bSave;
    private ImageView bClose;
    private Button bNo;
    private CheckBox cbIgnore;
    private LinearLayout llContactDetailsFollowupScreen;

    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    String phoneNumberFromLastActivity;
    String contactNameFromLastActivity;
    String preSelectedContactType = LSContact.CONTACT_TYPE_SALES;

    public AddEditLeadServiceBubbleHelper() {
    }

    public AddEditLeadServiceBubbleHelper(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        queue = Volley.newRequestQueue(context);
        bubblesManager = new BubblesManager.Builder(context).setTrashLayout(R.layout.notification_trash_layout).build();
        bubblesManager.initialize();
    }

    public static AddEditLeadServiceBubbleHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AddEditLeadServiceBubbleHelper(context);
        }
        return mInstance;
    }

    public void show(String contactTypeSales, String internationalNumber, String name) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.add_edit_lead_service_layout, null);

        phoneNumberFromLastActivity = internationalNumber;
        contactNameFromLastActivity = name;
        selectedContactType = contactTypeSales;

        initializeAllViewsFromThisParentView(bubbleView);


        // this method call when user removes notification layout
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
//                Toast.makeText(context, "Removed !", Toast.LENGTH_SHORT).show();
            }
        });
        // this methoid call when cursor clicks on the notification layout( bubble layout)
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
//                Toast.makeText(context, "Clicked !", Toast.LENGTH_SHORT).show();
            }
        });
        // add bubble view into bubble manager
        bubblesManager.addBubble(bubbleView, 0, 0);
    }

    public void hide() {
        bubblesManager.removeBubble(bubbleView);
//        if (bubblesManager == null){
//            Log.d(TAG, "hide: bubblesManager == null");
//        }else {
//            Log.d(TAG, "hide: bubblesManager != null");
//        }
//        if (bubbleView == null){
//            Log.d(TAG, "hide: bubbleView == null");
//        }else {
//            Log.d(TAG, "hide: bubbleView != null");
//        }
    }

    private void initializeAllViewsFromThisParentView(View bubbleView) {

        etPersonName = (EditText) bubbleView.findViewById(R.id.etPersonName);
        etPersonName.getBackground().setColorFilter(context.getResources().getColor(R.color.md_white), PorterDuff.Mode.SRC_IN);
        etPersonName.getBackground().clearColorFilter();

        etContactPhone = (TextView) bubbleView.findViewById(R.id.etContactPhone);
        bSave = (Button) bubbleView.findViewById(R.id.bSaveFollowupPopup);
        bClose = (ImageView) bubbleView.findViewById(R.id.bClose);
        bNo = (Button) bubbleView.findViewById(R.id.bNo);
        cbIgnore = (CheckBox) bubbleView.findViewById(R.id.cbIgnore);
        llContactDetailsFollowupScreen = (LinearLayout) bubbleView.findViewById(R.id.llContactDetailsAddContactScreen);
        // Mix Panel Event
        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
        try {
            mixpanel.track("Lead From Dialog - Shown");
        } catch (Exception e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
//        if launch mode is tag number then number is gotten out of bundle so it can be searched in
//        phonebook and the number can be populated in the editText Field
        Log.d(TAG, "onCreate: Tag Number");
        preSelectedContactType = selectedContactType;
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(context, phoneNumberFromLastActivity);
        String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, internationalNumber);
        etContactPhone.setText(internationalNumber);
        if (contactNameFromLastActivity != null) {
            etPersonName.setText(contactNameFromLastActivity);
        } else if (nameFromPhoneBook != null) {
            etPersonName.setText(nameFromPhoneBook);
        }
        etPersonName.setSelection(etPersonName.getText().length());
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
//                stopThisService();
//                System.exit(0);
                Toast.makeText(context, "Closed!", Toast.LENGTH_SHORT).show();
            }
        });
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
//                stopThisService();
//                System.exit(0);
                Toast.makeText(context, "Added to Unlabeled!", Toast.LENGTH_SHORT).show();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbIgnore.isChecked()) {
                    Log.d(TAG, "onClick: Checked");
                    etPersonName.setError(null);
                    etContactPhone.setError(null);
                    String contactName = etPersonName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    IgnoredContact.AddAsIgnoredContact(context, contactPhone, contactName); //TODO centralize convertion in one class
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("type", "ignored");
                        mixpanel.track("Lead From Dialog - Clicked", props);
                    } catch (Exception e) {
                        Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                    }
                    hide();
//                    stopThisService();
//                    System.exit(0);
                    Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: Not Checked");
                    etPersonName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etPersonName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etPersonName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation) {
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(context, contactPhone);
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
                                String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, intlNum);
                                if (checkContactInLocalPhonebook == null) {
                                    //Saving contact in native phonebook as well
                                    PhoneNumberAndCallUtils.addContactInNativePhonebook(context, checkContact.getContactName(), checkContact.getPhoneOne());
                                }
                                String projectToken = MixpanelConfig.projectToken;
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("type", "track");
                                    mixpanel.track("Lead From Dialog - Clicked", props);
                                    mixpanel.track("Lead From Dialog");
                                } catch (Exception e) {
                                    Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                                }
                                hide();
//                                stopThisService();
//                                System.exit(0);
                                Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                            }
                            hide();
                            Toast.makeText(context, "Already Exists Converted Successfully", Toast.LENGTH_SHORT).show();
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
                            String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, intlNum);
                            if (checkContactInLocalPhonebook == null) {
                                //Saving contact in native phonebook as well
                                PhoneNumberAndCallUtils.addContactInNativePhonebook(context, checkContact.getContactName(), checkContact.getPhoneOne());
                            }
                            String projectToken = MixpanelConfig.projectToken;
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
                            try {
                                JSONObject props = new JSONObject();
                                props.put("type", "track");
                                mixpanel.track("Lead From Dialog - Clicked", props);
                                mixpanel.track("Lead From Dialog");
                            } catch (Exception e) {
                                Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                            }
                            hide();
//                            stopThisService();
//                            System.exit(0);
                            Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
