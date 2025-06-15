package com.example.catatantodoapp.presentation.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.model.Note;
import com.example.catatantodoapp.data.repository.AuthRepositoryImpl;
import com.example.catatantodoapp.data.repository.NoteRepositoryImpl;
import com.example.catatantodoapp.domain.usecase.AuthUseCase;
import com.example.catatantodoapp.domain.usecase.NoteUseCase;
import com.example.catatantodoapp.presentation.activity.AddEditNoteActivity;
import com.example.catatantodoapp.presentation.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotesFragment extends Fragment implements NoteAdapter.OnNoteClickListener {
    private NoteUseCase noteUseCase;
    private AuthUseCase authUseCase;
    private NoteAdapter noteAdapter;
    private ActivityResultLauncher<Intent> noteResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        noteUseCase = new NoteUseCase(new NoteRepositoryImpl());
        authUseCase = new AuthUseCase(new AuthRepositoryImpl());

        RecyclerView rvNotes = view.findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(this);
        rvNotes.setAdapter(noteAdapter);

        loadNotes();

        // Initialize ActivityResultLauncher
        noteResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Note note = (Note) result.getData().getSerializableExtra(AddEditNoteActivity.EXTRA_NOTE);
                note.setUserId(authUseCase.getCurrentUser().getUid());
                if (note.getId() == null || note.getId().isEmpty()) {
                    // Add new note
                    note.setId(java.util.UUID.randomUUID().toString());
                    noteUseCase.addNote(note).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                              loadNotes();
                        } else {
                            Toast.makeText(requireContext(), "Failed to add note", Toast.LENGTH_SHORT).show();
                            Log.e("NotesFragment", "Failed to add note: " + task.getException());
                        }
                    });
                } else {
                    // Update existing note
                    noteUseCase.updateNote(note).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            loadNotes();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
                            Log.e("NotesFragment", "Failed to update note: " + task.getException());
                        }
                    });
                }
            }
        });

        FloatingActionButton fabAddNote = view.findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditNoteActivity.class);
            noteResultLauncher.launch(intent);
        });

        return view;
    }

    private void loadNotes() {
        String userId = authUseCase.getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("NotesFragment", "User ID is null");
            return;
        }
        Log.d("NotesFragment", "Loading notes for user: " + userId);
        noteUseCase.getNotes(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                noteAdapter.setNotes(task.getResult());
                Log.d("NotesFragment", "Notes loaded successfully: " + task.getResult().size());
            } else {
                Toast.makeText(requireContext(), "Failed to load notes", Toast.LENGTH_SHORT).show();
                Log.e("NotesFragment", "Failed to load notes: " + task.getException());
            }
        });
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(requireContext(), AddEditNoteActivity.class);
        intent.putExtra(AddEditNoteActivity.EXTRA_NOTE, note);
        noteResultLauncher.launch(intent);
    }

    @Override
    public void onNoteDelete(Note note) {
        noteUseCase.deleteNote(note.getId(), note.getUserId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadNotes();
            } else {
                Toast.makeText(requireContext(), "Failed to delete note", Toast.LENGTH_SHORT).show();
                Log.e("NotesFragment", "Failed to delete note: " + task.getException());
            }
        });
    }
}