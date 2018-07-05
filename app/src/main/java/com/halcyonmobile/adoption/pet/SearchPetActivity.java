package com.halcyonmobile.adoption.pet;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.PetSearchBinding;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.profile.PetAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.halcyonmobile.adoption.profile.AddPetActivity.TABLE_PETS;

public class SearchPetActivity extends AppCompatActivity {

    private List<Pet> foundPets;
    private FirebaseDatabase firebaseDatabase;
    private PetSearchBinding binding;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SearchPetActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foundPets = new ArrayList<>();
        setContentView(R.layout.activity_search_pet);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_pet);
        firebaseDatabase = FirebaseDatabase.getInstance();

        binding.foundPetsRecyclerView.setHasFixedSize(true);
        binding.foundPetsRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.foundPetsRecyclerView.setAdapter(new PetAdapter(foundPets, new PetAdapter.OnClick() {
            @Override
            public void setItemClickListener(Pet pet, int position) {
                Intent intent = new Intent(SearchPetActivity.this, PetDetailActivity.class);
                intent.putExtra("id", pet.getId());
                startActivity(intent);
            }
        }));

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.searchedPetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchPet(s.toString());
            }
        });
    }


    public void searchPet(final String name) {
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child(TABLE_PETS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot, name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot snapshot, String name) {
        for (DataSnapshot item : snapshot.getChildren()) {
            final Pet pet = item.getValue(Pet.class);
            if (name.isEmpty()) {
                foundPets.clear();
            }
            if (pet.getName().toLowerCase().contains(name.toLowerCase())
                    && petIsUnique(pet)) {
                foundPets.add(pet);
                if (!petIsUnique(pet)) {
                   // foundPets.remove(pet);
                }
            }
            binding.foundPetsRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public boolean petIsUnique(Pet pet) {
        for (Pet p : foundPets) {
            if (p.equals(pet)) {
                return false;
            }
        }
        return true;
    }
}
