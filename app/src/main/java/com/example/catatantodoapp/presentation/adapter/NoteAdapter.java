package com.example.catatantodoapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.model.Note;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private final OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
    }

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvTimestamp;
        private final ImageView ivNoteImage;

        NoteViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            tvTimestamp = itemView.findViewById(R.id.tv_note_timestamp);
            ivNoteImage = itemView.findViewById(R.id.iv_note_image);
            itemView.findViewById(R.id.btn_delete_note).setOnClickListener(v -> {
                listener.onNoteDelete(notes.get(getAdapterPosition()));
            });
            itemView.setOnClickListener(v -> {
                listener.onNoteClick(notes.get(getAdapterPosition()));
            });
        }

        void bind(Note note) {
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvTimestamp.setText(sdf.format(new Date(note.getTimestamp())));
            if (note.getImageUrl() != null && !note.getImageUrl().isEmpty()) {
                ivNoteImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(note.getImageUrl())
                        .into(ivNoteImage);
            } else {
                ivNoteImage.setVisibility(View.GONE);
            }
        }
    }
}