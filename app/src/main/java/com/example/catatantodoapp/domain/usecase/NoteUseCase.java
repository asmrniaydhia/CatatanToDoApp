package com.example.catatantodoapp.domain.usecase;

import com.example.catatantodoapp.data.model.Note;
import com.example.catatantodoapp.data.repository.NoteRepository;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class NoteUseCase {
    private final NoteRepository noteRepository;

    public NoteUseCase(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Task<Void> addNote(Note note) {
        return noteRepository.addNote(note);
    }

    public Task<List<Note>> getNotes(String userId) {
        return noteRepository.getNotes(userId);
    }

    public Task<Void> updateNote(Note note) {
        return noteRepository.updateNote(note);
    }

    public Task<Void> deleteNote(String noteId, String userId) {
        return noteRepository.deleteNote(noteId, userId);
    }
}