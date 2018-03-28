package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/20/2016.
 */

public class NotesInDealDetailsFragment extends TabFragment {
    public static final String TAG = "NotesInContactDetailsFr";
    public static final String CONTACT_ID = "contact_id";
    LSContact selectedContact;
    private TinyBus bus;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private Long contactIDLong;

    public static NotesInDealDetailsFragment newInstance(int page, String title, Long id) {
        NotesInDealDetailsFragment fragmentFirst = new NotesInDealDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong(NotesInDealDetailsFragment.CONTACT_ID, id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onNoteAddedEventModel(NoteAddedEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");
        onResumeFetchFreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        onResumeFetchFreshData();
    }

    private void onResumeFetchFreshData() {
        list.clear();

        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong(NotesInDealDetailsFragment.CONTACT_ID);
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);

        SeparatorItem separatorFollowup = new SeparatorItem();
        separatorFollowup.text = "Follow Up";
        list.add(separatorFollowup);

        ArrayList<TempFollowUp> allFollowupsOfThisContact = selectedContact.getAllFollowups(); //TODO crash here when contact is deleted because of syncing.
//        ArrayList<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getAllFollowupsFromContactId(selectedContact.getId()+"");
        Calendar now = Calendar.getInstance();
        TempFollowUp selectedFollowup = null;
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                if (oneFollowup.getDateTimeForFollowup() - 30000 > now.getTimeInMillis()) { //TODO not an ethical way to fix this bug.
                    Log.d(TAG, "updateUi time difference: " + (oneFollowup.getDateTimeForFollowup() - now.getTimeInMillis()));
                    selectedFollowup = oneFollowup;
                    break;
                }
            }
        }
        if (selectedFollowup != null) {
            list.add(selectedFollowup);
        } else {
            TempFollowUp tempFollowUp = new TempFollowUp();
            tempFollowUp.setTitle("#-1");
            tempFollowUp.setContact(selectedContact);
            list.add(tempFollowUp);
        }

        SeparatorItem separatorNotes = new SeparatorItem();
        separatorNotes.text = "Notes";
        list.add(separatorNotes);

        Collection<LSNote> allNotesOfThisContact = Select.from(LSNote.class).where(Condition.prop("contact_of_note").eq(selectedContact.getId())).orderBy("id DESC").list();
        if (!allNotesOfThisContact.isEmpty()) {
            list.addAll(allNotesOfThisContact);
            adapter.notifyDataSetChanged();
        } else {
            ErrorItem errorItem = new ErrorItem();
            errorItem.message = "Nothing in notes";
            errorItem.drawable = R.drawable.ic_notes_empty_xxxhdpi;
            list.add(errorItem);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_recycler, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyRecyclerViewAdapter(getActivity(), list);

        mRecyclerView.setAdapter(adapter);

        return view;
    }
}