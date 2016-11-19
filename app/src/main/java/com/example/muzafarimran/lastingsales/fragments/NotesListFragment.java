package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddNoteActivity;
import com.example.muzafarimran.lastingsales.activities.LSContactChooserActivity;
import com.example.muzafarimran.lastingsales.activities.NotesActivity;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.Note;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesListFragment extends Fragment {

    public static final String TAG = "NotesListFragment";


    public static ArrayList<LSNote> allNotes;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lvAllNotes;
    NotesListAdapter notesAdapter;
    FloatingActionButton floatingActionButton;

    private Bus mBus;
    private TinyBus bus;


    public NotesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesListFragment newInstance(String param1, String param2) {
        NotesListFragment fragment = new NotesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setList(List<LSNote> allNotes) {
        if (notesAdapter != null) {
            notesAdapter.setList(allNotes);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        mBus.register(this);

        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
//        mBus.unregister(this);
        bus.unregister(this);
        Log.d(TAG, "onStop() called");

        super.onStop();

    }

    @Subscribe
    public void onNoteAddedEventModel(NoteAddedEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");

        List<LSNote> allNotes = LSNote.listAll(LSNote.class);
        setList(allNotes);

        TinyBus.from(getActivity().getApplicationContext()).unregister(event);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setRetainInstance(true);
//        startActivityForResult(new Intent(getContext(), LSContactChooserActivity.class),CONTACT_REQUEST_CODE);
    }
    @Override
    public void onResume() {
        super.onResume();
        List<LSNote> outgoingCalls = (ArrayList<LSNote>) LSNote.listAll(LSNote.class);
        setList(outgoingCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        lvAllNotes = (ListView) view.findViewById(R.id.lvAllNotesNotesFragment);


        allNotes = (ArrayList<LSNote>) LSNote.listAll(LSNote.class);

        notesAdapter = new NotesListAdapter(getActivity(), R.layout.note_list_item, allNotes, getActivity());
        lvAllNotes.setAdapter(notesAdapter);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_add_note);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), AddNoteActivity.class), NotesActivity.ADD_NOTE_REQUEST_CODE);
            }
        });

        return view;
    }




 /*   public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == AddNoteFragment.CONTACT_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {

                notesAdapter.notifyDataSetChanged();
                notesAdapter.add();
            }
        }
    }*/

/*
  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null)
        {
            getActivity().finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
*/
}