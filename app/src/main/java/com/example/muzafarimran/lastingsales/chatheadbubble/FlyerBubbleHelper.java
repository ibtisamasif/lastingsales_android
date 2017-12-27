package com.example.muzafarimran.lastingsales.chatheadbubble;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.listeners.LSContactProfileCallback;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.ContactProfileProvider;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FlyerBubbleHelper extends AppCompatActivity {
    private static final String TAG = "FlyerBubbleHelper";
    private static FlyerBubbleHelper mInstance;
    private SessionManager sessionManager;
    private BubbleLayout bubbleView;
    private TextView tvNoteTextUIOCallPopup;
    private BubblesManager bubblesManager;
    private Context context;
    private TextView tvCallerHistoryName0;
    private TextView tvCallerHistoryName1;
    private TextView tvCallerHistoryLastCallDateTime0;
    private TextView tvCallerHistoryLastCallTimeAgo0;
    private ImageButton ibClose;
    private TextView tvName;
    private TextView tvCallerHistoryLastCallDateTime1;
    private TextView tvCallerHistoryLastCallTimeAgo1;
    private SimpleDraweeView user_avatar;
    private static RequestQueue queue;
    private TextView tvError;

    public FlyerBubbleHelper() {
    }

    public FlyerBubbleHelper(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        queue = Volley.newRequestQueue(context);
        bubblesManager = new BubblesManager.Builder(context).setTrashLayout(R.layout.notification_trash_layout).build();
        bubblesManager.initialize();
    }

    public static FlyerBubbleHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FlyerBubbleHelper(context);
        }
        return mInstance;
    }


    public void show(Long noteIdLong, String number, LSContact oneContact) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.flyer_layout, null);
        user_avatar = (SimpleDraweeView) bubbleView.findViewById(R.id.user_avatar);
        tvNoteTextUIOCallPopup = (TextView) bubbleView.findViewById(R.id.tvNoteTextUIOCallPopup);
        tvCallerHistoryName0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName0);
        tvCallerHistoryLastCallDateTime0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime0);
        tvCallerHistoryLastCallTimeAgo0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo0);
        tvCallerHistoryName1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName1);
        tvCallerHistoryLastCallDateTime1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime1);
        tvCallerHistoryLastCallTimeAgo1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo1);
        tvName = (TextView) bubbleView.findViewById(R.id.tvName);
        tvName.setText(number);
        ibClose = (ImageButton) bubbleView.findViewById(R.id.ibClose);
        tvError = (TextView) bubbleView.findViewById(R.id.tvError);
        tvCallerHistoryName0.setText("");
        tvCallerHistoryLastCallDateTime0.setText("");
        tvCallerHistoryLastCallTimeAgo0.setText("");
        tvCallerHistoryName1.setText("");
        tvCallerHistoryLastCallDateTime1.setText("");
        tvCallerHistoryLastCallTimeAgo1.setText("");

        tvError.setVisibility(View.VISIBLE);
        tvCallerHistoryName0.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime0.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo0.setVisibility(View.GONE);
        tvCallerHistoryName1.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime1.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo1.setVisibility(View.GONE);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

//        String num = PhoneNumberAndCallUtils.numberToInterNationalNumber(context, "+92 324 7158218");
        ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
        contactProfileProvider.getContactProfile(number, new LSContactProfileCallback() {
            @Override
            public void onSuccess(LSContactProfile result) {
                Log.d(TAG, "onResponse: lsContactProfile: " + result);
                if (result != null) {
                    MyDateTimeStamp.setFrescoImage(user_avatar, result.getSocial_image());
                }
            }
        });


//        if (oneContact != null) {
//            tvName.setText(oneContact.getContactName());
//        }
//
//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
//        if (tvName.getText().toString().equals("null") && name != null) {
//            tvName.setText(name);
//        }

        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
        if (oneContact != null) {
            if (oneContact.getContactName() != null) {
                tvName.setText(oneContact.getContactName());
            }
        } else {
            if (name != null) {
                tvName.setText(name);
            } else {
                tvName.setText(number);
            }
        }

        LSNote tempNote = null;
        if (noteIdLong != null) {
            tempNote = LSNote.findById(LSNote.class, noteIdLong);
            tvNoteTextUIOCallPopup.setText(tempNote.getNoteText());
        } else {
            tvNoteTextUIOCallPopup.setText("None!");
        }

        // this method call when user removes notification layout
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
//                Toast.makeText(context, "Closed !", Toast.LENGTH_SHORT).show();
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

        fetchCustomerHistory(number);
    }

    public void show(String number) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.flyer_layout, null);
        user_avatar = (SimpleDraweeView) bubbleView.findViewById(R.id.user_avatar);
        tvNoteTextUIOCallPopup = (TextView) bubbleView.findViewById(R.id.tvNoteTextUIOCallPopup);
        tvCallerHistoryName0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName0);
        tvCallerHistoryLastCallDateTime0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime0);
        tvCallerHistoryLastCallTimeAgo0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo0);
        tvCallerHistoryName1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName1);
        tvCallerHistoryLastCallDateTime1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime1);
        tvCallerHistoryLastCallTimeAgo1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo1);
        tvName = (TextView) bubbleView.findViewById(R.id.tvName);
        tvName.setText(number);
        ibClose = (ImageButton) bubbleView.findViewById(R.id.ibClose);
        tvError = (TextView) bubbleView.findViewById(R.id.tvError);

        tvCallerHistoryName0.setText("");
        tvCallerHistoryLastCallDateTime0.setText("");
        tvCallerHistoryLastCallTimeAgo0.setText("");
        tvCallerHistoryName1.setText("");
        tvCallerHistoryLastCallDateTime1.setText("");
        tvCallerHistoryLastCallTimeAgo1.setText("");

        tvError.setVisibility(View.VISIBLE);
        tvCallerHistoryName0.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime0.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo0.setVisibility(View.GONE);
        tvCallerHistoryName1.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime1.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo1.setVisibility(View.GONE);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

//                String num = PhoneNumberAndCallUtils.numberToInterNationalNumber(context, "+92 324 7158218");
        ContactProfileProvider contactProfileProvider = new ContactProfileProvider(context);
        contactProfileProvider.getContactProfile(number, new LSContactProfileCallback() {
            @Override
            public void onSuccess(LSContactProfile result) {
                Log.d(TAG, "onResponse: lsContactProfile: " + result);
                if (result != null) {
                    MyDateTimeStamp.setFrescoImage(user_avatar, result.getSocial_image());
                }
            }
        });

        LSContact oneContact;
        oneContact = LSContact.getContactFromNumber(PhoneNumberAndCallUtils.numberToInterNationalNumber(context, number));

//        if (oneContact != null) {
//            tvName.setText(oneContact.getContactName());
//        }
//
//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
//        if (tvName.getText().toString().equals("null") && name != null) {
//            tvName.setText(name);
//        }

        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
        if (oneContact != null) {
            if (oneContact.getContactName() != null) {
                tvName.setText(oneContact.getContactName());
            }
        } else {
            if (name != null) {
                tvName.setText(name);
            } else {
                tvName.setText(number);
            }
        }

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
        fetchCustomerHistory(number);
    }

    public void hide() {
        bubblesManager.removeBubble(bubbleView);
    }

    private void fetchCustomerHistory(final String number) {
        Log.d(TAG, "fetchCustomersHistoryFunc: Fetching Data...");
        Log.d(TAG, "fetchCustomerHistory: Number: " + number);
        Log.d(TAG, "fetchCustomerHistory: Token: " + sessionManager.getLoginToken());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CUSTOMER_HISTORY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("phone", "" + number)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "fetchCustomerHistory: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String respon) {
                tvError.setVisibility(View.GONE);
                Log.d(TAG, "onResponse() response = [" + respon + "]");
                try {
                    JSONObject jObj = new JSONObject(respon);
//                    int responseCode = jObj.getInt("responseCode");
                    JSONObject response = jObj.getJSONObject("response");
                    JSONArray dataArray = response.getJSONArray("data");
                    Log.d(TAG, "onResponse: dataArray Lenght: " + dataArray.length());
//                    for (int i = 0; i < dataArray.length(); i++) {
                    if (dataArray.length() > 0) {
                        JSONObject jsonobject0 = dataArray.getJSONObject(0);
                        String last_call0 = jsonobject0.getString("last_call");
                        String user_id0 = jsonobject0.getString("user_id");
                        String duration0 = jsonobject0.getString("duration");
                        String firstname0 = jsonobject0.getString("firstname");
                        String lastname0 = jsonobject0.getString("lastname");
                        String role_id0 = jsonobject0.getString("role_id");
                        String name0 = jsonobject0.getString("name");

                        Log.d(TAG, "onResponse0: last_call: " + last_call0);
                        Log.d(TAG, "onResponse0: user_id: " + user_id0);
                        Log.d(TAG, "onResponse0: duration: " + duration0);
                        Log.d(TAG, "onResponse0: firstname: " + firstname0);
                        Log.d(TAG, "onResponse0: lastname: " + lastname0);
                        Log.d(TAG, "onResponse0: role_id: " + role_id0);
                        Log.d(TAG, "onResponse0: name: " + name0);

                        if (name0 != null && !name0.equals("null")) {
                            tvName.setText(name0);
                        }

                        if (firstname0 != null && lastname0 != null && last_call0 != null) {
                            tvCallerHistoryName0.setVisibility(View.VISIBLE);
                            if (!user_id0.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName0.setText("Last contacted " + firstname0 + " " + lastname0);
                            } else {
                                tvCallerHistoryName0.setText("Last contacted with me");
                            }
                            tvCallerHistoryLastCallDateTime0.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallTimeAgo0.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallDateTime0.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallTimeAgo0.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0), context) + ")");
                        }
                    }
                    if (dataArray.length() > 1) {
                        JSONObject jsonobject1 = dataArray.getJSONObject(1);
                        String last_call1 = jsonobject1.getString("last_call");
                        String user_id1 = jsonobject1.getString("user_id");
                        String duration1 = jsonobject1.getString("duration");
                        String firstname1 = jsonobject1.getString("firstname");
                        String lastname1 = jsonobject1.getString("lastname");
                        String role_id1 = jsonobject1.getString("role_id");
                        String name1 = jsonobject1.getString("name");

                        Log.d(TAG, "onResponse1: last_call: " + last_call1);
                        Log.d(TAG, "onResponse1: user_id: " + user_id1);
                        Log.d(TAG, "onResponse1: duration: " + duration1);
                        Log.d(TAG, "onResponse1: firstname: " + firstname1);
                        Log.d(TAG, "onResponse1: lastname: " + lastname1);
                        Log.d(TAG, "onResponse1: role_id: " + role_id1);
                        Log.d(TAG, "onResponse1: name: " + name1);

                        if (firstname1 != null && lastname1 != null && last_call1 != null) {
                            tvCallerHistoryName1.setVisibility(View.VISIBLE);
                            if (!user_id1.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName1.setText("Last contacted " + firstname1 + " " + lastname1);
                            } else {
                                tvCallerHistoryName1.setText("Last contacted with me");
                            }

                            tvCallerHistoryLastCallDateTime1.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallTimeAgo1.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallDateTime1.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call1), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallTimeAgo1.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call1), context) + ")");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotGetCustomerHistory");
                if (!NetworkAccess.isNetworkAvailable(context)) {
                    tvError.setText("Internet is required to view connections");
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 404) {
                                tvError.setText("Connections not found");
                            } else {
                                tvError.setText("Error loading");
                            }
                        } else {
                            tvError.setText("Poor Internet Connectivity");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        int MY_MAX_RETRIES = 3;
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

}
