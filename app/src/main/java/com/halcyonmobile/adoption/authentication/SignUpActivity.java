package com.halcyonmobile.adoption.authentication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.SignUpBinding;
import com.halcyonmobile.adoption.model.User;
import com.halcyonmobile.adoption.profile.ProfileActivity;

import java.util.Calendar;
import java.util.Objects;


public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference databaseUsers;
    private SignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    public static final String TABLE_USERS = "users";

    private long date = 0;

    public static void getStartIntent(Context context) {
        context.startActivity(new Intent(context, SignUpActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child(TABLE_USERS);
        binding.dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    createDatePicker();
                }
            }
        });
        binding.submitSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = binding.email.getText().toString(), password = binding.password.getText().toString();
                if (validateUser()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                databaseUsers.child(user.getUid()).setValue(new User(user.getUid(),
                                        binding.username.getText().toString(), email, password, date,
                                        "", "", "", 0, 0,
                                        ""));
                                toast("Signed up successfully!");
                                ProfileActivity.getStartIntent(SignUpActivity.this);
                            } else {
                                toast(Objects.requireNonNull(task.getException()).getMessage());
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                                dialogBuilder.setTitle("User already exists");
                                dialogBuilder.setMessage("This user already exists. Log in to access the application");
                                dialogBuilder.setPositiveButton("log in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginActivity.getStartIntent(SignUpActivity.this);
                                    }
                                });
                                dialogBuilder.setNegativeButton("cancel", null);
                                dialogBuilder.show();
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean validateUser() {
        if (binding.username.getText().toString().length() < 6) {
            toast("Username length should be at least 6");
            return false;
        }
        if (binding.password.getText().toString().length() < 6) {
            toast("Password length should be at least 6");
            return false;
        }
        if (!binding.email.getText().toString().contains("@")) {
            binding.email.setError("Invalid email");
            toast("Please provide a valid email");
            return false;
        }
        if (date == 0) {
            toast("Specify your birthday");
            return false;
        }
        return true;
    }

    private void createDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Dialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.dob.setText(dayOfMonth + " " + getMonthName(month) + " " + year);
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth, 0, 0, 0);
                date = c.getTimeInMillis();
            }
        }, year, month, day);
        dialog.show();
    }

    private String getMonthName(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            default:
                return "December";
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
