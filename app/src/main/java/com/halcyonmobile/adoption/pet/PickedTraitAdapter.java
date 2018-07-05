package com.halcyonmobile.adoption.pet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.PickedTraitBinding;
import com.halcyonmobile.adoption.model.Trait;

import java.util.ArrayList;
import java.util.List;

public class PickedTraitAdapter extends RecyclerView.Adapter<PickedTraitAdapter.PickedTraitsViewHolder> {

    private List<Trait> selectedTraits = new ArrayList<>();
    public TraitAdapter.ItemOnClick onClick;

    @NonNull
    @Override
    public PickedTraitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.picked_trait_item, parent, false);
        return new PickedTraitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickedTraitsViewHolder holder, int position) {
        holder.setup(position);
    }

    @Override
    public int getItemCount() {
        return selectedTraits.size();
    }

    public void add(Trait trait) {
        int position = getItemCount();
        selectedTraits.add(trait);
        notifyItemInserted(position);
    }

    public Trait get(int position) {
        return selectedTraits.get(position);
    }

    public List<Trait> getList() {
        return selectedTraits;
    }

    public void remove(Trait trait) {
        int position = selectedTraits.indexOf(trait);
        selectedTraits.remove(trait);
        notifyItemRemoved(position);
    }

    class PickedTraitsViewHolder extends RecyclerView.ViewHolder {

        private PickedTraitBinding binding;

        PickedTraitsViewHolder(View itemView) {
            super(itemView);
            binding = PickedTraitBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClick != null) {
                        onClick.onClick(selectedTraits.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }

        void setup(int position) {
            binding.setPickedTrait(selectedTraits.get(position));
        }
    }
}
