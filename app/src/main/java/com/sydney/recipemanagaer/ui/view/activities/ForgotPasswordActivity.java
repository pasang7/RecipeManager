package com.sydney.recipemanagaer.ui.view.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.networking.retrofit.RetrofitClient;
import com.sydney.recipemanagaer.networking.retrofit.RetrofitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button submitButton;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        submitButton = findViewById(R.id.submitButton);
        retrofitService = new RetrofitService(this);

        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!email.isEmpty()) {
                requestPasswordReset(email);
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPasswordReset(String email) {

        retrofitService.forgotPassword(email, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error sending password reset link", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
