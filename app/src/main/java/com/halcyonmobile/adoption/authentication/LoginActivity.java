package com.halcyonmobile.adoption.authentication;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.LogInBinding;
import com.halcyonmobile.adoption.profile.ProfileActivity;

public class LoginActivity extends AppCompatActivity {
    private LogInBinding binding;
    private FirebaseAuth firebaseAuth;
    public static void getStartIntent(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        Glide.with(this)
                .load("https://cdn.pixabay.com/photo/2017/01/31/17/57/animal-2026001__340.png")
                .into(binding.titleImage);
        binding.submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                ProfileActivity.getStartIntent(LoginActivity.this);
                            }
                        });
            }
        });

        binding.noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.getStartIntent(LoginActivity.this);
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(binding.email.getText().toString());
            }
        });
    }
}
