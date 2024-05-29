package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.view.adapters.UserAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.UserViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

public class AdminFragment extends Fragment implements UserAdapter.OnUserListener {
    private UserViewModel viewModel;
    private RecyclerView usersRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UserRepository userRepository = new UserRepository(getContext());
        viewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            UserAdapter adapter = new UserAdapter(users, this);
            usersRecyclerView.setAdapter(adapter);
        });

        return view;
    }

    @Override
    public void onEditUser(User user) {
        // navigate to account setting
        Bundle args = new Bundle();
        args.putString("userId", user.getId());
        args.putString("username", user.getUsername());
        args.putString("fullname", user.getFullName());
        args.putString("email", user.getEmail());
        args.putString("bio", user.getBio());
        args.putString("password", user.getPassword()); // Be cautious with handling passwords
        args.putString("userImage", user.getProfilePic());
        args.putString("role", user.getRole());

        Fragment accountSettingFragment = new AccountSettingFragment();
        accountSettingFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, accountSettingFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteUser(String userId) {
        viewModel.deleteUser(userId).observe(getViewLifecycleOwner(), result -> {
            if ("Deleted successfully".equals(result)) {
                Toast.makeText(getContext(), "User deleted.", Toast.LENGTH_SHORT).show();
                Util.navigateToMainActivity(getContext());
            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
