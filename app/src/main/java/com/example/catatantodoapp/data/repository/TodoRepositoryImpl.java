package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.Todo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class TodoRepositoryImpl implements TodoRepository {
    private final FirebaseFirestore db;

    public TodoRepositoryImpl() {
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public Task<Void> addTodo(Todo todo) {
        return db.collection("users")
                .document(todo.getUserId())
                .collection("todos")
                .document(todo.getId())
                .set(todo);
    }

    @Override
    public Task<List<Todo>> getTodos(String userId) {
        return db.collection("users")
                .document(userId)
                .collection("todos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<Todo> todos = new ArrayList<>();
                        for (int i = 0; i < querySnapshot.size(); i++) {
                            todos.add(querySnapshot.getDocuments().get(i).toObject(Todo.class));
                        }
                        return todos;
                    }
                    throw task.getException();
                });
    }

    @Override
    public Task<Void> updateTodo(Todo todo) {
        return db.collection("users")
                .document(todo.getUserId())
                .collection("todos")
                .document(todo.getId())
                .set(todo);
    }

    @Override
    public Task<Void> deleteTodo(String todoId, String userId) {
        return db.collection("users")
                .document(userId)
                .collection("todos")
                .document(todoId)
                .delete();
    }
}