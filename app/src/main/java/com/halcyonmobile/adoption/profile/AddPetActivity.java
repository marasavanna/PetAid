package com.halcyonmobile.adoption.profile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.AddPetBinding;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.model.Trait;
import com.halcyonmobile.adoption.model.TraitConnection;
import com.halcyonmobile.adoption.pet.PickedTraitAdapter;
import com.halcyonmobile.adoption.pet.TraitAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddPetActivity extends AppCompatActivity implements TextWatcher {

    public static final String TABLE_PETS = "pets";
    public static final int PICK_IMAGE = 1;
    public static final int SECOND_IMAGE = 2;

    FirebaseAuth firebaseAuth;
    AddPetBinding binding;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imageUri1;
    Uri imageUri2;
    String petId;
    private long petAge = 0;
    private Pet pet;
    private TraitAdapter traitAdapter;
    private PickedTraitAdapter pickedTraitAdapter;
    private List<Trait> descriptionTraits;
    private DatabaseReference databaseTraits = FirebaseDatabase.getInstance().getReference().child("traits");

    public static void getStartIntent(Context context) {
        context.startActivity(new Intent(context, AddPetActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pet);

        descriptionTraits = new ArrayList<>();

        binding.traitsRecyclerView.setLayoutManager(new LinearLayoutManager(AddPetActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        binding.traitsRecyclerView.setHasFixedSize(true);


        traitAdapter = new TraitAdapter(Trait.getAllTraitNames());
        binding.traitsRecyclerView.setAdapter(traitAdapter);

        pickedTraitAdapter = new PickedTraitAdapter();
        binding.pickedTraitsRecyclerView.setLayoutManager(new LinearLayoutManager(AddPetActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        binding.pickedTraitsRecyclerView.setHasFixedSize(true);
        binding.pickedTraitsRecyclerView.setAdapter(pickedTraitAdapter);

        binding.petDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePickedList(s.toString());
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        pet = new Pet();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        final DatabaseReference databasePets = databaseReference.child(TABLE_PETS);
        binding.petImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(PICK_IMAGE);
            }
        });
        binding.petImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SECOND_IMAGE);
            }
        });
        binding.petAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    createDatePicker();
                }
            }
        });
        binding.petAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatePicker();
            }
        });
        binding.confirmAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                if (validatePet()) {
                    petId = databasePets.push().getKey();
                    pet = new Pet(petId, userId,
                            binding.petName.getText().toString(), binding.petSpecies.getText().toString(),
                            binding.petDescription.getText().toString(), "", "",
                            petAge, false);
                    databasePets.child(petId).setValue(pet);

                    // Add all the traits to database
                    for (Trait selectedTrait : pickedTraitAdapter.getList()) {
                        String id = databaseTraits.push().getKey();
                        assert id != null;
                        databaseTraits.child(id).setValue(new TraitConnection(id, petId, selectedTrait.id));
                    }

                    final StorageReference petStorageMain = storageReference.child("pets").child(petId + 1);
                    petStorageMain.putFile(imageUri1).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            petStorageMain.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pet.setImageMain(uri.toString());
                                    databasePets.child(petId).setValue(pet);
                                }
                            });
                        }
                    });


                    final StorageReference petStorageBackground = storageReference.child("pets").child(petId + 2);
                    petStorageBackground.putFile(imageUri2).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            petStorageBackground.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pet.setImageBackground(uri.toString());
                                    databasePets.child(petId).setValue(pet);
                                }
                            });
                        }
                    });
                    finish();
                } else {
                    Toast.makeText(AddPetActivity.this, "The data you input is not adequate. Try again", Toast.LENGTH_SHORT).show();
                    AddPetActivity.this.recreate();
                }
            }
        });
        binding.petName.addTextChangedListener(this);
        traitAdapter.onClick = new TraitAdapter.ItemOnClick() {
            @Override
            public void onClick(Trait trait, int position) {
                pickedTraitAdapter.add(trait);
                if (!binding.petName.getText().toString().isEmpty()) {
                    updateDescription(binding.petName.getText().toString());
                }
                traitAdapter.remove(trait);
            }
        };
        pickedTraitAdapter.onClick = new TraitAdapter.ItemOnClick() {
            @Override
            public void onClick(Trait trait, int position) {
                traitAdapter.add(trait);
                pickedTraitAdapter.remove(trait);
                binding.petDescription.setText(removeTraitFromDescription(trait.name, binding.petDescription.getText().toString()));
            }
        };

    }

    private void openGallery(int image) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        switch (image) {
            case PICK_IMAGE: {
                startActivityForResult(gallery, PICK_IMAGE);
                break;
            }
            case SECOND_IMAGE: {
                startActivityForResult(gallery, SECOND_IMAGE);
                break;
            }
        }
    }

    private void updateDescription(String name) {
        StringBuilder str = new StringBuilder(name + " is ");
        for (int i = 0; i < pickedTraitAdapter.getItemCount(); i++) {

            if (i == pickedTraitAdapter.getItemCount() - 1) {
                str.append(pickedTraitAdapter.get(i).name).append(".");
            } else {
                str.append(pickedTraitAdapter.get(i).name).append(", ");
            }
        }
        binding.petDescription.setText(str.toString());
    }

    private String removeTraitFromDescription(String trait, String description) {
        String result;
        result = description.replaceAll(trait, "").replaceAll(", \\.", "\\.");
        return result;
    }


    private void updatePickedList(String description) {
        descriptionTraits.clear();
        for (int i = 0; i < pickedTraitAdapter.getItemCount(); i++) {
            descriptionTraits.add(pickedTraitAdapter.get(i));
        }
        for (Trait trait : descriptionTraits) {
            if (!description.contains(trait.name)) {
                pickedTraitAdapter.remove(trait);
                traitAdapter.add(trait);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri1 = data.getData();
            binding.petImage1.setImageURI(imageUri1);

        }
        if (resultCode == RESULT_OK && requestCode == SECOND_IMAGE) {
            imageUri2 = data.getData();
            binding.petImage2.setImageURI(imageUri2);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean validatePet() {
        return !binding.petName.getText().toString().isEmpty() && !binding.petSpecies.getText().toString().isEmpty() &&
                !binding.petDescription.getText().toString().isEmpty() && !binding.petAge.getText().toString().isEmpty();
    }

    private void createDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Dialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth, 0, 0, 0);
                petAge = c.getTimeInMillis();
                pet.setAge(petAge);
                binding.petAge.setText(pet.getAgeString() + " old");
            }
        }, year, month, day);
        dialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateDescription(s.toString());
    }
}
