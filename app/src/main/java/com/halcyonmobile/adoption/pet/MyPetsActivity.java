package com.halcyonmobile.adoption.pet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.MyPetsBinding;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.profile.PetAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.halcyonmobile.adoption.profile.AddPetActivity.TABLE_PETS;

public class MyPetsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private List<Pet> myPets;
    private MyPetsBinding binding;
    private String currentUserId;
    private PetAdapter adapter;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseWishlist = database.child("wishlists");
    private DatabaseReference databasePets = database.child("pets");

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MyPetsActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_pets);

        binding.myPetsToolbar.inflateMenu(R.menu.toolbar_menu);
        binding.myPetsToolbar.setTitle("My Pets");
        setSupportActionBar(binding.myPetsToolbar);

        myPets = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child(TABLE_PETS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        binding.myPetsRecyclerView.setHasFixedSize(true);
        binding.myPetsRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new PetAdapter(myPets, new PetAdapter.OnClick() {
            @Override
            public void setItemClickListener(Pet pet, int position) {
                Intent intent = new Intent(MyPetsActivity.this, PetDetailActivity.class);
                intent.putExtra("id", pet.getId());
                startActivity(intent);
            }
        }, new PetAdapter.OnLongClick() {
            @Override
            public void setItemLongClickListener(final Pet pet, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPetsActivity.this)
                        .setTitle("Select action")
                        .setItems(R.array.actions_pets, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(MyPetsActivity.this, EditPetActivity.class);
                                        intent.putExtra("id", pet.getId());
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(MyPetsActivity.this)
                                                .setTitle("Remove").setMessage("Are you sure you want to remove " + pet.getId() + "?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        databaseWishlist.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot data) {
                                                                for (DataSnapshot child : data.getChildren()) {
                                                                    Wishlist wishlist = child.getValue(Wishlist.class);
                                                                    if (wishlist.getPetId().equals(pet.getId())) {
                                                                        databaseWishlist.child(wishlist.getId()).removeValue();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        databasePets.child(pet.getId()).removeValue();
                                                    }
                                                }).setNegativeButton("No", null);
                                        builderConfirm.show();
                                        break;
                                }
                            }
                        }).setNeutralButton("Cancel", null);
                builder.show();
            }
        });
        binding.myPetsRecyclerView.setAdapter(adapter);

        binding.myPetsToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.search) {
                    SearchPetActivity.getStartIntent(MyPetsActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void showData(DataSnapshot snapshot) {
        for (DataSnapshot item : snapshot.getChildren()) {
            final Pet pet = item.getValue(Pet.class);
            assert pet != null;
            if (pet.getUserId().equals(currentUserId)) {
                adapter.add(pet);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
