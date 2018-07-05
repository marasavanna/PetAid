package com.halcyonmobile.adoption;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageBindingAdapter {
    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView imageView, String url) {
        if (url != null) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        }else {
            Glide.with(imageView.getContext())
                    .load(R.drawable.default_user)
                    .into(imageView);
        }
    }
}
