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
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
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

public class NotesInDealDetailsFragment extends TabFragment {
    public static final String TAG = "NotesInContactDetailsFr";
    public static final String DEAL_ID = "deal_id";
    LSDeal selectedDeal;
    private TinyBus bus;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private Long dealIDLong;

    public static NotesInDealDetailsFragment newInstance(int page, String title, Long id) {
        NotesInDealDetailsFragment fragmentFirst = new NotesInDealDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong(NotesInDealDetailsFragment.DEAL_ID, id);
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
        dealIDLong = bundle.getLong(NotesInDealDetailsFragment.DEAL_ID);
        selectedDeal = LSDeal.findById(LSDeal.class, dealIDLong);

        SeparatorItem separatorNotes = new SeparatorItem();
        separatorNotes.text = "Notes";
        list.add(separatorNotes);

        Collection<LSNote> allNotesOfThisDeal = Select.from(LSNote.class).where(Condition.prop("deal_of_note").eq(selectedDeal.getId())).orderBy("id DESC").list();
        if (!allNotesOfThisDeal.isEmpty()) {
            list.addAll(allNotesOfThisDeal);
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