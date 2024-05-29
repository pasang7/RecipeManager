package com.sydney.recipemanagaer.ui.view.adapters;

import android.content.Context;
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

public class MyRecipeAdapter extends RecyclerView.Adapter<MyRecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private LayoutInflater inflater;
    private MyRecipeActionsListener listener;

    public interface MyRecipeActionsListener {
        void onMyRecipeClick(Recipe recipe);
    }

    public MyRecipeAdapter(Context context, List<Recipe> recipes, MyRecipeActionsListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.recipes = recipes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_my_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.nameTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());

        // Use Glide to load the image asynchronously
        Glide.with(holder.itemView.getContext())
                .load(Util.getBaseURL() + "recipe/images/" + recipe.getFeaturedImgURL())
                .placeholder(R.drawable.placeholder_image_foreground) // A placeholder image to show until the real image is loaded
                .error(R.drawable.error_image) // An error image to show if the real image fails to load
                .into(holder.featuredImageView); // The target ImageView to load the image into



        holder.itemView.setOnClickListener(e -> {
            if (listener != null) {
                listener.onMyRecipeClick(recipe);
            } else {
                Log.e("Adapter", "Listener not initialized");
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView featuredImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            featuredImageView = itemView.findViewById(R.id.imageViewMyRecipe);
            nameTextView = itemView.findViewById(R.id.textViewMyRecipeName);
            descriptionTextView = itemView.findViewById(R.id.textViewMyRecipeDescription);
        }
    }
}
