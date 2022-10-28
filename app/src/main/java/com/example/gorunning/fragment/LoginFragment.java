package com.example.gorunning.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gorunning.R;
import com.example.gorunning.models.User;
import com.example.gorunning.sharings.SharedViewModel;

import java.util.List;
import java.util.prefs.Preferences;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginFragment extends Fragment {

    Button signUpButton, forgetPasswordButton, loginButton;
    EditText emailInput, passwordInput;
    SharedViewModel sharedViewModel;
    CheckBox remember_box;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inAnimation();
        signUpButton = requireView().findViewById(R.id.sign_up_btn);
        forgetPasswordButton = requireView().findViewById(R.id.forget_password_btn);
        loginButton = requireView().findViewById(R.id.login_btn);
        emailInput = requireView().findViewById(R.id.login_email_input);
        passwordInput = requireView().findViewById(R.id.login_password_input);
        remember_box = requireView().findViewById(R.id.remember_password_btn);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        if (preferences.getBoolean("checked", false)) {
            emailInput.setText(preferences.getString("email", ""));
            passwordInput.setText(preferences.getString("password", ""));
            remember_box.setChecked(true);
        }

        signUpButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view1);
            navController.navigate(R.id.action_loginFragment_to_signUpFragment);
        });

        forgetPasswordButton.setOnClickListener(view12 -> {
            NavController navController = Navigation.findNavController(view12);
            navController.navigate(R.id.action_loginFragment_to_forgetPasswordFragment);
        });

        remember_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = preferences.edit();
                if (b) {
                    editor.putString("password", passwordInput.getText().toString());
                    editor.putString("email", emailInput.getText().toString());
                    editor.putBoolean("checked", true);
                } else {
                    editor.putString("password", "");
                    editor.putString("email", "");
                    editor.putBoolean("checked", false);
                }
                editor.apply();
            }
        });

        loginButton.setOnClickListener(view13 -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
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
                        if (!list.get(0).getPassword().equals(password)) {
                            passwordInput.setError("password or email incorrect");
                            return;
                        }
                        sharedViewModel.setUser(list.get(0));
                        loginAnimation(view13);
                    } else {
                        Toast.makeText(requireActivity(), "network error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        sharedViewModel.getUser().observe(requireActivity(), user -> {
            emailInput.setText(user.getEmail());
            passwordInput.setText(user.getPassword());
        });
    }

    // jump to home page with animation
    private void loginAnimation(View view1) {
        ConstraintLayout globeView = requireView().findViewById(R.id.login_globe_container);
        ConstraintLayout titleView = requireView().findViewById(R.id.login_title_container);
        ConstraintLayout inputView = requireView().findViewById(R.id.login_input_container);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(globeView, "translationY", 500f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(titleView, "translationY", -300f);
        AlphaAnimation animation3 = new AlphaAnimation(1, 0);
        animation1.setDuration(500);
        animation2.setDuration(500);
        animation3.setDuration(600);
        animation1.start();
        animation2.start();
        inputView.startAnimation(animation3);
        animation1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                NavController navController = Navigation.findNavController(view1);
                navController.navigate(R.id.action_loginFragment_to_homeActivity);
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void inAnimation() {
        ConstraintLayout globeView = requireView().findViewById(R.id.login_globe_container);
        ConstraintLayout titleView = requireView().findViewById(R.id.login_title_container);
        ConstraintLayout inputView = requireView().findViewById(R.id.login_input_container);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(globeView, "translationY", -500f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(titleView, "translationY", 300f);
        AlphaAnimation animation3 = new AlphaAnimation(0, 1);
        animation1.setDuration(500);
        animation2.setDuration(500);
        animation3.setDuration(1000);
        animation1.start();
        animation2.start();
        inputView.startAnimation(animation3);
    }
}