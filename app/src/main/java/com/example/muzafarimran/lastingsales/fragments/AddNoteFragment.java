package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FollowupsActivity;
import com.example.muzafarimran.lastingsales.activities.LSContactChooserActivity;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
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
 * Use the {@link AddNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNoteFragment extends Fragment {


    public static final String TAG = "AddNoteFragment";
    public static final int CONTACT_REQUEST_CODE = 99;

    public static ArrayList<Note> allNotes;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView tvContactName;
    EditText etContactNote;
    Button bOk, bCancel;
    LSContact oneContact;


    public AddNoteFragment() {
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
    public static AddNoteFragment newInstance(String param1, String param2) {
        AddNoteFragment fragment = new AddNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        tvContactName = (TextView) view.findViewById(R.id.contact_name_add_note);
        etContactNote = (EditText) view.findViewById(R.id.contact_note_add_note);
        bOk = (Button) view.findViewById(R.id.ok_add_note);
        bCancel = (Button) view.findViewById(R.id.cancel_add_note);

        setUpListners();
        return view;
    }

    private void setUpListners() {
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oneContact != null) {
                    LSNote note = new LSNote();
                    note.setContactOfNote(oneContact);
                    note.setNoteText(etContactNote.getText().toString());

                    note.save();
//                    getAllNotes().add(note);

//                    sending back result ok so previous activity / fragment can update its list

                    Intent data = new Intent();
                    String text = note.getId() + "";
//                    data.setData(Uri.parse(text));
                    data.setData(Uri.parse(text));
                    getActivity().setResult(RESULT_OK, data);


                    NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();

                    TinyBus bus = TinyBus.from(getActivity().getApplicationContext());
                    bus.register(mNoteAdded);
                    bus.post(mNoteAdded);
                    Log.d("AddNoteEventGenerated", "onNoteAdded() called  ");


                    getActivity().finish();

                }

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();

            }
        });

    }

    private static ArrayList<Note> getAllNotes() {

        if (allNotes == null) {
            allNotes = new ArrayList<>();
            allNotes.add(new Note("Do not call him in early morning", 0));
            allNotes.add(new Note("Ask about his savings", 1));
            allNotes.add(new Note("Call him before noon", 2));
            allNotes.add(new Note("Call and ask about his ill child as well", 3));
        }
        return allNotes;
    }


    public void setContact(String returnedResult) {
        if (returnedResult != null) {
            oneContact = LSContactChooserActivity.getAllContacts().get(Integer.parseInt(returnedResult));

            tvContactName.setText(oneContact.getContactName());
        }
    }



    /*

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == AddNoteFragment.CONTACT_REQUEST_CODE)
        {
            if (data == null)
            {
                getActivity().finish();
            }
            if (resultCode == RESULT_OK)
            {
                String returnedResult = data.getData().toString();
                setContact(returnedResult);
            }
        }
    }

*/

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