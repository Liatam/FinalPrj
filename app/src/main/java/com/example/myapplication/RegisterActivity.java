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

    public boolean registerUser() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        // Create a User object and save it to SharedPreferences
        User user = new User(firstName, lastName, email, phone, false, null);
        if(validation(user)) {
            saveUser(user);
            printSavedUserData();
            return true;
        }
        System.out.println("false register user.");
        printSavedUserData();
        return false;
        // Optionay, you can also redirect the user to a login screen
        //        // after successful registration.ll
    }


    private void saveUser(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
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
        System.out.println("*****in print user data in register");
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
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
                if(registerUser()) {
                    Intent intent = new Intent(RegisterActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
        private boolean validation(User user){
            // Perform validation checks
            if (user.getFirstName().isEmpty()) {
                firstNameEditText.setError("First Name is required");
                firstNameEditText.requestFocus();
                return false;
            }

            if (user.getLastName().isEmpty()) {
                lastNameEditText.setError("Last Name is required");
                lastNameEditText.requestFocus();
                return false;
            }

            if (user.getEmail().isEmpty()) {
                emailEditText.setError("Email is required");
                emailEditText.requestFocus();
                return false;
            } else if (!isValidEmail(user.getEmail())) {
                emailEditText.setError("Invalid email address");
                emailEditText.requestFocus();
                return false;
            }

            if (user.getPhone().isEmpty()) {
                phoneEditText.setError("Phone is required");
                phoneEditText.requestFocus();
                return false;
            } else if (!user.getPhone().matches("^[0-9]{10}$")) {
                phoneEditText.setError("Invalid phone number");
                phoneEditText.requestFocus();
                return false;
            }
            return true;
        }
    private boolean isValidEmail(String email) {
        // You can implement your email validation logic here.
        // For a basic check, you can use a regular expression.
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}