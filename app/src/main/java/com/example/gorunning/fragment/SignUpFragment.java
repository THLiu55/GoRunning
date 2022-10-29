package com.example.gorunning.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gorunning.R;
import com.example.gorunning.models.Captcha;
import com.example.gorunning.models.User;
import com.example.gorunning.sharings.SharedViewModel;
import com.example.gorunning.utils.MailSender;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SignUpFragment extends Fragment {

    Button cancelButton, confirmButton, sendEmailButton;
    EditText signUpUsername, signUpPassword, signUpRePassword, signUpEmail, signUpCaptcha;
    SharedViewModel sharedViewModel;
    public static final String REGEX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inAnimation();

        cancelButton = requireView().findViewById(R.id.sign_back_btn);
        confirmButton = requireView().findViewById(R.id.sign_confirm_btn);
        sendEmailButton = requireView().findViewById(R.id.sign_send_btn);

        signUpUsername = requireView().findViewById(R.id.signup_username);
        signUpPassword = requireView().findViewById(R.id.signup_password);
        signUpRePassword = requireView().findViewById(R.id.signup_repassword);
        signUpEmail = requireView().findViewById(R.id.signup_email);
        signUpCaptcha = requireView().findViewById(R.id.signup_captcha);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        cancelButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        sendEmailButton.setOnClickListener(view1 -> {
            sendValidationEmail();
        });

        confirmButton.setOnClickListener(view1 -> {
            String emailAddress = signUpEmail.getText().toString();
            String captcha = signUpCaptcha.getText().toString();
            String username = signUpUsername.getText().toString();
            String password = signUpPassword.getText().toString();
            String rePassword = signUpRePassword.getText().toString();
            if (!checkAllFormat(username, password, rePassword)) {
                return;
            }
            // todo: add a new account (check if email is registered & captcha correctness) & update viewModel & jump to login page
            BmobQuery<Captcha> captchaQuery = new BmobQuery<>();
            captchaQuery.addWhereEqualTo("email", emailAddress);
            captchaQuery.findObjects(new FindListener<Captcha>() {
                @Override
                public void done(List<Captcha> list, BmobException e) {
                    if (e==null) {
                        if (list.size() == 0) {
                            signUpEmail.setError("email not validated");
                            return;
                        }
                        Captcha captcha1 = list.get(0);
                        if (!captcha1.getCaptcha().equals(captcha)) {
                            signUpCaptcha.setError("captcha not correct");
                            return;
                        }
                        createAccount(username, password, emailAddress, view1);
                    } else {
                        Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void createAccount(String username, String password, String email, View view1) {
        User user = new User(email, username, password);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                // back to login page
                sharedViewModel.setUser(user);
                NavController navController = Navigation.findNavController(view1);
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });
    }

    private void sendValidationEmail() {
        String emailAddress = signUpEmail.getText().toString();
        if (!checkEmailFormat(emailAddress)) {
            return;
        }
        countDownAnimation();
        BmobQuery<Captcha> captchaQuery = new BmobQuery<>();
        captchaQuery.addWhereEqualTo("email", emailAddress);
        captchaQuery.findObjects(new FindListener<Captcha>() {
            @Override
            public void done(List<Captcha> list, BmobException e) {
                if(e==null){
                    if (list.size() == 0) {
                        System.out.println("here I am");
                        String num = generateCaptcha();
                        createCaptchaRecord(emailAddress, num);
                        sendEmail(emailAddress, num);
                    }
                }else{
                    Log.d("error", e.getMessage());
                }
            }
        });
    }

    // count down animation
    @SuppressLint("UseCompatLoadingForDrawables")
    public void countDownAnimation() {
        sendEmailButton.setBackground(getResources().getDrawable(R.drawable.send_button2));
        sendEmailButton.setClickable(false);
        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                sendEmailButton.setText("" + millisUntilFinished / 1000);
                sendEmailButton.setTextColor(getResources().getColor(R.color.white));
                sendEmailButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                sendEmailButton.setTextSize(15);
            }
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            public void onFinish() {
                sendEmailButton.setText("");
                sendEmailButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.send_message), null, null, null);
                sendEmailButton.setBackground(getResources().getDrawable(R.drawable.send_button));
                sendEmailButton.setClickable(true);
            }
        }.start();
    }

    private void createCaptchaRecord(String email, String num) {
        Captcha captcha = new Captcha(email, num);
        captcha.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null) {
                    Log.d("test", "done");
                } else {
                    Log.d("test", e.getMessage());
                }
            }
        });
    }

    private void sendEmail(String customerEmail, String captcha) {
        new Thread(() -> {
            String title = "Welcome to GoRunning!";
            String message = "your captcha is " + captcha + ".\n";
            MailSender.sendMailToUser(customerEmail, title, message);
        }).start();
    }

    private String generateCaptcha() {
        int random = (int) (Math.random() * 1000000);
        return String.valueOf(random);
    }

    private boolean checkEmailFormat(String email) {
        if (email.equals("")) {
            signUpEmail.setError("wrong format");
            return false;
        }
        Pattern p = Pattern.compile(SignUpFragment.REGEX_EMAIL);
        Matcher matched_to = p.matcher(email);
        if (matched_to.matches()) {
            return true;
        }
        signUpEmail.setError("wrong format");
        return false;
    }

    private boolean checkAllFormat(String username, String password, String rePassword) {
        int nameLength = username.length();
        int passwordLength = password.length();
        boolean flag = true;
        if (nameLength < 3) {
            signUpUsername.setError("too short");
            flag = false;
        }
        if (nameLength > 20) {
            signUpUsername.setError("too long");
            flag = false;
        }
        if (passwordLength < 6) {
            signUpPassword.setError("too short");
            flag = false;
        }
        if (passwordLength > 20) {
            signUpPassword.setError("too long");
            flag = false;
        }
        if (!rePassword.equals(password)) {
            signUpRePassword.setError("not match");
            flag = false;
        }
        return flag;
    }

    private void inAnimation() {
        ConstraintLayout globeView = requireView().findViewById(R.id.signup_globe_container);
        ConstraintLayout titleView = requireView().findViewById(R.id.signup_title_container);
        ConstraintLayout inputView = requireView().findViewById(R.id.signup_input_container);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(globeView, "translationY", -400f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(titleView, "translationY", 240f);
        AlphaAnimation animation3 = new AlphaAnimation(0, 1);
        animation1.setDuration(500);
        animation2.setDuration(500);
        animation3.setDuration(1000);
        animation1.start();
        animation2.start();
        inputView.startAnimation(animation3);
    }
}