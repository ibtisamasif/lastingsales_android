package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSNote extends SugarRecord
{
//    private int id;
    private String NoteText;
    LSContact contactOfNote;
    private String createdAt;

    public LSNote()
    {
    }

    public String getNoteText()
    {
        return NoteText;
    }

    public void setNoteText(String noteText)
    {
        NoteText = noteText;
    }

    public LSContact getContactOfNote()
    {
        return contactOfNote;
    }

    public void setContactOfNote(LSContact contactOfNote)
    {
        this.contactOfNote = contactOfNote;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }
}
