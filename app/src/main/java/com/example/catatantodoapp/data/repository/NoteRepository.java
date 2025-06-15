package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.Note;
import com.google.android.gms.tasks.Task;
import java.util.List;

public interface NoteRepository {
    Task<Void> addNote(Note note);
    Task<List<Note>> getNotes(String userId);
    Task<Void> updateNote(Note note);
    Task<Void> deleteNote(String noteId, String userId);
}