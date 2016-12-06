package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSNote extends SugarRecord {
    LSContact contactOfNote;
    //    private int id;
    private String NoteText;
    private String createdAt;

    public LSNote() {
    }

    public static List<LSNote> getNotesByContactId(Long id) {
        return LSNote.find(LSNote.class, "contact_of_note = ? ", id + "");
    }

    public String getNoteText() {
        return NoteText;
    }

    public void setNoteText(String noteText) {
        NoteText = noteText;
    }

    public LSContact getContactOfNote() {
        return contactOfNote;
    }

    public void setContactOfNote(LSContact contactOfNote) {
        this.contactOfNote = contactOfNote;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}