package com.example.muzafarimran.lastingsales.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.events.ContactTaggedFromUntaggedContactEventModel;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.events.UnlabeledContactAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {
    private static final String TAG = "HomeFragment";
    private TextView tvTotalLeadsValue;
    private TextView tvInquiriesValue;
    private TextView tvInactiveLeadsValue;
    private TextView tvUnlabeledContacts;
    private CardView lltotalLeadsContainer;
    private CardView llInActiveLeadsContainer;
    private CardView llUnlabeledContainer;
    private CardView llinquriesContainer;
    //    private FrameLayout followupsListHolderFrameLayout;
    private FollowupsTodayListFragment followupsTodayListFragment;
    private FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    private FloatingActionMenu floatingActionMenu;
    private TinyBus bus;
    private RatingBar ratingBar1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvTotalLeadsValue = (TextView) view.findViewById(R.id.tvTotalLeadsValue);
        tvInquiriesValue = (TextView) view.findViewById(R.id.tvInquriesValue);
        tvUnlabeledContacts = (TextView) view.findViewById(R.id.tvUntaggeContactsVal);
        tvInactiveLeadsValue = (TextView) view.findViewById(R.id.tvInactiveLeadsValue);
        lltotalLeadsContainer = (CardView) view.findViewById(R.id.lltotalLeadsContainer);
        llinquriesContainer = (CardView) view.findViewById(R.id.llinquriesContainer);
        llUnlabeledContainer = (CardView) view.findViewById(R.id.llUnlabeledContactsContainer);
        llInActiveLeadsContainer = (CardView) view.findViewById(R.id.llInActiveLeadsContactsContainer);
        ratingBar1 = (RatingBar) view.findViewById(R.id.ratingBar1);
//        followupsListHolderFrameLayout = (FrameLayout) view.findViewById(R.id.followupsListHolderFrameLayout);
        updateHomeFigures();
//        lltotalLeadsContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = 2;
//                ((TabSelectedListener) getActivity()).onTabSelectedEvent(position, "AllLeads");
//            }
//        });
        llinquriesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 0;
                ((TabSelectedListener) getActivity()).onTabSelectedEvent(position, "Inquiries");
//                mViewPager.setCurrentItem(1,true);
//                Bundle bundle = new Bundle();
//                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsTodayListFragment.class.getName());
//                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Followups Today");
//                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
//                Intent intent = new Intent(getContext(), FrameActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                String projectToken = MixpanelConfig.projectToken;
//                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), projectToken);
//                try {
//                    JSONObject props = new JSONObject();
//                    props.put("Gender", "Female");
//                    props.put("Logged in", false);
//                    mixpanel.track("Inquiries(Home)", props);
//                } catch (JSONException e) {
//                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//                }
            }
        });
        llUnlabeledContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UnlabeledFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Unlabeled Leads");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
//                String projectToken = MixpanelConfig.projectToken;
//                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), projectToken);
//                try {
//                    JSONObject props = new JSONObject();
//                    props.put("Gender", "Female");
//                    props.put("Logged in", false);
//                    mixpanel.track("Unlabeled(Home)", props);
//                } catch (JSONException e) {
//                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//                }
            }
        });
        llInActiveLeadsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 2;
                ((TabSelectedListener) getActivity()).onTabSelectedEvent(position, "InActiveLeads");
//                Intent intent;
//                Bundle bundle = new Bundle();
//                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, InActiveLeadsFragment.class.getName());
//                bundle.putString(FrameActivity.ACTIVITY_TITLE, "InActive Leads");
//                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//                intent = new Intent(getContext(), FrameActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                String projectToken = MixpanelConfig.projectToken;
//                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), projectToken);
//                mixpanel.track("InActive(Home)");
            }
        });


        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        floatingActionButtonImport = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), AddEditLeadActivity.class);
                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), AddEditLeadActivity.class);
                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
                startActivity(intent);
            }
        });
//        //Bundle bundle = new Bundle();
//        //bundle.putString(NotesFragment.CONTACT_ID, selectedContact.getId().toString());
//        followupsTodayListFragment = new FollowupsTodayListFragment();
//        //followupsTodayListFragment.setArguments(bundle);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.followupsListHolderFrameLayout, followupsTodayListFragment);
//        transaction.commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHomeFigures();
        setHasOptionsMenu(false);
    }

    private void updateHomeFigures() {
        new UpdateHomeFigureAsync().execute();
    }

    private class UpdateHomeFigureAsync extends AsyncTask<Void, String, Void> {
        //        ProgressDialog progressDialog;
        List<LSContact> allLeads;
        ArrayList<LSContact> allUnlabeledContacts;
        ArrayList<LSContact> allInactiveLeads;
        List<LSInquiry> allInquiries;

        UpdateHomeFigureAsync() {
            super();
//            progressDialog = new ProgressDialog(NavigationDrawerActivity.this);
//            progressDialog.setTitle("Loading data");
//            //this method will be running on UI thread
//            progressDialog.setMessage("Please Wait...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            allLeads = LSContact.getDateArrangedSalesContacts();
            allUnlabeledContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_UNLABELED);
            allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();
            allInquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//            SystemClock.sleep(200);
            return (null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(String... item) {
            Log.d(TAG, "onProgressUpdate: " + item);
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(getContext(), "onPostExecuteWon", Toast.LENGTH_SHORT).show();
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
            if (allLeads != null) {
                if (allLeads.size() > 0) {
                    lltotalLeadsContainer.setVisibility(View.VISIBLE);
                    tvTotalLeadsValue.setText("" + allLeads.size());
                } else {
                    lltotalLeadsContainer.setVisibility(View.GONE);
                }
            } else {
                tvTotalLeadsValue.setText(0);
            }

            if (allInquiries != null) {
                if (allInquiries.size() > 0) {
                    llinquriesContainer.setVisibility(View.VISIBLE);
                    tvInquiriesValue.setText("" + allInquiries.size());
                } else {
                    llinquriesContainer.setVisibility(View.GONE);
                }
            } else {
                tvInquiriesValue.setText(0);
            }

            if (allUnlabeledContacts != null) {
                if (allUnlabeledContacts.size() > 0) {
                    llUnlabeledContainer.setVisibility(View.VISIBLE);
                    tvUnlabeledContacts.setText("" + allUnlabeledContacts.size());
                } else {
                    llUnlabeledContainer.setVisibility(View.GONE);
                }
            } else {
                tvUnlabeledContacts.setText(0);
            }
            if (allInactiveLeads != null) {
                if (allInactiveLeads.size() > 0) {
                    llInActiveLeadsContainer.setVisibility(View.VISIBLE);
                    tvInactiveLeadsValue.setText("" + allInactiveLeads.size());
                } else {
                    llInActiveLeadsContainer.setVisibility(View.GONE);
                }
            } else {
                tvInactiveLeadsValue.setText(0);
            }
        }
    }


    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            updateHomeFigures();
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            updateHomeFigures();
        }
    }

    @Subscribe
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onOutgoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            updateHomeFigures();
        }
    }

    @Subscribe
    public void onContactTaggedFromUntaggedContactEventModel(ContactTaggedFromUntaggedContactEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");
        updateHomeFigures();
    }

    @Subscribe
    public void onUnlabeledContactAddedEventModel(UnlabeledContactAddedEventModel event) {
        Log.d(TAG, "onUnlabeledContactAddedEventModel() called with: event = [" + event + "]");
        updateHomeFigures();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "100");
        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });
        sequence.setConfig(config);
        sequence.addSequenceItem(floatingActionMenu, "You can add leads here", "GOT IT");
        if (llinquriesContainer.isShown()) {
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(llinquriesContainer)
                            .setDismissText("GOT IT")
                            .setContentText("These are your inquiries which you need to call back")
                            .withRectangleShape(true)
                            .build()
            );
        }
        if (llUnlabeledContainer.isShown()) {
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(llUnlabeledContainer)
                            .setDismissText("GOT IT")
                            .setContentText("here are your unlabeled contacts please save them")
                            .withRectangleShape()
                            .build()
            );
        }
        sequence.start();
    }
}