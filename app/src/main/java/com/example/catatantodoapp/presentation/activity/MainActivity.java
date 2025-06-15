package com.example.catatantodoapp.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.repository.AuthRepositoryImpl;
import com.example.catatantodoapp.domain.usecase.AuthUseCase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private AuthUseCase authUseCase;
    private static final String KEY_LAST_DESTINATION = "last_destination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authUseCase = new AuthUseCase(new AuthRepositoryImpl());

        // Check if user is logged in
        if (authUseCase.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Always start with NotesFragment on fresh login
            if (savedInstanceState == null) {
                navController.navigate(R.id.notesFragment);
            } else {
                // Restore last destination if activity is recreated (e.g., from background)
                int lastDestinationId = savedInstanceState.getInt(KEY_LAST_DESTINATION, R.id.notesFragment);
                navController.navigate(lastDestinationId);
            }

            // Setup BottomNavigationView with NavController
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // Save destination when user navigates
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Save the current destination to be restored later
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        .edit()
                        .putInt(KEY_LAST_DESTINATION, destination.getId())
                        .apply();
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current destination when activity is paused (e.g., switching apps)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            int currentDestinationId = navHostFragment.getNavController().getCurrentDestination().getId();
            outState.putInt(KEY_LAST_DESTINATION, currentDestinationId);
        }
    }
}