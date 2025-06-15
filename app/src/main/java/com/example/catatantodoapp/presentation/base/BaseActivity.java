package com.example.catatantodoapp.presentation.base;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setupUI();
        observeData();
    }

    protected abstract int getLayoutId();
    protected abstract void setupUI();
    protected abstract void observeData();
}