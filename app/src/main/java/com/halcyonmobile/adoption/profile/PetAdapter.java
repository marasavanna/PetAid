package com.halcyonmobile.adoption.profile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.PetItemBinding;
import com.halcyonmobile.adoption.model.Pet;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> pets;
    private OnClick onClickItem;
    private OnLongClick onLongClickItem;

    PetAdapter(List<Pet> pets) {
        this.pets = pets;
    }

    public PetAdapter(List<Pet> pets, OnClick onClick) {
        this.pets = pets;
        this.onClickItem = onClick;
    }

    public PetAdapter(List<Pet> pets, OnClick onClick, OnLongClick onLongClick) {
        this.pets = pets;
        this.onClickItem = onClick;
        this.onLongClickItem = onLongClick;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.pet_item_layout, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        holder.setup(position);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public interface OnClick {

        void setItemClickListener(Pet pet, int position);

    }

    public void add(Pet pet) {
        int pos = getItemCount();
        pets.add(pet);
        notifyItemInserted(pos);
    }

    public void remove(Pet pet) {
        int pos = pets.indexOf(pet);
        pets.remove(pet);
        notifyItemRemoved(pos);
    }

    public void modify(int position, Pet pet) {
        pets.set(position, pet);
        notifyItemChanged(position);
    }

    public interface OnLongClick {

        void setItemLongClickListener(Pet pet, int position);

    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        private PetItemBinding petItemBinding;

        PetViewHolder(View itemView) {
            super(itemView);
            petItemBinding = PetItemBinding.bind(itemView);
        }

        void setup(final int position) {
            petItemBinding.setPet(pets.get(position));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem.setItemClickListener(pets.get(position), position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickItem.setItemLongClickListener(pets.get(position), position);
                    return true;
                }
            });
        }
    }

}
