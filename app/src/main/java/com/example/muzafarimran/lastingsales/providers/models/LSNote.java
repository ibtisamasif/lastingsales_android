package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSNote extends SugarRecord {
    LSContact contactOfNote;
    //    private int id;
    private String NoteText;
    private String createdAt;
    private String syncStatus;
    private String serverId;

    public LSNote() {
    }

    public static LSNote getNoteByServerId(String id) {
        ArrayList<LSNote> list = null;
        try {
            list = (ArrayList<LSNote>) LSNote.find(LSNote.class, "server_id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
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

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}