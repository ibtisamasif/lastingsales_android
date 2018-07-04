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
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/20/2016.
 */

public class NotesInOrganizationDetailsFragment extends TabFragment {
    public static final String TAG = "NotesInOrganizationDeta";
    public static final String ORGANIZATION_ID = "organization_id";
    private static Bundle args;
    LSOrganization selectedOrganization;
    private Long organizationIDLong;
    private TinyBus bus;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();

    public static NotesInOrganizationDetailsFragment newInstance(int page, String title, Long id) {
        NotesInOrganizationDetailsFragment fragmentFirst = new NotesInOrganizationDetailsFragment();
        args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong(NotesInOrganizationDetailsFragment.ORGANIZATION_ID, id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_recycler, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyRecyclerViewAdapter(getActivity(), list);

        mRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        onResumeFetchFreshData();
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

    private void onResumeFetchFreshData() {
        list.clear();
        if (args != null) {
            organizationIDLong = args.getLong(NotesInOrganizationDetailsFragment.ORGANIZATION_ID);
        }
        selectedOrganization = LSOrganization.findById(LSOrganization.class, organizationIDLong);

        if (selectedOrganization != null) {
            SeparatorItem separatorFollowup = new SeparatorItem();
            separatorFollowup.text = "Follow Up";
            list.add(separatorFollowup);

            SeparatorItem separatorNotes = new SeparatorItem();
            separatorNotes.text = "Notes";
            list.add(separatorNotes);

            Collection<LSNote> allNotesOfThisContact = Select.from(LSNote.class).where(Condition.prop("organization_of_note").eq(selectedOrganization.getId())).orderBy("id DESC").list();
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
    }
}