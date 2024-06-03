package com.sydney.recipemanagaer.ui.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.UserViewModelFactory;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    private  UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserRepository userRepository = new UserRepository(this);
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);


        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewRegister = findViewById(R.id.textViewRegister);
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        buttonLogin.setOnClickListener(view -> {
            // Validate input
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Perform validation
            if (isValidCredentials(email, password)) {
                try {
                    userViewModel.login(email, password).observe(this, result -> {
                        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                        if ("Login successful".equals(result)) {
                            navigateToMainActivity();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // If credentials are invalid, show an error message
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        textViewRegister.setOnClickListener(view -> {
            // Navigate to the RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        textViewForgotPassword.setOnClickListener(view -> {
            // Navigate to the RegisterActivity
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }
    private boolean isValidCredentials(String username, String password) {
        //check if both fields are non-empty
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        // Optionally, add this to prevent the user from going back to the login screen with the back button after logging in
        finish();
    }

}