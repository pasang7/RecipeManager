package com.sydney.recipemanagaer.ui.view.adapters;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.utils.Util;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final ArrayList<Object> images;

    public ImageAdapter(ArrayList<Object> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object image = images.get(position);
        if (image instanceof String) {
            String imageName = (String) image;

            // Load image from local storage or server for createRecipe
            Glide.with(holder.imageView.getContext())
                    .load(Util.getBaseURL() + "recipe/images/" + imageName)
                    .placeholder(R.drawable.placeholder_image_background)
                    .error(R.drawable.error_image)
                    .into(holder.imageView);

        } else if (image instanceof Uri) {
            Uri imageUri = (Uri) image;
            holder.imageView.setImageURI(imageUri);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
