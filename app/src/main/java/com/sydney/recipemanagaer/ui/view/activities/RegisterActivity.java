package com.sydney.recipemanagaer.ui.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.UserViewModelFactory;

import org.json.JSONException;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextView textViewLogin = findViewById(R.id.textViewLogin);

        fullNameEditText = findViewById(R.id.textFullName);
        emailEditText = findViewById(R.id.textEmail);
        passwordEditText = findViewById(R.id.textPassword);
        confirmPasswordEditText = findViewById(R.id.textConfirmPassword);
        signupButton = findViewById(R.id.signup_button);

        UserRepository userRepository = new UserRepository(this);
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);


        signupButton.setOnClickListener(view -> {

            // Retrieve input from EditText fields
            String fullName = fullNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // if conditions to check the empty fields
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            //if condition to check email
            if (!isValidEmail(email)) {
                Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // if condition to check the passwords
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                userViewModel.signup(new User(fullName, email, password, confirmPassword)).observe(this, result -> {
                    if ("Signup Successful".equals(result)) {
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();  // Finish SignupActivity so user can't go back to it

                    } else {
                        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    }
                    ;
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        textViewLogin.setOnClickListener(view -> {
            // Navigate to the RegisterActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Method to check format of email
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}