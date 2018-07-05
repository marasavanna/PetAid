package com.halcyonmobile.adoption;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.halcyonmobile.adoption.authentication.LoginActivity;
import com.halcyonmobile.adoption.authentication.SignUpActivity;
import com.halcyonmobile.adoption.databinding.MainActivityBinding;
import com.halcyonmobile.adoption.introduction.IntroViewPagerAdapter;
import com.halcyonmobile.adoption.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ProfileActivity.getStartIntent(this);
            finish();
        }
        binding.introViewPager.setAdapter(new IntroViewPagerAdapter(getSupportFragmentManager()));
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.getStartIntent(MainActivity.this);
            }
        });

        binding.logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.getStartIntent(MainActivity.this);
            }
        });
    }
}
