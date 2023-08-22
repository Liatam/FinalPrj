package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button captureButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private OkHttpClient okHttpClient;

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
                                sendImageToServer(base64Image);
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
        setRegisteredStatus(true);
        printSavedUserData();
        // Optionay, you can also redirect the user to a login screen
        //        // after successful registration.ll
    }

    public void sendImageToServer(String base64image) {

        MediaType mediaType = MediaType.parse("image/png"); // Or image/jpeg, depending on the image format
        RequestBody requestBody = RequestBody.create(base64image, mediaType);


        // Build the request with the image data in the request body
        Request request = new Request.Builder()
                .url("http://10.100.102.8:5000/imageToId") // Update with your server URL
                .post(requestBody) // Use .post() for a POST request
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray idVectorArray = jsonObject.optJSONArray("id_vector");

                    if (idVectorArray != null) {
                        // Convert the JSONArray to a list of floats
                        List<Float> idVector = new ArrayList<>();
                        for (int i = 0; i < idVectorArray.length(); i++) {
                            idVector.add((float) idVectorArray.getDouble(i));
                        }

                        // Now you have the idVector as a List<Float>
                        // You can use it as needed
                        handleIdVector(idVector);
                    } else {
                        // Handle the case where "id_vector" key is missing or not an array
                        // You can display an error message or take appropriate action
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    // You can display an error message or take appropriate action
                }
            }
        });
    }

    private void handleIdVector(List<Float> idVector) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Serialize the List<Float> to JSON
        Gson gson = new Gson();
        String idVectorJson = gson.toJson(idVector);

        editor.putString("idVector", idVectorJson);
        editor.apply();

        System.out.println("Id vector saved to shared preferences"+idVectorJson);
    }

    //get the vector from the sharedpreferences
    private List<Float> getIdVectorFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String idVectorJson = sharedPreferences.getString("idVector", "");

        // Deserialize the JSON string back to a List<Float>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Float>>() {
        }.getType();
        return gson.fromJson(idVectorJson, listType);
    }

    private void setRegisteredStatus(boolean isRegistered) {
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
        okHttpClient = new OkHttpClient();

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
                openCamera();
            }
        });

    }
}
