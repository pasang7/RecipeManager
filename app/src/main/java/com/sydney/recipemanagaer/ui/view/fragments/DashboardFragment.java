package com.sydney.recipemanagaer.ui.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.view.activities.LoginActivity;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.UserViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

public class DashboardFragment extends Fragment {

    private UserViewModel viewModel;
    private User userDetails;
    ImageView userProfile;
    TextView userName, userEmail, userBio;
    Button logoutButton, btnAdminDashboard, myRecipesBtn, accountSettingsBtn, btnManageRecipeCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setupUI(view);
        setupViewModel();
        observeViewModel();
        return view;
    }

    private void setupUI(View view) {
        userProfile = view.findViewById(R.id.imageViewUserProfile);
        userName = view.findViewById(R.id.textViewUserName);
        userEmail = view.findViewById(R.id.textViewUserEmail);
        userBio = view.findViewById(R.id.textViewUserBio);
        logoutButton = view.findViewById(R.id.buttonLogout);
        btnAdminDashboard = view.findViewById(R.id.btnAdminDashboard);
        btnManageRecipeCategory = view.findViewById(R.id.btnManageRecipeCategory);
        myRecipesBtn = view.findViewById(R.id.myRecipesBtn);
        accountSettingsBtn = view.findViewById(R.id.accountSettingsBtn);

        logoutButton.setOnClickListener(v -> logoutUser());
        btnAdminDashboard.setOnClickListener(v -> navigateToAdminDashboard());

        btnManageRecipeCategory.setOnClickListener(v -> navigateToManageCategory());

        myRecipesBtn.setOnClickListener(v -> navigateToMyRecipes());
        accountSettingsBtn.setOnClickListener(v -> navigateToAccountSetting());

    }

    private void setupViewModel() {
        UserRepository userRepository = new UserRepository(getContext());
        viewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userDetails = user;
                updateUI(user);
                Log.d("DashboardFragment", "User role: " + user.getRole());
                btnAdminDashboard.setVisibility("admin".equals(user.getRole()) ? View.VISIBLE : View.GONE);
                btnManageRecipeCategory.setVisibility("admin".equals(user.getRole()) ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateUI(User user) {
        userName.setText(user.getUsername());
        userEmail.setText(user.getEmail());
        userBio.setText(user.getBio());

        Glide.with(this)
                .load(Util.getBaseURL() + "user/images/" + user.getProfilePic())
                .placeholder(R.drawable.placeholder_image_background)
                .error(R.drawable.error_image)
                .into(userProfile);
    }

    private void logoutUser() {
        viewModel.clearSession();
        Toast.makeText(getContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void navigateToAdminDashboard() {
        Fragment adminFragment = new AdminFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, adminFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToManageCategory() {
        Fragment categoryFragment = new CategoryFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, categoryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToMyRecipes() {
        Fragment myRecipesFragment = new MyRecipesFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, myRecipesFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Util.userIsLoggedIn(getContext())) {
            Util.navigateToLoginActivity(getContext());
            return; // Stop further execution of this method
        }
    }

    private void navigateToAccountSetting() {
        Bundle args = new Bundle();
        args.putString("userId", userDetails.getId());
        args.putString("username", userDetails.getUsername());
        args.putString("fullname", userDetails.getFullName());
        args.putString("email", userDetails.getEmail());
        args.putString("bio", userDetails.getBio());
        args.putString("password", userDetails.getPassword()); // Be cautious with handling passwords
        args.putString("userImage", userDetails.getProfilePic());
        args.putString("role", userDetails.getRole());

        Fragment accountSettingFragment = new AccountSettingFragment();
        accountSettingFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, accountSettingFragment)
                .addToBackStack(null)
                .commit();
    }

}

