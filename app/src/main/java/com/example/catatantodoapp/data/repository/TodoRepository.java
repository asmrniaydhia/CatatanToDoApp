package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.Todo;
import com.google.android.gms.tasks.Task;
import java.util.List;

public interface TodoRepository {
    Task<Void> addTodo(Todo todo);
    Task<List<Todo>> getTodos(String userId);
    Task<Void> updateTodo(Todo todo);
    Task<Void> deleteTodo(String todoId, String userId);
}