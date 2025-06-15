package com.example.catatantodoapp.data.repository;

import com.example.catatantodoapp.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Task<User> register(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            return new User(firebaseUser.getUid(), firebaseUser.getEmail());
                        }
                    }
                    throw task.getException();
                });
    }

    @Override
    public Task<User> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            return new User(firebaseUser.getUid(), firebaseUser.getEmail());
                        }
                    }
                    throw task.getException();
                });
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            return new User(firebaseUser.getUid(), firebaseUser.getEmail());
        }
        return null;
    }
}