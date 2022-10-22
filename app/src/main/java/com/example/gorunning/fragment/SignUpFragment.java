package com.example.gorunning.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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

import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SignUpFragment extends Fragment {

    Button cancelButton, confirmButton, sendEmailButton;
    EditText signUpUsername, signUpPassword, signUpRePassword, signUpEmail, signUpCaptcha;
    boolean validEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelButton = requireView().findViewById(R.id.sign_back_btn);
        confirmButton = requireView().findViewById(R.id.sign_confirm_btn);
        sendEmailButton = requireView().findViewById(R.id.sign_send_btn);

        signUpUsername = requireView().findViewById(R.id.signup_username);
        signUpPassword = requireView().findViewById(R.id.signup_password);
        signUpRePassword = requireView().findViewById(R.id.signup_repassword);
        signUpEmail = requireView().findViewById(R.id.signup_email);
        signUpCaptcha = requireView().findViewById(R.id.signup_captcha);

        cancelButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        sendEmailButton.setOnClickListener(view1 -> {
            sendValidationEmail();
        });

        confirmButton.setOnClickListener(view1 -> {
            addNewAccount();
        });
    }

    private void addNewAccount() {
        String emailAddress = signUpEmail.getText().toString();
        String captcha = signUpCaptcha.getText().toString();
        LCQuery<LCObject> query = new LCQuery<>("user");
        query.whereEqualTo("email", emailAddress);
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable d) {}
            public void onNext(List<LCObject> lcObjects) {
                if (lcObjects.size() == 0) {
                    // check the captcha correctness
                    new LCQuery<>("captcha").whereEqualTo("email", emailAddress).findInBackground().subscribe(new Observer<List<LCObject>>() {
                        public void onSubscribe(Disposable d) {}
                        public void onNext(List<LCObject> lcObjects) {
                            if (lcObjects.size() != 0 && lcObjects.get(0).get("captcha").equals(captcha)) {
                                String username = signUpUsername.getText().toString();
                                String password = signUpPassword.getText().toString();
                                createAccount(username, password, emailAddress);
                            }
                        }
                        public void onError(Throwable e) {}
                        public void onComplete() {}
                    });

                } else {
                    signUpEmail.setError("registered email");
                }
            }
            public void onError(Throwable e) {
                signUpEmail.setError("fail to send");
            }
            public void onComplete() {}
        });
    }

    private void createAccount(String username, String password, String email) {
        LCObject newAccount = new LCObject("user");
        newAccount.put("username", username);
        newAccount.put("password", password);
        newAccount.put("email", email);
        newAccount.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable d) {}
            public void onNext(LCObject lcObject) {
                // todo -- after create an account in database
            }
            public void onError(Throwable e) {}
            public void onComplete() {}
        });
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void sendValidationEmail() {
        String emailAddress = signUpEmail.getText().toString();
        String captcha = signUpCaptcha.getText().toString();
        sendEmailButton.setBackground(getResources().getDrawable(R.drawable.send_button2));
        LCQuery<LCObject> query = new LCQuery<>("user");
        query.whereEqualTo("email", emailAddress);
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable d) {}
            public void onNext(List<LCObject> lcObjects) {
                if (lcObjects.size() == 0) {
                    // send email
                    sendEmail(emailAddress);
                    // create captcha record
                    createCaptchaRecord(emailAddress, captcha);
                } else {
                    signUpEmail.setError("registered email");
                }
            }
            public void onError(Throwable e) {}
            public void onComplete() {}
        });
    }

    private void createCaptchaRecord(String email, String captcha) {
        LCObject captchaRecord = new LCObject("captcha");
        captchaRecord.put("email", email);
        captchaRecord.put("captcha", captcha);
        captchaRecord.saveInBackground();
    }

    // todo -- send an email with captcha
    private void sendEmail(String address) {

    }
}