package com.example.muzafarimran.lastingsales.chatheadbubble;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BubbleHelper extends AppCompatActivity {
    private static final String TAG = "BubbleHelper";
    private final SessionManager sessionManager;
    private BubbleLayout bubbleView;
    private TextView tvNoteTextUIOCallPopup;
    private static BubbleHelper mInstance;
    private BubblesManager bubblesManager;
    private Context context;
    private TextView tvCallerHistoryName0;
    private TextView tvCallerHistoryName1;
    private TextView tvCallerHistoryLastCallDateTime0;
    //    private TextView tvCallerHistoryLastCallTimeAgo0;
    private ImageButton ibClose;
    private TextView tvName;
    private TextView tvCallerHistoryLastCallDateTime1;
//    private TextView tvCallerHistoryLastCallTimeAgo1;

    public BubbleHelper(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        bubblesManager = new BubblesManager.Builder(context).setTrashLayout(R.layout.notification_trash_layout).build();
        bubblesManager.initialize();
    }

    public static BubbleHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BubbleHelper(context);
        }
        return mInstance;
    }

    public void show(Long noteIdLong, String number, LSContact oneContact) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.notification_layout, null);
        tvNoteTextUIOCallPopup = (TextView) bubbleView.findViewById(R.id.tvNoteTextUIOCallPopup);
        tvCallerHistoryName0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName0);
        tvCallerHistoryLastCallDateTime0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime0);
//        tvCallerHistoryLastCallTimeAgo0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo0);
        tvCallerHistoryName1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName1);
        tvCallerHistoryLastCallDateTime1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime1);
//        tvCallerHistoryLastCallTimeAgo1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo1);
        tvName = (TextView) bubbleView.findViewById(R.id.tvName);
        tvName.setText(number);
        ibClose = (ImageButton) bubbleView.findViewById(R.id.ibClose);
        tvCallerHistoryName0.setText("");
        tvCallerHistoryLastCallDateTime0.setText("");
        tvCallerHistoryName1.setText("");
        tvCallerHistoryLastCallDateTime1.setText("");
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
//        if (oneContact != null) {
//            if (oneContact.getContactName() != null) {
//                tvName.setText(oneContact.getContactName());
//            } else if (name != null) {
//                tvName.setText(name);
//            }
////            else if (oneContact.getPhoneOne() != null) {
////                tvName.setText(oneContact.getPhoneOne());
////            }
//        }
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
                Toast.makeText(context, "Closed !", Toast.LENGTH_SHORT).show();
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
        bubblesManager.addBubble(bubbleView, 0, 0);

        fetchCustomerHistory(number);
    }

    public void show(String number) {

        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.notification_layout, null);
        tvNoteTextUIOCallPopup = (TextView) bubbleView.findViewById(R.id.tvNoteTextUIOCallPopup);
        tvCallerHistoryName0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName0);
        tvCallerHistoryLastCallDateTime0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime0);
//        tvCallerHistoryLastCallTimeAgo0 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo0);
        tvCallerHistoryName1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryName1);
        tvCallerHistoryLastCallDateTime1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallDateTime1);
//        tvCallerHistoryLastCallTimeAgo1 = (TextView) bubbleView.findViewById(R.id.tvCallerHistoryLastCallTimeAgo1);
        tvName = (TextView) bubbleView.findViewById(R.id.tvName);
        tvName.setText(number);
        ibClose = (ImageButton) bubbleView.findViewById(R.id.ibClose);
        tvCallerHistoryName0.setText("");
        tvCallerHistoryLastCallDateTime0.setText("");
        tvCallerHistoryName1.setText("");
        tvCallerHistoryLastCallDateTime1.setText("");
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, number);
//        if (name != null) {
//            tvName.setText(name);
//        }

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
        RequestQueue queue = Volley.newRequestQueue(context);
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
                Log.d(TAG, "onResponse() response = [" + respon + "]");
                try {
                    JSONObject jObj = new JSONObject(respon);
                    int responseCode = jObj.getInt("responseCode");
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
                            if (!user_id0.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName0.setText("Last contacted " + firstname0 + " " + lastname0);
                            } else {
                                tvCallerHistoryName0.setText("Last contacted with me");
                            }
                            tvCallerHistoryLastCallDateTime0.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0), "dd-MMM-yyyy"));
//                            tvCallerHistoryLastCallTimeAgo0.setText(PhoneNumberAndCallUtils.getTimeAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0) , context));
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
                            if (!user_id1.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName1.setText("Last contacted " + firstname1 + " " + lastname1);
                            } else {
                                tvCallerHistoryName1.setText("Last contacted with me");
                            }
                            tvCallerHistoryLastCallDateTime1.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call1), "dd-MMM-yyyy"));
//                            tvCallerHistoryLastCallTimeAgo1.setText(PhoneNumberAndCallUtils.getTimeAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0) , context));
                        }
                    }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotGetCustomerHistory");
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
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

}
