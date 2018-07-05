package com.halcyonmobile.adoption.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.halcyonmobile.adoption.ImageBindingAdapter;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.ProfileFragmentBinding;
import com.halcyonmobile.adoption.databinding.UserDescriptionDialogBinding;
import com.halcyonmobile.adoption.model.User;
import com.halcyonmobile.adoption.pet.ManagePetsActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.halcyonmobile.adoption.authentication.SignUpActivity.TABLE_USERS;
import static com.halcyonmobile.adoption.profile.AddPetActivity.PICK_IMAGE;

public class UserProfileActivity extends AppCompatActivity {

    public static final String USER_IMAGES = "user_images";

    private FirebaseAuth firebaseAuth;
    private UserDescriptionDialogBinding dialogBinding;
    private DatabaseReference databaseUsers;
    private ProfileFragmentBinding binding;
    private StorageReference storageReference;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, UserProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_profile);
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child(TABLE_USERS);

        databaseUsers.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (!user.city.isEmpty()) {
                    binding.userDescription.setText("This is " + user.username + ", who lives in " + user.city);
                }
                binding.nameAge.setText(user.username + ", " + getAge(user.dob));
                if (!user.image.isEmpty()) {
                    ImageBindingAdapter.setImageUrl(binding.profileImage, user.image);
                }
                binding.adoptions.setText(user.adoptedPets + " adopted pets");
                binding.given.setText(user.givenPets + " given pets");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ManagePetsActivity.class);
                intent.putExtra("isWishlist", true);
                startActivity(intent);
            }
        });

        binding.buttonPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ManagePetsActivity.class);
                intent.putExtra("isWishlist", false);
                startActivity(intent);
            }
        });

        binding.userDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addDescriptionBuilder = new AlertDialog.Builder(UserProfileActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                dialogBinding = DataBindingUtil.inflate(inflater, R.layout.description_dialog, null, false);
                addDescriptionBuilder.setTitle("Add a description");
                addDescriptionBuilder.setView(dialogBinding.getRoot());
                addDescriptionBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        databaseUsers.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                assert user != null;
                                user.description = dialogBinding.enterDescription.getText().toString();
                                user.city = dialogBinding.userCity.getText().toString();
                                user.country = dialogBinding.userCountry.getText().toString();
                                databaseUsers.child(user.id).setValue(user);
                                if (!user.city.isEmpty()) {
                                    binding.userDescription.setText("This is " + user.username + ", who lives in " + user.city);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(UserProfileActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                addDescriptionBuilder.setNeutralButton("Cancel", null);
                AlertDialog dialog = addDescriptionBuilder.create();
                Objects.requireNonNull(dialog.getWindow()).setLayout(600, 400);
                dialog.show();
            }
        });
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri profileImageUri = data.getData();
            binding.profileImage.setImageURI(profileImageUri);
            final StorageReference usersStorage = storageReference.child(USER_IMAGES);
            assert profileImageUri != null;
            usersStorage.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).putFile(profileImageUri);

            databaseUsers.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    usersStorage.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.image = uri.toString();
                            databaseUsers.child(user.id).setValue(user);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private int getAge(long time) {
        Calendar present = Calendar.getInstance();
        Calendar userDate = Calendar.getInstance();
        userDate.setTime(new Date(time));
        return present.get(Calendar.YEAR) - userDate.get(Calendar.YEAR);
    }

}
