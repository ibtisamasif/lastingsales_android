package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.TaskAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSTask;
import com.example.muzafarimran.lastingsales.sync.MyURLs;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 2/1/2018.
 */

public class ViewHolderTaskCard extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderTaskCard";

    private final TextView tvContactName;
    private final TextView tvContactNumber;
    private final TextView tvTaskName;
    private final TextView tvTaskDescription;
    private final ImageView ivTick;

    public ViewHolderTaskCard(View v) {
        super(v);

        tvContactName = v.findViewById(R.id.tvContactName);
        tvContactNumber = v.findViewById(R.id.tvContactNumber);
        tvTaskName = v.findViewById(R.id.tvTaskName);
        tvTaskDescription = v.findViewById(R.id.tvTaskDescription);
        ivTick = v.findViewById(R.id.ivTick);

    }

    public void bind(Object item, int position, Context mContext) {
        LSTask lsTask = (LSTask) item;

        LSContact lsContact = LSContact.getContactFromServerId(lsTask.getLeadId());


        if (lsContact != null) {
//            Log.i(TAG, "bind: lscontact: " + lsContact.toString());
            if (lsContact.getContactName() != null)
                tvContactName.setText(lsContact.getContactName());
            if (lsContact.getPhoneOne() != null) {
                tvContactNumber.setText(lsContact.getPhoneOne());
            }
        } else {
            Log.i(TAG, "bind: lscontact: is NULL");
        }


        if (lsTask.getName() != null) {
            tvTaskName.setText(lsTask.getName());
        }
        if (lsTask.getName() != null) {
            tvTaskDescription.setText(lsTask.getDescription());
        }

        ivTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                lsTask.setStatus("Done");
                                String remarksText = userInputDialogEditText.getText().toString();
                                if (remarksText != null && !remarksText.equalsIgnoreCase(""))
                                    lsTask.setRemarks(remarksText);
                                editTaskToServer(mContext, lsTask);
                                Toast.makeText(mContext, "Syncing task", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
    }

    private void editTaskToServer(final Context mContext, final LSTask lsTask) {

        SessionManager sessionManager = new SessionManager(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.UPDATE_TASK;
        Uri builtUri;
        builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + lsTask.getLeadId())
                .appendPath("task")
                .appendPath("" + lsTask.getServerId())
//                .appendQueryParameter("status", "done")
                .appendQueryParameter("status", lsTask.getStatus())
                .appendQueryParameter("remarks", "" + lsTask.getRemarks())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "editTaskToServer: URL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "onResponse() editTaskToServer: response = [" + response + "]");
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        Log.d(TAG, "onResponse : LastSeenInLocal : " + sessionManager.getLastAppVisit());
//                        Log.d(TAG, "onResponse : LastSeenFromServer : " + responseObject.getString("last_seen"));
                lsTask.delete();
//                    }
//                } catch (JSONException e) {
//                    FirebaseCrash.report(e);
//                }
                TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                bus.post(mCallEvent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse:editTaskToServer CouldNotUpdateTaskToServer");
                try {
                    if (error.networkResponse != null) {
                        Log.e(TAG, "onErrorResponse: statusCode: " + error.networkResponse.statusCode);
                        if (error.networkResponse.statusCode == 410) {
                            Log.e(TAG, "onErrorResponse: alreadyUpdatedTaskToServer");
                            lsTask.delete();
                        }
                        if (error.networkResponse.statusCode == 412) {
                            Log.e(TAG, "onErrorResponse: NoFurtherSteps");
                            lsTask.delete();
                        }
                        TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
                        TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                        bus.post(mCallEvent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);


    }
}
