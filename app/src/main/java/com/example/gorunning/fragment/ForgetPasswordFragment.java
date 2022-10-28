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
import cn.bmob.v3.listener.UpdateListener;


public class ForgetPasswordFragment extends Fragment {

    Button backButton, confirmButton, sendButton;
    EditText emailInput, captchaInput, passwordInput, rePasswordInput;
    SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inAnimation();

        sendButton = requireView().findViewById(R.id.forget_send_btn);
        backButton = requireView().findViewById(R.id.forget_back_btn);
        confirmButton = requireView().findViewById(R.id.forget_confirm_btn);

        emailInput = requireView().findViewById(R.id.forget_email_input);
        captchaInput = requireView().findViewById(R.id.forget_captcha_input);
        passwordInput = requireView().findViewById(R.id.forget_password_input);
        rePasswordInput = requireView().findViewById(R.id.forget_re_password_input);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        backButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment);
        });

        sendButton.setOnClickListener(view1 -> sendValidateEmail());

        confirmButton.setOnClickListener(view1 -> {
            String email = emailInput.getText().toString();
            String captcha = captchaInput.getText().toString();
            String password = passwordInput.getText().toString();
            String rePassword = rePasswordInput.getText().toString();
            if (!checkAllFormat(password, rePassword)) {
                return;
            }
            BmobQuery<Captcha> captchaQuery = new BmobQuery<>();
            captchaQuery.addWhereEqualTo("email", email);
            captchaQuery.findObjects(new FindListener<Captcha>() {
                @Override
                public void done(List<Captcha> list, BmobException e) {
                    if (list.size() == 0) {
                        emailInput.setError("email not registered");
                        return;
                    }
                    if (!list.get(0).getCaptcha().equals(captcha)) {
                        captchaInput.setError("wrong captcha");
                        return;
                    }
                    resetPassword(view1, email, password);
                }
            });
        });
    }

    private void resetPassword(View view, String email, String password) {
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("email", email);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        emailInput.setError("account not exist");
                        return;
                    }
                    User user = new User(email, list.get(0).getUsername(), password);
                    user.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sharedViewModel.setUser(user);
                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment);
                            } else {
                                Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendValidateEmail() {
        String email = emailInput.getText().toString();
        if (!checkEmailFormat(email)) {
            return;
        }
        countDownAnimation();
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("email", email);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        emailInput.setError("email not registered");
                        return;
                    }
                    String num = generateCaptcha();
                    updateCaptchaRecord(email, num);
                    sendEmail(email, num);
                } else {
                    Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCaptchaRecord(String email, String num) {
        BmobQuery<Captcha> captchaQuery = new BmobQuery<>();
        captchaQuery.addWhereEqualTo("email", email);
        captchaQuery.findObjects(new FindListener<Captcha>() {
            @Override
            public void done(List<Captcha> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        Captcha captcha = new Captcha(email, num);
                        captcha.update(list.get(0).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null)  {
                                    Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail(String customerEmail, String captcha) {
        new Thread(() -> {
            String title = "Reset your password";
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
            emailInput.setError("wrong format");
            return false;
        }
        Pattern p = Pattern.compile(SignUpFragment.REGEX_EMAIL);
        Matcher matched_to = p.matcher(email);
        if (matched_to.matches()) {
            return true;
        }
        emailInput.setError("wrong format");
        return false;
    }

    // count down animation
    @SuppressLint("UseCompatLoadingForDrawables")
    public void countDownAnimation() {
        sendButton.setBackground(getResources().getDrawable(R.drawable.send_button2));
        sendButton.setClickable(false);
        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                sendButton.setText("" + millisUntilFinished / 1000);
                sendButton.setTextColor(getResources().getColor(R.color.white));
                sendButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                sendButton.setTextSize(15);
            }
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            public void onFinish() {
                sendButton.setText("");
                sendButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.send_message), null, null, null);
                sendButton.setBackground(getResources().getDrawable(R.drawable.send_button));
                sendButton.setClickable(true);
            }
        }.start();
    }

    private boolean checkAllFormat(String password, String rePassword) {
        boolean flag = true;
        int passwordLength = password.length();
        if (passwordLength < 6) {
            passwordInput.setError("too short");
            flag = false;
        }
        if (passwordLength > 20) {
            passwordInput.setError("too long");
            flag = false;
        }
        if (!rePassword.equals(password)) {
            rePasswordInput.setError("not match");
        }
        return flag;
    }

    private void inAnimation() {
        ConstraintLayout globeView = requireView().findViewById(R.id.forget_globe_container);
        ConstraintLayout titleView = requireView().findViewById(R.id.forget_title_container);
        ConstraintLayout inputView = requireView().findViewById(R.id.forget_input_container);
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