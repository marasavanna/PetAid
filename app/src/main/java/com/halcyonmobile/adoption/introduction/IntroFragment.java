package com.halcyonmobile.adoption.introduction;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halcyonmobile.adoption.R;
import com.halcyonmobile.adoption.databinding.IntroItemBinding;
import com.halcyonmobile.adoption.model.Intro;


public class IntroFragment extends Fragment {

    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";
    private String description;
    private String image;

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(String textDescription, String imageUrl) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putString(DESCRIPTION, textDescription);
        args.putString(IMAGE, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.description = getArguments().getString(DESCRIPTION);
            this.image = getArguments().getString(IMAGE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        IntroItemBinding binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false);
        View view = binding.getRoot();
        binding.setIntroItem(new Intro(image, description));
        return view;
    }

}
