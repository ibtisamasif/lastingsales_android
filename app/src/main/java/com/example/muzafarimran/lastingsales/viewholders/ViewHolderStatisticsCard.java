package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.example.muzafarimran.lastingsales.carditems.StatisticsItem;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.MyURLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderStatisticsCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderStatisticsCar";
    private final TextView tvArtValue;
    private final TextView tvLeadsValue;
    private final TextView tvInquiriesValue;
    private final TextView tvCallsValue;
    private SessionManager sessionManager;
    private RequestQueue queue;

    public ViewHolderStatisticsCard(View v) {
        super(v);

        tvArtValue = v.findViewById(R.id.tvArtValue);
        tvLeadsValue = v.findViewById(R.id.tvLeadsValue);
        tvInquiriesValue = v.findViewById(R.id.tvInquiriesValue);
        tvCallsValue = v.findViewById(R.id.tvCallsValue);

    }

    public void bind(Object item, int position, Context mContext) {
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        final StatisticsItem statisticsItem = (StatisticsItem) item;
        tvArtValue.setText(MessageFormat.format("{0}", statisticsItem.artValue));
        tvLeadsValue.setText(MessageFormat.format("{0}", statisticsItem.leadsValue));
        tvInquiriesValue.setText(MessageFormat.format("{0}", statisticsItem.inquiriesValue));
        tvCallsValue.setText(MessageFormat.format("{0}", statisticsItem.callsValue));

//        fetchInquiries(mContext);
//        fetchAgentLeadsFunc();
//        fetchAgentCallsFunc();
    }

    private void fetchInquiries(Context mContext) {
        Log.d(TAG, "fetchInquiries: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_INQUIRY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("status", "pending")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalInquiries = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalInquiries: " + totalInquiries);

                    JSONArray jsonarray = jObj.getJSONArray("inquiries_count");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        Log.d(TAG, "INDEX " + i);
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        int total = jsonobject.getInt("total");
                        String status = jsonobject.getString("status");

                        Log.d(TAG, "onResponse: total: " + total);
                        Log.d(TAG, "onResponse: status: " + status);

                        if (jsonobject.has("status") && status.equals("pending")) {
                            tvInquiriesValue.setText(MessageFormat.format("{0}", total));
                        }

                        String average_response_time = jObj.getString("average_response_time");
                        if (jObj.has("average_response_time")) {
                            long average_response_time_long = (long) Double.parseDouble(average_response_time);
//                            average_response_time_long = average_response_time_long * 1000;
                            int day = (int) TimeUnit.SECONDS.toDays(average_response_time_long);
                            long hours = TimeUnit.SECONDS.toHours(average_response_time_long) - (day * 24);
                            long minute = TimeUnit.SECONDS.toMinutes(average_response_time_long) - (TimeUnit.SECONDS.toHours(average_response_time_long) * 60);
                            long second = TimeUnit.SECONDS.toSeconds(average_response_time_long) - (TimeUnit.SECONDS.toMinutes(average_response_time_long) * 60);
                            if (day != 0) {
                                tvArtValue.setText(day + "d " + hours + "h " + minute + "m " + second + "s");
                            } else if (hours != 0) {
                                tvArtValue.setText(hours + "h " + minute + "m " + second + "s");
                            } else if (minute != 0) {
                                tvArtValue.setText(minute + "m " + second + "s ");
                            }
                        }
                    }

                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext);
                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchInquiries Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("gVLqb2w8XEpdaQOK8wU7MpNXL9ZpZtBhiN1sbxImCuIOIiFQbMN3AHN098Ua");
                Map<String, String> params = new HashMap<String, String>();
//                params.put("api_token", "" + sessionManager.getLoginToken());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void fetchAgentLeadsFunc() {
        Log.d(TAG, "fetchAgentLeadsFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CONTACTS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("lead_type", "" + LSContact.CONTACT_TYPE_SALES)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalLeads = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalLeads: " + totalLeads);
                    tvLeadsValue.setText(MessageFormat.format("{0}", totalLeads));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchAgentLeadsFunc Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("gVLqb2w8XEpdaQOK8wU7MpNXL9ZpZtBhiN1sbxImCuIOIiFQbMN3AHN098Ua");
                Map<String, String> params = new HashMap<String, String>();
//                params.put("api_token", "" + sessionManager.getLoginToken());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }


    private void fetchAgentCallsFunc() {
        Log.d(TAG, "fetchAgentCallsFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CALL;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalLeads = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalLeads: " + totalLeads);
                    tvCallsValue.setText(MessageFormat.format("{0}", totalLeads));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchAgentCallsFunc Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("gVLqb2w8XEpdaQOK8wU7MpNXL9ZpZtBhiN1sbxImCuIOIiFQbMN3AHN098Ua");
                Map<String, String> params = new HashMap<String, String>();
//                params.put("api_token", "" + sessionManager.getLoginToken());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
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
