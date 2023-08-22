package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;


public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;

    public void registerUser() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        // Create a User object and save it to SharedPreferences
        User user = new User(firstName, lastName, email, phone);
        saveUser(user);
        setRegisteredStatus(true);
        printSavedUserData();
        // Optionay, you can also redirect the user to a login screen
        //        // after successful registration.ll
    }


    public void setRegisteredStatus(boolean isRegistered) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRegistered", isRegistered);
        editor.apply();
        System.out.println("User registration status updated to: " + isRegistered);
    }

    private void saveUser(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = serializeUser(user);
        editor.putString("user", userJson);
        editor.apply();
        System.out.println("User data saved.");
    }

    private String serializeUser(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    private void printSavedUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUserJson = sharedPreferences.getString("user", null);

        if (savedUserJson != null) {
            // Deserialize the User object from JSON (or any other format)
            User savedUser = deserializeUser(savedUserJson);

            if (savedUser != null) {
                // Log the saved user information
                System.out.println("SavedUserData First Name: " + savedUser.getFirstName());
                System.out.println("SavedUserData Last Name: " + savedUser.getLastName());
                System.out.println("SavedUserData Email: " + savedUser.getEmail());
                System.out.println("SavedUserData Phone: " + savedUser.getPhone());
            }
        } else {
            // User data not found in SharedPreferences
            System.out.println("SavedUserData User data not found.");
        }
    }

    private User deserializeUser(String savedUserJson) {
        Gson gson = new Gson();
        return gson.fromJson(savedUserJson, User.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);


        Button registerSubmit = findViewById(R.id.registerSubmit);
        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button captureButton = findViewById(R.id.buttonFirst);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openCamera();
                Intent intent = new Intent(RegisterActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }
}