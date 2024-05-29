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

import org.json.JSONException;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private LayoutInflater inflater;
    private FavoriteActionsListener listener;

    public interface FavoriteActionsListener {
        void onRemoveFavorite(Recipe recipe) throws JSONException;
        void onFavoriteRecipeClick(Recipe recipe);
    }

    public FavoriteAdapter(Context context, List<Recipe> recipes, FavoriteActionsListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.recipes = recipes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.nameTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_fill);

        // Use Glide to load the image asynchronously
        Glide.with(holder.itemView.getContext())
                .load(Util.getBaseURL() + "recipe/images/" + recipe.getFeaturedImgURL())
                .placeholder(R.drawable.placeholder_image_foreground) // A placeholder image to show until the real image is loaded
                .error(R.drawable.error_image) // An error image to show if the real image fails to load
                .into(holder.featuredImageView); // The target ImageView to load the image into


        holder.favoriteIcon.setOnClickListener(v -> {
            try {
                listener.onRemoveFavorite(recipe);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, recipes.size());
                recipes.remove(position); // Remove the item from the list
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });

        holder.itemView.setOnClickListener(e -> {
            if (listener != null) {
                listener.onFavoriteRecipeClick(recipe);
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
        ImageView favoriteIcon;
        ImageView featuredImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            featuredImageView = itemView.findViewById(R.id.imageViewFavorite);
            nameTextView = itemView.findViewById(R.id.textViewRecipeName);
            descriptionTextView = itemView.findViewById(R.id.textViewRecipeDescription);
            favoriteIcon = itemView.findViewById(R.id.buttonRemoveFavorite);
        }
    }
}
