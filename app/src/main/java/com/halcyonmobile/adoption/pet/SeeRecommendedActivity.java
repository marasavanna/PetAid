package com.halcyonmobile.adoption.pet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.model.Trait;
import com.halcyonmobile.adoption.model.TraitConnection;
import com.halcyonmobile.adoption.profile.PetAdapter;
import com.halcyonmobile.adoption.profile.PetsFragment;

import java.util.ArrayList;
import java.util.List;

public class SeeRecommendedActivity extends AppCompatActivity {

    private List<Pet> recommended;
    private PetAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private List<TraitConnection> desiredTraits;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SeeRecommendedActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_recommended);

        recommended = PetsFragment.recommmendedPets;
        adapter = new PetAdapter(recommended, new PetAdapter.OnClick() {
            @Override
            public void setItemClickListener(Pet pet, int position) {
                Intent intent = new Intent(SeeRecommendedActivity.this, PetDetailActivity.class);
                intent.putExtra("id", pet.getId());
                startActivity(intent);
            }
        });
        RecyclerView recommendedPets = findViewById(R.id.recommended_pets);
        recommendedPets.setLayoutManager(new LinearLayoutManager(this));
        recommendedPets.setHasFixedSize(true);
        recommendedPets.setAdapter(adapter);


        desiredTraits = new ArrayList<>();

    }


    public void addTraits() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("traits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filterTraits(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void filterTraits(DataSnapshot dataSnapshot) {
        for (Pet liked : recommended) {
            for (DataSnapshot item : dataSnapshot.getChildren()) {

                final TraitConnection trait = item.getValue(TraitConnection.class);
                if(liked.getId().equals(trait.targetId)){
                    desiredTraits.add(trait);
                }
            }
        }
    }

    public void getMostWantedThreeTraits(){
        int count = 0;



        for(int i = 0; i < desiredTraits.size(); i++) {
            for(int j = 0; j < desiredTraits.size(); j++){

            }
        }
    }
}
