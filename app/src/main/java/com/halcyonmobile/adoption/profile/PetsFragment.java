package com.halcyonmobile.adoption.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.PetsFragmentBinding;
import com.halcyonmobile.adoption.model.Pet;
import com.halcyonmobile.adoption.pet.CardAdapter;
import com.halcyonmobile.adoption.pet.FilterBySpeciesActivity;
import com.halcyonmobile.adoption.pet.PetDetailActivity;
import com.halcyonmobile.adoption.pet.RecommendedActivity;
import com.halcyonmobile.adoption.pet.SearchPetActivity;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.halcyonmobile.adoption.profile.AddPetActivity.TABLE_PETS;


public class PetsFragment extends Fragment implements CardStackView.CardEventListener {

    private List<Pet> petsToAdopt;
    private PetsFragmentBinding binding;
    private FirebaseDatabase firebaseDatabase;
    public static List<Pet> recommmendedPets = new ArrayList<>();
    private CardAdapter cardAdapter;
    private int swipedIndex = 0;

    public PetsFragment() {
        // Required empty public constructor
        // But this is by default...
    }

    public static PetsFragment newInstance() {

        return new PetsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        petsToAdopt = new ArrayList<>();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pets, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        cardAdapter = new CardAdapter(Objects.requireNonNull(getContext()));
        // TODO go to the XML file and set visibility to visible (it is set on gone)
        binding.cardStackPets.setAdapter(cardAdapter);
        binding.cardStackPets.setCardEventListener(this);
        swipedIndex = binding.cardStackPets.getTopIndex();
        reference.child(TABLE_PETS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setHasOptionsMenu(true);
        ((ProfileActivity)getActivity()).setSupportActionBar(binding.searchToolbar);
        binding.searchToolbar.setTitle("Pets");
        binding.searchToolbar.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));
        binding.petsRecyclerView.setHasFixedSize(true);
        binding.petsRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.petsRecyclerView.setAdapter(new PetAdapter(petsToAdopt, new PetAdapter.OnClick() {
            @Override
            public void setItemClickListener(Pet pet, int position) {
                Intent intent = new Intent(getContext(), PetDetailActivity.class);
                intent.putExtra("id", pet.getId());
                startActivity(intent);
            }
        }));

        binding.addPetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPetActivity.getStartIntent(v.getContext());
            }
        });

        return binding.getRoot();
    }

    private void showData(DataSnapshot snapshot){
        petsToAdopt.clear();
        for(DataSnapshot item: snapshot.getChildren()) {
            final Pet pet = item.getValue(Pet.class);

            assert pet != null;
            petsToAdopt.add(pet);
            binding.petsRecyclerView.getAdapter().notifyDataSetChanged();
            cardAdapter.add(pet);
            cardAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCardDragging(float percentX, float percentY) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.big_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(SearchPetActivity.getStartIntent(getContext()));
                return true;
            case R.id.filter:
                startActivity(FilterBySpeciesActivity.getStartIntent(getContext()));
                return true;
            case R.id.recommended:
                startActivity(RecommendedActivity.Companion.getStartIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCardSwiped(SwipeDirection direction) {
        switch (direction) {
            case Left:
                // TODO get out of stack
                break;
            case Right:
                Intent intent = new Intent(getContext(), PetDetailActivity.class);
                intent.putExtra("id", cardAdapter.getItem(swipedIndex).getId());
                recommmendedPets.add(cardAdapter.getItem(swipedIndex));
                startActivity(intent);
                break;
        }
        swipedIndex = binding.cardStackPets.getTopIndex();
    }

    @Override
    public void onCardReversed() {

    }

    @Override
    public void onCardMovedToOrigin() {

    }

    @Override
    public void onCardClicked(int index) {

    }

}
