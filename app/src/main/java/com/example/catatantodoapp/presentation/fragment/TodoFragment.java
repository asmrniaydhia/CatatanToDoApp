package com.example.catatantodoapp.presentation.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.catatantodoapp.data.model.Todo;
import com.example.catatantodoapp.data.repository.AuthRepositoryImpl;
import com.example.catatantodoapp.data.repository.TodoRepositoryImpl;
import com.example.catatantodoapp.domain.usecase.AuthUseCase;
import com.example.catatantodoapp.domain.usecase.TodoUseCase;
import com.example.catatantodoapp.presentation.activity.AddEditTodoActivity;
import com.example.catatantodoapp.presentation.adapter.TodoAdapter;
import com.example.catatantodoapp.receiver.ReminderReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodoFragment extends Fragment implements TodoAdapter.OnTodoClickListener {
    private TodoUseCase todoUseCase;
    private AuthUseCase authUseCase;
    private TodoAdapter todoAdapter;
    private ActivityResultLauncher<Intent> todoResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        todoUseCase = new TodoUseCase(new TodoRepositoryImpl());
        authUseCase = new AuthUseCase(new AuthRepositoryImpl());

        RecyclerView rvTodos = view.findViewById(R.id.rv_todos);
        rvTodos.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoAdapter = new TodoAdapter(this);
        rvTodos.setAdapter(todoAdapter);

        loadTodos();

        todoResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Todo todo = (Todo) result.getData().getSerializableExtra(AddEditTodoActivity.EXTRA_TODO);
                String userId = authUseCase.getCurrentUser().getUid();
                todo.setUserId(userId);
                if (todo.getId() == null || todo.getId().isEmpty()) {
                    todo.setId(java.util.UUID.randomUUID().toString());
                    todoUseCase.addTodo(todo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (todo.getReminderTime() > 0) {
                                scheduleReminder(todo);
                            }
                            loadTodos();
                        } else {
                            Toast.makeText(requireContext(), "Failed to add todo", Toast.LENGTH_SHORT).show();
                            Log.e("TodoFragment", "Failed to add todo: " + task.getException());
                        }
                    });
                } else {
                    todoUseCase.updateTodo(todo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (todo.getReminderTime() > 0) {
                                scheduleReminder(todo);
                            }
                            loadTodos();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update todo", Toast.LENGTH_SHORT).show();
                            Log.e("TodoFragment", "Failed to update todo: " + task.getException());
                        }
                    });
                }
            }
        });

        FloatingActionButton fabAddTodo = view.findViewById(R.id.fab_add_todo);
        fabAddTodo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditTodoActivity.class);
            todoResultLauncher.launch(intent);
        });

        return view;
    }

    private void loadTodos() {
        String userId = authUseCase.getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("TodoFragment", "User ID is null");
            return;
        }
        Log.d("TodoFragment", "Loading todos for user: " + userId);
        todoUseCase.getTodos(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                todoAdapter.setTodos(task.getResult());
                Log.d("TodoFragment", "Todos loaded successfully: " + task.getResult().size());
            } else {
                Toast.makeText(requireContext(), "Failed to load todos", Toast.LENGTH_SHORT).show();
                Log.e("TodoFragment", "Failed to load todos: " + task.getException());
            }
        });
    }

    private void scheduleReminder(Todo todo) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Izin alarm eksak diperlukan. Buka pengaturan aplikasi.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("title", todo.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                todo.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    todo.getReminderTime(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    todo.getReminderTime(),
                    pendingIntent
            );
        }
    }

    private void cancelReminder(Todo todo) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                todo.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onTodoClick(Todo todo) {
        Intent intent = new Intent(requireContext(), AddEditTodoActivity.class);
        intent.putExtra(AddEditTodoActivity.EXTRA_TODO, todo);
        todoResultLauncher.launch(intent);
    }

    @Override
    public void onTodoDelete(Todo todo) {
        todoUseCase.deleteTodo(todo.getId(), todo.getUserId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cancelReminder(todo);
                loadTodos();
            } else {
                Toast.makeText(requireContext(), "Failed to delete todo", Toast.LENGTH_SHORT).show();
                Log.e("TodoFragment", "Failed to delete todo: " + task.getException());
            }
        });
    }

    @Override
    public void onTodoChecked(Todo todo, boolean isChecked) {
        todo.setCompleted(isChecked);
        todoUseCase.updateTodo(todo).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(requireContext(), "Failed to update todo", Toast.LENGTH_SHORT).show();
                Log.e("TodoFragment", "Failed to update todo: " + task.getException());
            }
        });
    }
}