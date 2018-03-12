package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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
import com.example.muzafarimran.lastingsales.carditems.ConnectionItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderConnectionsCard extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderConnectionsCa";

    private LinearLayout llDynamicConnectionsContainer;
    Context mContext;
    private static SessionManager sessionManager;
    private static RequestQueue queue;
    private LSContact selectedContact;
    private TextView tvError;

    public ViewHolderConnectionsCard(View v) {
        super(v);

        llDynamicConnectionsContainer = (LinearLayout) v.findViewById(R.id.llDynamicConnectionsContainer);

    }

    public void bind(Object item, int position, Context mContext) {
        this.mContext = mContext;
        final ConnectionItem connectionItem = (ConnectionItem) item;

        tvError = new TextView(mContext);
        tvError.setText("Loading...");
        tvError.setGravity(Gravity.CENTER);
        llDynamicConnectionsContainer.addView(tvError);
        tvError.setVisibility(View.VISIBLE);

        selectedContact = connectionItem.lsContact;
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        fetchCustomerHistory(selectedContact.getPhoneOne());
    }

    private void fetchCustomerHistory(final String number) {
        Log.d(TAG, "fetchCustomersHistoryFunc: Fetching Data...");
        Log.d(TAG, "fetchCustomerHistory: Number: " + number);
        Log.d(TAG, "fetchCustomerHistory: Token: " + sessionManager.getLoginToken());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CUSTOMER_HISTORY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
//                .appendQueryParameter("phone", "+92 301 4775234")
                .appendQueryParameter("phone", "" + number)
//                .appendQueryParameter("api_token", "NVAPN67dqZU4bBW18ykrtylvfyXFogt1dh3Dgw2XsOFuQKaWLySRv058v1If")
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
                    if (dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            Log.d(TAG, "onResponse: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            JSONObject jsonobject = dataArray.getJSONObject(i);
                            String last_call = jsonobject.getString("last_call");
                            String user_id = jsonobject.getString("user_id");
                            String duration = jsonobject.getString("duration");
                            String firstname = jsonobject.getString("firstname");
                            String lastname = jsonobject.getString("lastname");
                            String role_id = jsonobject.getString("role_id");
                            String name = jsonobject.getString("name");

                            Log.d(TAG, "onResponse0: last_call: " + last_call);
                            Log.d(TAG, "onResponse0: user_id: " + user_id);
                            Log.d(TAG, "onResponse0: duration: " + duration);
                            Log.d(TAG, "onResponse0: firstname: " + firstname);
                            Log.d(TAG, "onResponse0: lastname: " + lastname);
                            Log.d(TAG, "onResponse0: role_id: " + role_id);
                            Log.d(TAG, "onResponse0: name: " + name);

//                            Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                            int width = display.getWidth();

                            LinearLayout llParentHorizontal = new LinearLayout(mContext);
                            llParentHorizontal.setFocusable(true);
                            llParentHorizontal.setFocusableInTouchMode(true);
                            llParentHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                            llParentHorizontal.setWeightSum(10);

                            LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParamsRow.setMargins(8, 8, 8, 8);
                            llParentHorizontal.setLayoutParams(layoutParamsRow);

                            TextView tvCallerHistoryName = new TextView(mContext);
                            tvCallerHistoryName.setPadding(0, 0, 0, 0);
                            tvCallerHistoryName.setMaxLines(3);
                            tvCallerHistoryName.setGravity(Gravity.LEFT);
                            tvCallerHistoryName.setTextSize(14);
                            tvCallerHistoryName.setTypeface(tvCallerHistoryName.getTypeface(), Typeface.BOLD);
                            if (!user_id.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName.setText("Last contacted " + firstname + " " + lastname);
                            } else {
                                tvCallerHistoryName.setText("Last contacted with me");
                            }

                            TextView tvCallerHistoryLastCallTimeAgo = new TextView(mContext);
                            tvCallerHistoryLastCallTimeAgo.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), mContext) + ")");
                            tvCallerHistoryLastCallTimeAgo.setPadding(0, 0, 0, 0);
                            tvCallerHistoryLastCallTimeAgo.setGravity(Gravity.LEFT);
                            tvCallerHistoryLastCallTimeAgo.setTextSize(10);

                            TextView tvCallerHistoryLastCallDateTime = new TextView(mContext);
                            tvCallerHistoryLastCallDateTime.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallDateTime.setPadding(0, 0, 0, 0);
                            tvCallerHistoryLastCallDateTime.setGravity(Gravity.RIGHT);
                            tvCallerHistoryLastCallDateTime.setTextSize(14);

                            LinearLayout l1ChildLeft = new LinearLayout(mContext);
                            l1ChildLeft.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lpChildLeft = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpChildLeft.weight = 7;
                            l1ChildLeft.setLayoutParams(lpChildLeft);
                            l1ChildLeft.addView(tvCallerHistoryName);
                            l1ChildLeft.addView(tvCallerHistoryLastCallTimeAgo);

                            LinearLayout llChildRight = new LinearLayout(mContext);
                            llChildRight.setOrientation(LinearLayout.HORIZONTAL);
                            llChildRight.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams lpChildRight = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpChildRight.weight = 3;
                            lpChildRight.gravity = Gravity.RIGHT;
                            llChildRight.setLayoutParams(lpChildRight);
                            llChildRight.addView(tvCallerHistoryLastCallDateTime);

                            llParentHorizontal.addView(l1ChildLeft);
                            llParentHorizontal.addView(llChildRight);

                            llDynamicConnectionsContainer.addView(llParentHorizontal);
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
                if (!NetworkAccess.isNetworkAvailable(mContext)) {
                    tvError.setText("Internet is required to view connections");

                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 404) {
                                tvError.setText("Connections not found");
                            } else {
                                tvError.setText("Error Loading");
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
