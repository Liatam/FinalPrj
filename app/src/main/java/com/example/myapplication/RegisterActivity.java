package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private Button captureButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");  //this is the image

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);  //coded image
                                CameraActivity.sendImageToServer(base64Image);
                                System.out.println("Image sent to server");
                            }
                        }
                    });

    public void registerUser() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        // Create a User object and save it to SharedPreferences
        User user = new User(firstName, lastName, email, phone);
        saveUser(user);
        printSavedUserData();
        // Optionally, you can also redirect the user to a login screen
        // after successful registration.
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

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        registerUser();
        Button captureButton = findViewById(R.id.buttonFirst);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }
}
