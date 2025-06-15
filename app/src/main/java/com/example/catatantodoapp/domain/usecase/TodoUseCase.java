package com.example.catatantodoapp.domain.usecase;

import com.example.catatantodoapp.data.model.Todo;
import com.example.catatantodoapp.data.repository.TodoRepository;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class TodoUseCase {
    private final TodoRepository todoRepository;

    public TodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Task<Void> addTodo(Todo todo) {
        return todoRepository.addTodo(todo);
    }

    public Task<List<Todo>> getTodos(String userId) {
        return todoRepository.getTodos(userId);
    }

    public Task<Void> updateTodo(Todo todo) {
        return todoRepository.updateTodo(todo);
    }

    public Task<Void> deleteTodo(String todoId, String userId) {
        return todoRepository.deleteTodo(todoId, userId);
    }
}