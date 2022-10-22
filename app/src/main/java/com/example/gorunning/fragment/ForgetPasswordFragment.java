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


public class ForgetPasswordFragment extends Fragment {

    Button backButton, confirmButton, sendButton;
    EditText emailInput, captchaInput, passwordInput, rePasswordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendButton = requireView().findViewById(R.id.forget_send_btn);
        backButton = requireView().findViewById(R.id.forget_back_btn);
        confirmButton = requireView().findViewById(R.id.forget_confirm_btn);

        emailInput = requireView().findViewById(R.id.forget_email_input);
        captchaInput = requireView().findViewById(R.id.forget_captcha_input);
        passwordInput = requireView().findViewById(R.id.forget_password_input);
        rePasswordInput = requireView().findViewById(R.id.forget_re_password_input);

        backButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment);
        });

        sendButton.setOnClickListener(view1 -> {
            sendValidateEmail();
        });

        confirmButton.setOnClickListener(view1 -> {
            boolean success = resetPassword();
            if (success) {
                // todo
            }
        });
    }

    // todo
    private boolean resetPassword() {
        return false;
    }

    // todo
    private void sendValidateEmail() {

    }
}