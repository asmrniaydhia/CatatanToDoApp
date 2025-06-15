package com.example.catatantodoapp.domain.usecase;

import com.example.catatantodoapp.data.model.User;
import com.example.catatantodoapp.data.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

public class AuthUseCase {
    private final AuthRepository authRepository;

    public AuthUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Task<User> register(String email, String password) {
        return authRepository.register(email, password);
    }

    public Task<User> login(String email, String password) {
        return authRepository.login(email, password);
    }

    public void logout() {
        authRepository.logout();
    }

    public User getCurrentUser() {
        return authRepository.getCurrentUser();
    }
}