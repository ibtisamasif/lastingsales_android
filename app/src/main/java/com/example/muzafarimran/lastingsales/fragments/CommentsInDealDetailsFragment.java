package com.example.muzafarimran.lastingsales.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.muzafarimran.lastingsales.carditems.CommentItem;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.TryAgainItem;
import com.example.muzafarimran.lastingsales.events.CommentEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.sync.MyURLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/20/2016.
 */

public class CommentsInDealDetailsFragment extends TabFragment {
    public static final String TAG = "CommentsInDealDetailsFr";
    public static final String CONTACT_ID = "contact_id";
    LSContact selectedContact;
    private TinyBus bus;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private Long contactIDLong;

    private SessionManager sessionManager;
    private Context mContext;
    private static RequestQueue queue;

    private ProgressDialog pdLoading;
    private Button buttonSend;
    private EditText chatText;

    public static CommentsInDealDetailsFragment newInstance(int page, String title, Long id) {
        Log.d(TAG, "newInstance: ");
        CommentsInDealDetailsFragment fragmentFirst = new CommentsInDealDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong(CommentsInDealDetailsFragment.CONTACT_ID, id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
        sessionManager = new SessionManager(context);
        queue = Volley.newRequestQueue(context);
        pdLoading = new ProgressDialog(context);
        pdLoading.setTitle("Loading data");
        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_comments_screen_in_lead, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MyRecyclerViewAdapter(mContext, list);

        mRecyclerView.setAdapter(adapter);

        chatText = (EditText) view.findViewById(R.id.chatText);

        buttonSend = (Button) view.findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdLoading != null) {
                    pdLoading.show();
                }
                String comment = chatText.getText().toString();
                addCommentToServer(comment);
                chatText.setText("");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(mContext.getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            contactIDLong = bundle.getLong(CommentsInDealDetailsFragment.CONTACT_ID);
        }
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        onResumeFetchFreshData();
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (pdLoading != null && pdLoading.isShowing()) {
            pdLoading.dismiss();
        }
        super.onDestroy();
    }

    @Subscribe
    public void onCommentEventModel(CommentEventModel event) {
        Log.d(TAG, "onCommentEventModel() called with: event = [" + event + "]");
        onResumeFetchFreshData();
    }

    private void onResumeFetchFreshData() {
        list.clear();
        if (selectedContact != null) {
//            SeparatorItem separatorNotes = new SeparatorItem();
//            separatorNotes.text = "Comments";
//            list.add(separatorNotes);


            //Fetch Comments from Api now and put it in the list
            if (pdLoading != null) {
                pdLoading.show();
            }
            fetchAgentCommentsFunc(selectedContact);


//            Collection<LSNote> allCommentsOfThisContact = Select.from(LSNote.class).where(Condition.prop("contact_of_note").eq(selectedContact.getId())).orderBy("id DESC").list();


//            if (!allCommentsOfThisContact.isEmpty()) {
//
//                list.addAll(allCommentsOfThisContact);
//                adapter.notifyDataSetChanged();
//
//            } else {
//
//                ErrorItem errorItem = new ErrorItem();
//                errorItem.message = "Nothing in comments";
//                errorItem.drawable = R.drawable.ic_notes_empty_xxxhdpi;
//                list.add(errorItem);
//                adapter.notifyDataSetChanged();
//
//            }
        }
    }


    private void fetchAgentCommentsFunc(LSContact contact) {
        Log.d(TAG, "fetchAgentNotesFunc: Fetching comments...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_COMMENTS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + contact.getServerId())
                .appendPath("comment")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Collection<CommentItem> allCommentsOfThisContact = new ArrayList<CommentItem>();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse() getComments: response = [" + response + "]");
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    JSONObject jObj = new JSONObject(response);
                    JSONObject respObject = jObj.getJSONObject("response");
                    JSONArray dataArray = respObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonobject = dataArray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String lead_id = jsonobject.getString("lead_id");
                        String comment = jsonobject.getString("comment");
                        String created_at = jsonobject.getString("created_at");
                        String updated_at = jsonobject.getString("updated_at");
                        String created_by = jsonobject.getString("created_by");

                        Log.d(TAG, "onResponse: id: " + id);
                        Log.d(TAG, "onResponse: lead_id: " + lead_id);
                        Log.d(TAG, "onResponse: comment: " + comment);
                        Log.d(TAG, "onResponse: created_at: " + created_at);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);

                        boolean left = false;
                        if (!sessionManager.getKeyLoginId().equals(created_by)) {
                            left = true;
                        }
                        CommentItem tempComment = new CommentItem(comment, created_at, updated_at, created_by, left);

                        allCommentsOfThisContact.add(tempComment);
                    }
                    if (!allCommentsOfThisContact.isEmpty()) {

                        list.addAll(allCommentsOfThisContact);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(list.size() - 1);
                    } else {

                        ErrorItem errorItem = new ErrorItem();
                        errorItem.message = "Nothing in comments";
                        errorItem.drawable = R.drawable.ic_notes_empty_xxxhdpi;
                        list.add(errorItem);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    Log.e(TAG, "onResponse: JSONException Comments");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETComments");
                if (pdLoading != null && pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    ErrorItem errorItem = new ErrorItem();
                    errorItem.message = "No comments found";
                    errorItem.drawable = R.drawable.ic_notes_empty_xxxhdpi;
                    list.add(errorItem);
                    adapter.notifyDataSetChanged();
                } else {
                    TryAgainItem tryAgainItem = new TryAgainItem();
                    tryAgainItem.message = "Check internet connectivity";
                    tryAgainItem.drawable = R.drawable.ic_notes_empty_xxxhdpi;
                    list.add(tryAgainItem);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addCommentToServer(final String comment) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_COMMENT + selectedContact.getServerId() + "/comment", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() addComment: response = [" + response + "]");
                if (pdLoading != null && pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                TinyBus.from(mContext.getApplicationContext()).post(new CommentEventModel());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddComment");
                try {
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            if (error.networkResponse.statusCode == 409) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 409) {
                                    JSONObject responseObject = jObj.getJSONObject("response");
//                                    deal.setServerId(responseObject.getString("id"));
//                                    deal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
//                                    deal.save();
                                    TinyBus.from(mContext.getApplicationContext()).post(new CommentEventModel());
                                }
                            }
                        }
                        Toast.makeText(mContext, "Check internet connectivity", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("comment", comment);
                params.put("api_token", "" + sessionManager.getLoginToken());
                Log.d(TAG, "getParams: addCommentToServerSync " + params);
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