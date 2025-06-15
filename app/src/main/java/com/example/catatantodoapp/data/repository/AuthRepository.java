package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.User;
import com.google.android.gms.tasks.Task;

public interface AuthRepository {
    Task<User> register(String email, String password);
    Task<User> login(String email, String password);
    void logout();
    User getCurrentUser();
}