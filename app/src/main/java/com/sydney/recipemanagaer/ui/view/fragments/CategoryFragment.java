package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.repository.CategoryRepository;
import com.sydney.recipemanagaer.ui.viewmodel.CategoryViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.CategoryViewModelFactory;

public class CategoryFragment extends Fragment {
    private EditText addCategoryTitle;
    private EditText addCategoryDesc;
    private Button submitCategoryBtn;
    private CategoryViewModel categoryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_category, container, false);
        addCategoryTitle = view.findViewById(R.id.addCategoryTitle);
        addCategoryDesc = view.findViewById(R.id.addCategoryDesc);
        submitCategoryBtn = view.findViewById(R.id.submitCategoryBtn);

        // Initialize CategoryRepository and CategoryViewModel
        CategoryRepository categoryRepository = new CategoryRepository(getContext());
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(categoryRepository)).get(CategoryViewModel.class);


        submitCategoryBtn.setOnClickListener(v -> {
            String title = addCategoryTitle.getText().toString().trim();
            String desc = addCategoryDesc.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Category title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            Category category = new Category(title, desc);
            categoryViewModel.addCategory(category).observe(getViewLifecycleOwner(), result -> {
                if ("Category created successfully.".equals(result)) {
                    Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();

                    // Optionally, navigate back or clear fields
                    addCategoryTitle.setText("");
                    addCategoryDesc.setText("");

                    navigateToDashboard();
                } else {
                    Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void navigateToDashboard() {
        Fragment dashboardFragment = new DashboardFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment)
                .addToBackStack(null)
                .commit();
    }
}
