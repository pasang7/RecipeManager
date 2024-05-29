package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.repository.CategoryRepository;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;
import com.sydney.recipemanagaer.ui.view.adapters.ImageAdapter;
import com.sydney.recipemanagaer.ui.view.adapters.IngredientAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.CategoryViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.RecipeViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.CategoryViewModelFactory;
import com.sydney.recipemanagaer.ui.viewmodel.factory.RecipeViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateRecipeFragment extends Fragment {
    private EditText editTextRecipeName, editTextFoodType, editTextRecipeDescription, editTextInstructions, editTextCookingTime;
    private ImageView imageViewSelected;
    private ActivityResultLauncher<String> imagePickerLauncherForFeaturedImage;
    private ActivityResultLauncher<String> imagePickerLauncherForImages;
    private Button buttonUpdateRecipe, addImageButton, addFeaturedImage;
    private AutoCompleteTextView autoCompleteTextView;
    private ChipGroup chipGroup;
    private RecipeViewModel viewModel;
    private List<String> ingredients;
    private IngredientAdapter adapter;
    private String recipeId, featuredImagePath;
    private ArrayList<Object> images = new ArrayList<>();
    private ArrayList<String> imagesPaths = new ArrayList<>();
    private ImageAdapter imgAdapter;

    private RecyclerView imagesRecyclerView;
    private Spinner spinnerRecipeType;
    private List<Category> categories;
    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_recipe, container, false);
        RecipeRepository recipeRepository = new RecipeRepository(getContext());
        viewModel = new ViewModelProvider(this, new RecipeViewModelFactory(recipeRepository)).get(RecipeViewModel.class);

        // Initialize CategoryRepository and CategoryViewModel
        CategoryRepository categoryRepository = new CategoryRepository(getContext());
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(categoryRepository)).get(CategoryViewModel.class);


        initViews(view);
        populateFields();
        setListeners(view);

        return view;
    }

    private void initViews(View view) {
        TextView label = view.findViewById(R.id.textLayoutLabel);
        label.setText("Update Recipe");

//        editTextFoodType = view.findViewById(R.id.editTextRecipeType);

        editTextRecipeName = view.findViewById(R.id.editTextRecipeName);
        editTextRecipeDescription = view.findViewById(R.id.editTextRecipeDescription);
        editTextInstructions = view.findViewById(R.id.editTextInstructions);
        editTextCookingTime = view.findViewById(R.id.editTextCookingTime);
        imageViewSelected = view.findViewById(R.id.imageViewSelected);
        buttonUpdateRecipe = view.findViewById(R.id.buttonSubmitRecipe);
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewIngredients);
        chipGroup = view.findViewById(R.id.chipGroupSelectedIngredients);

        // Initialize the list of ingredients and the adapter for AutoCompleteTextView
        ingredients = new ArrayList<>(Arrays.asList(
                "Flour", "Sugar", "Eggs", "Butter", "Milk", "Salt", "Tomatoes", "Onions",
                "Carrots", "Bell peppers", "Garlic", "Broccoli", "Spinach", "Potatoes",
                "Apples", "Bananas", "Oranges", "Strawberries", "Grapes", "Lemons",
                "Pineapple", "Chicken", "Beef", "Pork", "Lamb", "Turkey", "Salmon",
                "Tuna", "Shrimp", "Cod", "Crab", "Lobster", "Cheese", "Yogurt", "Cream",
                "Rice", "Wheat flour", "Oats", "Quinoa", "Lentils", "Chickpeas",
                "Black beans", "Almonds", "Walnuts", "Peanuts", "Sunflower seeds",
                "Flaxseeds", "Basil", "Oregano", "Thyme", "Rosemary", "Cumin", "Paprika",
                "Black pepper", "Olive oil", "Canola oil", "Coconut oil", "Vegetable oil",
                "Ghee", "Honey", "Maple syrup", "Molasses", "Kale", "Celery", "Zucchini",
                "Eggplant", "Peas", "Cauliflower", "Asparagus", "Artichoke", "Mushrooms",
                "Pomegranate", "Blueberries", "Raspberries", "Mango", "Avocado", "Peach",
                "Cabbage", "Lettuce", "Cucumber", "Corn", "Radishes", "Mint", "Cilantro",
                "Parsley", "Fennel", "Chili powder", "Turmeric", "Cinnamon", "Nutmeg",
                "Cardamom", "Vanilla", "Sesame seeds", "Pistachios", "Hazelnuts", "Pumpkin seeds",
                "Macadamia nuts"
        ));

        adapter = new IngredientAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, ingredients);
        autoCompleteTextView.setAdapter(adapter);

        addFeaturedImage = view.findViewById(R.id.buttonSelectImage);
        addImageButton = view.findViewById(R.id.addImageButton);

        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView);
        imgAdapter = new ImageAdapter(images);
        imagesRecyclerView.setAdapter(imgAdapter);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        spinnerRecipeType = view.findViewById(R.id.spinnerRecipeType);

        loadCategories();

    }

    private void loadCategories() {
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), results -> {
            this.categories = results;
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRecipeType.setAdapter(adapter);

            // Set the selected category if there is a categoryId
            String categoryJsonString = getArguments() != null ? getArguments().getString("category") : null;
            if (categoryJsonString != null) {
                try {
                    JSONObject categoryJson = new JSONObject(categoryJsonString);
                    String categoryId = categoryJson.getString("_id");
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).get_id().equals(categoryId)) {
                            spinnerRecipeType.setSelection(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void populateFields() {
        Bundle args = getArguments();
        if (args != null) {
            recipeId = args.getString(("recipeId"));
            editTextRecipeName.setText(args.getString("title"));
            editTextRecipeDescription.setText(args.getString("description"));
            editTextInstructions.setText(args.getString("instructions"));
            editTextCookingTime.setText(args.getString("cookingTime"));
//            editTextFoodType.setText(args.getString("foodType"));


            Glide.with(this)
                    .load(Util.getBaseURL() + "recipe/images/" + args.getString("imageUrl"))
                    .placeholder(R.drawable.placeholder_image_background)
                    .error(R.drawable.error_image)
                    .into(imageViewSelected);

            // Split the ingredients string into an array and load as chips
            String ingredients = args.getString("ingredients");
            if (ingredients != null && !ingredients.isEmpty()) {
                String[] ingredientArray = ingredients.split(", ");
                loadIngredientsAsChips(Arrays.asList(ingredientArray));
            }
            // Load existing images
            List<String> imageURLs = args.getStringArrayList("imagesUrl");
            if (imageURLs != null && !imageURLs.isEmpty()) {
                for (String url : imageURLs) {
                    images.add(url);
                }
                imgAdapter.notifyDataSetChanged();

            }


        }
    }

    private void loadIngredientsAsChips(List<String> ingredients) {
        for (String ingredient : ingredients) {
            addChipToGroup(ingredient);
        }
    }

    private void addChipToGroup(String ingredient) {
        Chip chip = new Chip(getContext());
        chip.setText(ingredient);
        chip.setChipIconResource(R.drawable.ic_check);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        chipGroup.addView(chip);
    }

    private void setListeners(View view) {
        addFeaturedImage.setOnClickListener(v -> {
            imagePickerLauncherForFeaturedImage.launch("image/*");
        });

        addImageButton.setOnClickListener(v -> {
            imagePickerLauncherForImages.launch("image/*");
        });

        // Setup the image picker for featured image
        imagePickerLauncherForFeaturedImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    featuredImagePath = Util.getPath(getContext(), uri);
                    imageViewSelected.setImageURI(uri);
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.placeholder_image_background)
                            .error(R.drawable.error_image)
                            .into(imageViewSelected);
                }
        );

        // Setup the image picker for images
        imagePickerLauncherForImages = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    imagesPaths.add(Util.getPath(getContext(), uri));
                    if (images.size() < 5) {
                        images.add(uri);
                        imgAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "You can only upload up to 5 images.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selection = parent.getItemAtPosition(position).toString();
            addChipToGroup(selection);
            autoCompleteTextView.setText("");
        });

        buttonUpdateRecipe.setOnClickListener(v -> updateRecipe());
    }

    private void updateRecipe() {
        String title = editTextRecipeName.getText().toString();
        String description = editTextRecipeDescription.getText().toString();
        String instructions = editTextInstructions.getText().toString();
        String cookingTimeStr = editTextCookingTime.getText().toString();
        Category selectedCategory = (Category) spinnerRecipeType.getSelectedItem();

        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            ingredients.add(chip.getText().toString());
        }
        int cookingTime;
        try {
            cookingTime = Integer.parseInt(cookingTimeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid cooking time. Please enter a number.", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe updatedRecipe = new Recipe(recipeId, title, description, ingredients, instructions, cookingTime);

        updatedRecipe.setFeaturedImage(featuredImagePath);
        updatedRecipe.setImages(imagesPaths);


        if (selectedCategory != null) {
            updatedRecipe.setCategoryId(selectedCategory.get_id());
        }


        viewModel.updateRecipe(updatedRecipe).observe(getViewLifecycleOwner(), result -> {
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            Util.navigateToMainActivity(getContext());
        });
    }
}
