package com.example.catatantodoapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.model.Todo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos = new ArrayList<>();
    private final OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(Todo todo);
        void onTodoDelete(Todo todo);
        void onTodoChecked(Todo todo, boolean isChecked);
    }

    public TodoAdapter(OnTodoClickListener listener) {
        this.listener = listener;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final CheckBox cbCompleted;
        private final TextView tvTimestamp;

        TodoViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_todo_title);
            cbCompleted = itemView.findViewById(R.id.cb_todo_completed);
            tvTimestamp = itemView.findViewById(R.id.tv_todo_timestamp);
            itemView.findViewById(R.id.btn_delete_todo).setOnClickListener(v -> {
                listener.onTodoDelete(todos.get(getAdapterPosition()));
            });
            itemView.setOnClickListener(v -> {
                listener.onTodoClick(todos.get(getAdapterPosition()));
            });
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onTodoChecked(todos.get(getAdapterPosition()), isChecked);
            });
        }

        void bind(Todo todo) {
            tvTitle.setText(todo.getTitle());
            cbCompleted.setChecked(todo.isCompleted());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvTimestamp.setText(sdf.format(new Date(todo.getTimestamp())));
        }
    }
}