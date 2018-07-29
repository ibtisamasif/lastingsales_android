package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.DealDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.example.muzafarimran.lastingsales.utilscallprocessing.DeleteManager;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by ibtisam on 2/1/2018.
 */

public class ViewHolderDealCard extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderTaskCard";

    private final ConstraintLayout cl;
    private final SimpleDraweeView user_avatar;
    private final TextView tvContactName;
    private final TextView tvContactNumber;
    private final SimpleDraweeView organization_avatar;
    private final TextView tvOrganizationName;
    private final TextView tvOrganizationNumber;
    private final TextView tvDealName;
//    private final ImageView ivTick;

    public ViewHolderDealCard(View v) {
        super(v);

        cl = v.findViewById(R.id.cl);
        user_avatar = v.findViewById(R.id.user_avatar);
        tvContactName = v.findViewById(R.id.tvContactName);
        tvContactNumber = v.findViewById(R.id.tvNumber);
        organization_avatar = v.findViewById(R.id.organization_avatar);
        tvOrganizationName = v.findViewById(R.id.tvOrganizationName);
        tvOrganizationNumber = v.findViewById(R.id.tvOrganizationNumber);
        tvDealName = v.findViewById(R.id.tvDealName);
//        ivTick = v.findViewById(R.id.ivTick);

    }

    public void bind(Object item, int position, Context mContext) {
        LSDeal lsDeal = (LSDeal) item;
        LSContact contact = lsDeal.getContact();
        if (contact != null) {
            user_avatar.setVisibility(View.VISIBLE);
            tvContactName.setVisibility(View.VISIBLE);
            tvContactNumber.setVisibility(View.VISIBLE);
            if (contact.getContactName() != null) {
                tvContactName.setText(contact.getContactName());
            } else {
                tvContactName.setText("");
            }
            if (contact.getPhoneOne() != null) {
                tvContactNumber.setText(contact.getPhoneOne());
            } else {
                tvContactNumber.setText("");
            }
            if (contact.getContactProfile() != null) {
                MyDateTimeStamp.setFrescoImage(user_avatar, contact.getContactProfile().getSocial_image());
            }
        } else {
            user_avatar.setVisibility(View.GONE);
            tvContactName.setVisibility(View.GONE);
            tvContactName.setVisibility(View.GONE);
//            Log.i(TAG, "bind: lscontact: is NULL");
//            tvContactName.setText("UNKNOWN");
//            tvContactNumber.setText("number");
        }
        LSOrganization organization = lsDeal.getOrganization();
        if (organization != null) {
            organization_avatar.setVisibility(View.VISIBLE);
            tvOrganizationName.setVisibility(View.VISIBLE);
            tvOrganizationNumber.setVisibility(View.VISIBLE);
            if (organization.getName() != null) {
                tvOrganizationName.setText(organization.getName());
            } else {
                tvOrganizationName.setText("");
            }
            if (organization.getPhone() != null) {
                tvOrganizationNumber.setText(organization.getPhone());
            } else {
                tvOrganizationNumber.setText("");
            }
//            if(organization.getContactProfile()!=null){
//                MyDateTimeStamp.setFrescoImage(user_avatar, organization.getContactProfile().getSocial_image());
//            }
        } else {
            organization_avatar.setVisibility(View.GONE);
            tvOrganizationName.setVisibility(View.GONE);
            tvOrganizationNumber.setVisibility(View.GONE);
//            tvContactName.setText("UNKNOWN");
//            tvContactNumber.setText("number");
        }
        if (lsDeal.getName() != null) {
            tvDealName.setText(lsDeal.getName());
//            if (lsDeal.getIsPrivate() != null && lsDeal.getIsPrivate().equalsIgnoreCase("1")) {
//                tvDealName.setText(lsDeal.getName() + " (Private)");
//            } else if (lsDeal.getIsPrivate() != null && lsDeal.getIsPrivate().equalsIgnoreCase("0")) {
//                tvDealName.setText(lsDeal.getName() + " (Public)");
//            }
        }

        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lsDeal != null) {
                    Intent detailsActivityIntent = new Intent(mContext, DealDetailsTabActivity.class);
                    detailsActivityIntent.putExtra(DealDetailsTabActivity.KEY_DEAL_ID, lsDeal.getId() + "");
                    mContext.startActivity(detailsActivityIntent);
                } else {
                    DeleteManager.deleteDeal(mContext, lsDeal);
//                    lsDeal.delete();
                }
            }
        });

//        ivTick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
//
////                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
////                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
////                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
////                alertDialogBuilderUserInput.setView(mView);
////                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
////                alertDialogBuilderUserInput
////                        .setCancelable(false)
////                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialogBox, int id) {
////                                lsTask.setStatus("Done");
////                                String remarksText = userInputDialogEditText.getText().toString();
////                                if (remarksText != null && !remarksText.equalsIgnoreCase(""))
////                                    lsTask.setRemarks(remarksText);
//////                                editTaskToServer(mContext, lsTask);
////                                Toast.makeText(mContext, "Syncing task", Toast.LENGTH_SHORT).show();
////                            }
////                        })
////                        .setNegativeButton("Cancel",
////                                new DialogInterface.OnClickListener() {
////                                    public void onClick(DialogInterface dialogBox, int id) {
////                                        dialogBox.cancel();
////                                    }
////                                });
////                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
////                alertDialogAndroid.show();
//            }
//        });
    }

//    private void editTaskToServer(final Context mContext, final LSTask lsTask) {
//
//        SessionManager sessionManager = new SessionManager(mContext);
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.UPDATE_TASK;
//        Uri builtUri;
//        builtUri = Uri.parse(BASE_URL)
//                .buildUpon()
//                .appendPath("" + lsTask.getLeadId())
//                .appendPath("task")
//                .appendPath("" + lsTask.getServerId())
////                .appendQueryParameter("status", "done")
//                .appendQueryParameter("status", lsTask.getStatus())
//                .appendQueryParameter("remarks", "" + lsTask.getRemarks())
//                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                .build();
//        final String myUrl = builtUri.toString();
//        Log.d(TAG, "editTaskToServer: URL: " + myUrl);
//        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////                Log.d(TAG, "onResponse() editTaskToServer: response = [" + response + "]");
////                try {
////                    JSONObject jObj = new JSONObject(response);
////                    int responseCode = jObj.getInt("responseCode");
////                    if (responseCode == 200) {
////                        JSONObject responseObject = jObj.getJSONObject("response");
////                        Log.d(TAG, "onResponse : LastSeenInLocal : " + sessionManager.getLastAppVisit());
////                        Log.d(TAG, "onResponse : LastSeenFromServer : " + responseObject.getString("last_seen"));
//                lsTask.delete();
////                    }
////                } catch (JSONException e) {
////                    FirebaseCrash.report(e);
////                }
//                TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
//                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                bus.post(mCallEvent);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onErrorResponse:editTaskToServer CouldNotUpdateTaskToServer");
//                try {
//                    if (error.networkResponse != null) {
//                        Log.e(TAG, "onErrorResponse: statusCode: " + error.networkResponse.statusCode);
//                        if (error.networkResponse.statusCode == 410) {
//                            Log.e(TAG, "onErrorResponse: alreadyUpdatedTaskToServer");
//                            lsTask.delete();
//                        }
//                        if (error.networkResponse.statusCode == 412) {
//                            Log.e(TAG, "onErrorResponse: NoFurtherSteps");
//                            lsTask.delete();
//                        }
//                        TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
//                        TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                        bus.post(mCallEvent);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//
//
//    }
}
