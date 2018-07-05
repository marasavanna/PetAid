package com.halcyonmobile.adoption.profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.ProfileDataBinding;
import com.halcyonmobile.adoption.pet.SearchPetActivity;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private ProfileDataBinding binding;

    public static void getStartIntent(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
//        binding.toolbar.inflateMenu(R.menu.toolbar_menu);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_pets:
                        selectedFragment = PetsFragment.newInstance();
                        break;
                    case R.id.navigation_chat:
                        selectedFragment = ChatFragment.Companion.getInstance();
                        break;
                    case R.id.navigation_settings:
                        selectedFragment = new UserProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), selectedFragment).commit();
                return true;
            }
        });

//        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId() == R.id.search){
//                    startActivity(SearchPetActivity.getStartIntent(ProfileActivity.this));
//                    return true;
//                }
//                return false;
//            }
//        });
        getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), PetsFragment.newInstance()).commit();
    }
}
