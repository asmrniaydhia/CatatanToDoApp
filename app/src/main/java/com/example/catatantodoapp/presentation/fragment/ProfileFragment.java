package com.example.catatantodoapp.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.repository.AuthRepositoryImpl;
import com.example.catatantodoapp.domain.usecase.AuthUseCase;
import com.example.catatantodoapp.presentation.activity.LoginActivity;

public class ProfileFragment extends Fragment {
    private AuthUseCase authUseCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authUseCase = new AuthUseCase(new AuthRepositoryImpl());

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Clear SharedPreferences on logout
            requireActivity().getSharedPreferences("AppPrefs", requireActivity().MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            authUseCase.logout();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}