package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.TaskAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSTask;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/26/2017.
 */

public class AgentTasksFetchAsync extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "AgentDataFetchAsync";
    private SessionManager sessionManager;
    private Context mContext;
    private static RequestQueue queue;

    public AgentTasksFetchAsync(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
    }

    @Override
    protected Void doInBackground(Object... objects) {
        if (NetworkAccess.isNetworkAvailable(mContext)) {
            fetchTasks();
        }
        return null;
    }

    private void fetchTasks() {
        Log.d(TAG, "fetchTasks: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_TASK;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getTasks: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalTasks = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalTasks: " + totalTasks);

                    JSONArray jsonarray = responseObject.getJSONArray("data");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        Log.d(TAG, "INDEX " + i);
                        JSONObject dataObject = jsonarray.getJSONObject(i);

                        String id = dataObject.getString("id");
                        String user_id = dataObject.getString("user_id");
                        String company_id = dataObject.getString("company_id");
                        String workflow_id = dataObject.getString("workflow_id");
                        String step_id = dataObject.getString("step_id");
                        String lead_id = dataObject.getString("lead_id");
                        String name = dataObject.getString("name");
                        String description = dataObject.getString("description");
//                    String type = dataObject.getString("type");
                        String status = dataObject.getString("status");
                        String created_by = dataObject.getString("created_by");
                        String created_at = dataObject.getString("created_at");
//                    String updated_at = dataObject.getString("updated_at");
//                    String assigned_at = dataObject.getString("assigned_at");
//                    String completed_at = dataObject.getString("completed_at");
//                    String remarks = dataObject.getString("remarks");

                        //lead object needs not to be parsed for now.
//                        JSONObject leadObject = dataObject.getJSONObject("lead");
//                        String lead_id_lead_obj = leadObject.getString("id");
//                        String lead_name_lead_obj = leadObject.getString("name");

                        // ignore if task already exists
                        LSTask lsTask = LSTask.getTaskFromServerId(id);
                        if (lsTask == null) {
                            Log.d(TAG, "onResponse: task doesn't already exit in db");
                            // check if lead still exists of which the task is
                            LSContact lsContact = LSContact.getContactFromServerId(lead_id);
                            if (lsContact != null) {
                                Log.d(TAG, "onResponse: contact of task still exists");
                                LSTask newTask = new LSTask();
                                newTask.setServerId(id);
                                newTask.setUserId(user_id);
                                newTask.setCompanyId(company_id);
                                newTask.setWorkflowId(workflow_id);
                                newTask.setStepId(step_id);
                                newTask.setLeadId(lead_id);
                                newTask.setName(name);
                                newTask.setDescription(description);
//                    newTask.setType(type);
                                newTask.setStatus(status);
                                newTask.setCreatedBy(created_by);
                                newTask.setCreatedAt(created_at);
//                    newTask.setUpdatedAt(Long.parseLong(updated_at));
//                    newTask.setAssignedAt(assigned_at);
//                    newTask.setCompletedAt(completed_at);
//                    newTask.setRemarks(remarks);
                                newTask.save();
                            }
                        }
                    }

                    TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext);
                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotSyncGETTasks");
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
