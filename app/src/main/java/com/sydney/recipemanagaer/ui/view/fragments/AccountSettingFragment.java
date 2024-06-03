package com.sydney.recipemanagaer.ui.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.UserViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

public class AccountSettingFragment extends Fragment {
    private EditText editTextUserUsername, editTextUserRole, editTextUserFullName, editTextUserBio, editTextUserEmail, editTextUserPassword;
    private ImageView imageViewSelected;
    private Button buttonUpdateUser, buttonChangeProfilePic;
    private ActivityResultLauncher<String> imagePickerLauncherForUserImage;
    private String userImagePath;
    private UserViewModel viewModel;
    private String userId, userRole;
    private Button buttonChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_setting, container, false);

        UserRepository userRepository = new UserRepository(getContext());
        viewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        initViews(view);
        populateFields();
        setListeners(view);

        return view;
    }

    private void initViews(View view) {
        editTextUserUsername = view.findViewById(R.id.editTextUserUsername);
        editTextUserFullName = view.findViewById(R.id.editTextUserFullName);
        editTextUserBio = view.findViewById(R.id.editTextUserBio);
        editTextUserEmail = view.findViewById(R.id.editTextUserEmail);
        editTextUserPassword = view.findViewById(R.id.editTextPassword);
//        editTextUserRole = view.findViewById((R.id.editTextRole);

        imageViewSelected = view.findViewById(R.id.imageViewSelected);

        buttonUpdateUser = view.findViewById(R.id.btnUpdateUser);
        buttonChangeProfilePic = view.findViewById(R.id.btnChangeProfilePic);
        buttonChangePassword = view.findViewById(R.id.btnChangePassword);
    }

    private void populateFields() {
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userId");
            editTextUserUsername.setText(args.getString("username"));
            editTextUserFullName.setText(args.getString("fullname"));
            editTextUserBio.setText(args.getString("bio"));
            editTextUserEmail.setText(args.getString("email"));
            editTextUserEmail.setKeyListener(null);
//            editTextUserPassword.setText(args.getString("password"));
//            editTextUserRole.setText(args.getString("role"));
            userRole = args.getString("role");

            Glide.with(this)
                    .load(Util.getBaseURL() + "user/images/" + args.getString("userImage"))
                    .placeholder(R.drawable.placeholder_image_background)
                    .error(R.drawable.error_image)
                    .into(imageViewSelected);

        } else {
            Toast.makeText(getContext(), "No user data found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners(View view) {
        buttonUpdateUser.setOnClickListener(v -> updateUser());

        buttonChangeProfilePic.setOnClickListener(v -> {
            imagePickerLauncherForUserImage.launch("image/*");
        });

        // Setup the image picker for featured image
        imagePickerLauncherForUserImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    userImagePath = Util.getPath(getContext(), uri);
                    imageViewSelected.setImageURI(uri);
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.placeholder_image_background)
                            .error(R.drawable.error_image)
                            .into(imageViewSelected);
                }
        );

        buttonChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_change_password);

        // Set dialog to match parent dimensions
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        EditText editTextCurrentPassword = dialog.findViewById(R.id.editTextCurrentPassword);
        EditText editTextNewPassword = dialog.findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmNewPassword = dialog.findViewById(R.id.editTextConfirmNewPassword);
        Button btnSubmitChangePassword = dialog.findViewById(R.id.btnSubmitChangePassword);

        btnSubmitChangePassword.setOnClickListener(v -> {
            String currentPassword = editTextCurrentPassword.getText().toString();
            String newPassword = editTextNewPassword.getText().toString();
            String confirmNewPassword = editTextConfirmNewPassword.getText().toString();

            if (newPassword.equals(confirmNewPassword)) {
                updatePassword(currentPassword, newPassword, confirmNewPassword, dialog);
            } else {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updatePassword(String currentPassword, String newPassword, String confirmPassword, Dialog dialog) {
        viewModel.updatePassword(currentPassword, newPassword, confirmPassword).observe(getViewLifecycleOwner(), result -> {
            if (result.equals("Password Updated Successfully")) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                Util.navigateToLoginActivity(getContext());
            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUser() {
        String username = editTextUserUsername.getText().toString();
        String name = editTextUserFullName.getText().toString();
        String bio = editTextUserBio.getText().toString();
        String email = editTextUserEmail.getText().toString();

        User updatedUser = new User(userId, name, email, username, bio, userImagePath, userRole);

        viewModel.updateUser(updatedUser).observe(getViewLifecycleOwner(), result -> {
            if(result.equals("Update Successful")) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                //Util.navigateToMainActivity(getContext());
                navigateToDashboard();

            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void navigateToDashboard() {
        if (isAdded() && getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.dashboardFragment);
        }
    }
}
