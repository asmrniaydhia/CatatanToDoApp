package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.Note;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepository {
    private final FirebaseFirestore db;

    public NoteRepositoryImpl() {
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public Task<Void> addNote(Note note) {
        return db.collection("users")
                .document(note.getUserId())
                .collection("notes")
                .document(note.getId())
                .set(note);
    }

    @Override
    public Task<List<Note>> getNotes(String userId) {
        return db.collection("users")
                .document(userId)
                .collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<Note> notes = new ArrayList<>();
                        for (int i = 0; i < querySnapshot.size(); i++) {
                            notes.add(querySnapshot.getDocuments().get(i).toObject(Note.class));
                        }
                        return notes;
                    }
                    throw task.getException();
                });
    }

    @Override
    public Task<Void> updateNote(Note note) {
        return db.collection("users")
                .document(note.getUserId())
                .collection("notes")
                .document(note.getId())
                .set(note);
    }

    @Override
    public Task<Void> deleteNote(String noteId, String userId) {
        return db.collection("users")
                .document(userId)
                .collection("notes")
                .document(noteId)
                .delete();
    }
}