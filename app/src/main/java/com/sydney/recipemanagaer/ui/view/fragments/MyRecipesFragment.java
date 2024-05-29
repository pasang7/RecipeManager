package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;
import com.sydney.recipemanagaer.ui.view.adapters.MyRecipeAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.RecipeViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.RecipeViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

public class MyRecipesFragment extends Fragment implements MyRecipeAdapter.MyRecipeActionsListener{

    private RecyclerView recyclerView;
    private MyRecipeAdapter adapter;
    private RecipeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewMyRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecipeRepository recipeRepository = new RecipeRepository(getContext());
        viewModel = new ViewModelProvider(this, new RecipeViewModelFactory(recipeRepository)).get(RecipeViewModel.class);

        viewModel.getUserRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter = new MyRecipeAdapter(getContext(), recipes, this);
            recyclerView.setAdapter(adapter);
        });

        return view;
    }

    @Override
    public void onMyRecipeClick(Recipe recipe) {
        Util.handleViewRecipeDetail(recipe, getActivity());
    }
}