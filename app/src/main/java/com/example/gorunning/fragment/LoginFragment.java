package com.example.gorunning.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.gorunning.R;

public class LoginFragment extends Fragment {

    Button signUpButton, forgetPasswordButton, loginButton;
    EditText emailInput, passwordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpButton = requireView().findViewById(R.id.sign_up_btn);
        forgetPasswordButton = requireView().findViewById(R.id.forget_password_btn);
        loginButton = requireView().findViewById(R.id.login_btn);
        emailInput = requireView().findViewById(R.id.login_email_input);
        passwordInput = requireView().findViewById(R.id.login_password_input);


        signUpButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_loginFragment_to_signUpFragment);
        });

        forgetPasswordButton.setOnClickListener(view12 -> {
            NavController navController = Navigation.findNavController(view12);
            navController.navigate(R.id.action_loginFragment_to_forgetPasswordFragment);
        });

        loginButton.setOnClickListener(view13 -> {
            boolean success = login();
            if (success) {
                // todo
            }
        });
    }

    // todo
    private boolean login() {
        return false;
    }
}