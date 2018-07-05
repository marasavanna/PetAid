package com.halcyonmobile.adoption.pet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.profile.PetAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.halcyonmobile.adoption.profile.AddPetActivity.TABLE_PETS;

public class FilterBySpeciesActivity extends AppCompatActivity {


    private List<Pet> filteredPets;
    private PetAdapter adapter;
    private FirebaseDatabase firebaseDatabase;

    public static Intent getStartIntent(Context context){
        return new Intent(context, FilterBySpeciesActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_by_species);

        filteredPets = new ArrayList<>();
        adapter = new PetAdapter(filteredPets, new PetAdapter.OnClick() {
            @Override
            public void setItemClickListener(Pet pet, int position) {
                Intent intent = new Intent(FilterBySpeciesActivity.this, PetDetailActivity.class);
                intent.putExtra("id", pet.getId());
                startActivity(intent);
            }
        });


        RecyclerView filteredPetsRv = findViewById(R.id.filtered_pets_recycler_view);
        filteredPetsRv.setLayoutManager(new LinearLayoutManager(this));
        filteredPetsRv.setHasFixedSize(true);
        filteredPetsRv.setAdapter(adapter);


        ImageView back = findViewById(R.id.button_back_filter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final AutoCompleteTextView filter = findViewById(R.id.searched_pet_species);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterPets(s.toString());
            }
        });
    }


    public void filterPets(final String species){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child(TABLE_PETS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot, species);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot snapshot, String species){
        filteredPets.clear();
        for(DataSnapshot item: snapshot.getChildren()) {
            final Pet pet = item.getValue(Pet.class);
            assert pet != null;
            if(pet.getSpecies().toLowerCase().contains(species.toLowerCase())) {
                filteredPets.add(pet);
                adapter.notifyDataSetChanged();
            }

        }
    }
}
