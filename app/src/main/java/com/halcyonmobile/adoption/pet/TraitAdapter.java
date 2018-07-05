package com.halcyonmobile.adoption.pet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.TraitBinding;
import com.halcyonmobile.adoption.model.Trait;

import java.util.ArrayList;
import java.util.List;

public class TraitAdapter extends RecyclerView.Adapter<TraitAdapter.TraitViewHolder> {

    private List<Trait> traits;
    public ItemOnClick onClick;

    public TraitAdapter(List<Trait> traits) {
        this.traits = traits;
    }

    @NonNull
    @Override
    public TraitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trait_item, parent, false);
        return new TraitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraitViewHolder holder, int position) {
        holder.setup(position);
    }

    @Override
    public int getItemCount() {
        return traits.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void removeTrait(int position) {
        traits.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Trait trait) {
        int position = getItemCount();
        traits.add(trait);
        notifyItemInserted(position);
    }

    public Trait get(int position) {
        return traits.get(position);
    }

    public void remove(Trait trait) {
        int position = traits.indexOf(trait);
        traits.remove(trait);
        notifyItemRemoved(position);
    }

    class TraitViewHolder extends RecyclerView.ViewHolder {
        TraitBinding binding;

        TraitViewHolder(View itemView) {
            super(itemView);
            binding = TraitBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClick != null) {
                        onClick.onClick(traits.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }

        void setup(int position) {
            binding.setTrait(traits.get(position));
        }
    }

    public interface ItemOnClick {

        public void onClick(Trait trait, int position);

    }

}