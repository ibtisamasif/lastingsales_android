package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.MissedCallsAdapter;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class MissedCallsFragment extends SearchFragment {

    public static final String TAG = "MissedCallFragment";
    MissedCallsAdapter missedCallsAdapter;
    ListView listView = null;
    private Bus mBus;
    private TinyBus bus;

    public static MissedCallsFragment newInstance(int page, String title) {
        MissedCallsFragment fragmentFirst = new MissedCallsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSCall> missedCalls) {
        if (missedCallsAdapter != null) {
            missedCallsAdapter.setList(missedCalls);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        missedCallsAdapter = new MissedCallsAdapter(getContext());
        setHasOptionsMenu(true);
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

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
//            List<LSCall> missedCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_MISSED);
//            setList(missedCalls);
            updateList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        List<LSCall> missedCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_MISSED);
//        setList(missedCalls);
       updateList();
    }

    private void updateList() {
        List<LSCall> allMissedCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_MISSED);
        List<LSCall> unhandledMissedCalls = new ArrayList<>();
        for (LSCall oneMissedCall : allMissedCalls) {
            if (oneMissedCall.getInquiryHandledState() == LSCall.INQUIRY_NOT_HANDLED) {
                unhandledMissedCalls.add(oneMissedCall);
            }
        }
//        List<LSCall> missedCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_MISSED);
        ArrayList<LSCall> uniqueCalls = new ArrayList<>();
        if (unhandledMissedCalls.size() > 0) {
            for(int counter1=0; counter1<unhandledMissedCalls.size(); counter1++) {
                LSCall oneCall = unhandledMissedCalls.get(counter1);
                if (isNumberInUniqueCalls(uniqueCalls, oneCall)) {
                    LSCall uniqueCallObject = getUniqueCallObject(unhandledMissedCalls, oneCall);
                    if (uniqueCallObject != null) {
                        uniqueCallObject.setCountOfInquiries(uniqueCallObject.getCountOfInquiries()+1);
                        Log.d(TAG, "updateList: "+uniqueCallObject.getCountOfInquiries());
                    }
                } else {
                    Log.d(TAG, "updateList: Unique");
                    oneCall.setCountOfInquiries(1);
                    uniqueCalls.add(oneCall);
                }
            }
        }

        setList(uniqueCalls);
    }

    private LSCall getUniqueCallObject(List<LSCall> unhandledMissedCalls, LSCall oneCall) {
        for (LSCall callFromList : unhandledMissedCalls) {
            if (callFromList.getContact() != null) {
                if (oneCall.getContact() != null) {
                    if (callFromList.getContact().getPhoneOne().equals(oneCall.getContact().getPhoneOne())) {
                        return callFromList;
                    }
                } else if (oneCall.getContact() == null) {
                    if (callFromList.getContact().getPhoneOne().equals(oneCall.getContactNumber())) {
                        return callFromList;
                    }
                }
            } else if (callFromList.getContact() == null) {
                if (oneCall.getContact() != null) {
                    if (callFromList.getContactNumber().equals(oneCall.getContact().getPhoneOne())) {
                        return callFromList;
                    }
                } else if (oneCall.getContact() == null) {
                    callFromList.getContactNumber().equals(oneCall.getContactNumber());
                    return callFromList;
                }
            }
        }
        return null;
    }

    private boolean isNumberInUniqueCalls(ArrayList<LSCall> uniqueCalls, LSCall oneCall) {
        for (LSCall callFromList : uniqueCalls) {
            if (callFromList.getContact() != null) {
                if (oneCall.getContact() != null) {
                    if (callFromList.getContact().getPhoneOne().equals(oneCall.getContact().getPhoneOne())) {
                        Log.d(TAG, "isNumberInUniqueCalls: 1");
                        return true;
                    }
                } else if (oneCall.getContact() == null) {
                    if (callFromList.getContact().getPhoneOne().equals(oneCall.getContactNumber())) {
                        Log.d(TAG, "isNumberInUniqueCalls: 2");
                        return true;
                    }
                }
            } else if (callFromList.getContact() == null) {
                if (oneCall.getContact() != null) {
                    if (callFromList.getContactNumber().equals(oneCall.getContact().getPhoneOne())) {
                        Log.d(TAG, "isNumberInUniqueCalls: 3");
                        return true;
                    }
                } else if (oneCall.getContact() == null) {
                    if (callFromList.getContactNumber().equals(oneCall.getContactNumber())) {
                        Log.d(TAG, "isNumberInUniqueCalls: 4");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if(materialSearchView!=null) {
            materialSearchView.setMenuItem(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(missedCallsAdapter);
        return view;
    }

    @Override
    protected void onSearch(String query) {
        missedCallsAdapter.getFilter().filter(query);
    }
}