package com.sydney.recipemanagaer.ui.view.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.utils.Util;

import java.util.List;

public class GenericRecipeAdapter extends RecyclerView.Adapter<GenericRecipeAdapter.ViewHolder> {
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    private List<Recipe> dataList;
    OnRecipeClickListener listener;

    public GenericRecipeAdapter(List<Recipe> dataList, OnRecipeClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (dataList != null && !dataList.isEmpty()) {
            Recipe recipe = dataList.get(position);
            holder.nameTextView.setText(recipe.getTitle());
            holder.descriptionTextView.setText(limitDescriptionLength(recipe.getDescription()));


            // Use Glide to load the image asynchronously
            Glide.with(holder.itemView.getContext())
                    .load(Util.getBaseURL() + "recipe/images/" + recipe.getFeaturedImgURL())
                    .placeholder(R.drawable.placeholder_image_foreground) // A placeholder image to show until the real image is loaded
                    .error(R.drawable.error_image) // An error image to show if the real image fails to load
                    .into(holder.featuredImageView); // The target ImageView to load the image into

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                } else {
                    Log.e("Adapter", "Listener not initialized");
                }
            });
        } else {
            // Handle the null or empty response
            // For example, display a message or a loading indicator
            holder.nameTextView.setText("No recipes available");
            holder.descriptionTextView.setText("");
            holder.featuredImageView.setImageResource(R.drawable.placeholder_image_foreground);

            Log.e("Adapter", "No data");
        }
    }

    private String limitDescriptionLength(String description) {
        int maxLength = 160; // Set your desired maximum length
        if (description.length() > maxLength) {
            return description.substring(0, maxLength) + "...";
        } else {
            return description;
        }
    }



    @Override
    public int getItemCount() {
        return dataList != null && !dataList.isEmpty() ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public ImageView featuredImageView;
//        public ImageView favoriteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            featuredImageView = itemView.findViewById(R.id.featuredImageView);
//            favoriteIcon = itemView.findViewById(R.id.imageViewFavorite);
        }
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        this.dataList = newRecipes;
        notifyDataSetChanged();
    }
}

