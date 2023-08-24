package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecMeActivity extends AppCompatActivity {

    private Bitmap capturedImage;
    private ActivityResultLauncher<Intent> cameraLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_me);

        Button recMeButton = findViewById(R.id.recMeButton);
        printSavedUserData();
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Photo capture was successful, handle the result
                Bundle extras = result.getData().getExtras();
                if (extras != null) {
                    capturedImage = (Bitmap) extras.get("data");
                    uploadPhoto(capturedImage);
                }
            }
        });

        recMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(intent);
            }
        });
    }

    private void uploadPhoto(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // Save the Bitmap as a JPEG image without compression
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    // Create an OkHttpClient instance
                    OkHttpClient client = new OkHttpClient();

                    // URL of your Flask server endpoint
                    String serverUrl = "http://10.100.102.8:5000/recMe";

                    // Convert the image byte array to RequestBody
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

                    // Create a list of floats
                    List<Float> floatList = new ArrayList<>();
                    floatList=getIdVector();

                    // Build the request body
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"image.jpg\""),
                                    imageBody)
                            .addFormDataPart("features_vec", floatList.toString())
                            .build();

                    // Create the POST request
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // Get the response body as an InputStream
                        InputStream responseBodyStream = response.body().byteStream();

                        // Create a BufferedReader to read the response line by line
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBodyStream));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            // Parse the JSON response into a JSONObject
                            JSONObject jsonResponse = new JSONObject(line);

                            // Check if the response contains a vector
                            if (jsonResponse.has("cur_vector")) {
                                JSONArray idVectorArray = jsonResponse.getJSONArray("cur_vector");
                                List<Float> curIdVector = new ArrayList<>();
                                for (int i = 0; i < idVectorArray.length(); i++) {
                                    Float idVector = Float.valueOf(idVectorArray.getString(i));
                                    curIdVector.add(idVector);
                                }
                                updateIdVec(curIdVector);
                            }

                            // Check if the response contains an "answer" field
                            if (jsonResponse.has("answer")) {
                                if (jsonResponse.getString("answer").equals("True")) {
                                    System.out.println("in answer true");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RecMeActivity.this, "Face match!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

//                    // Create a JSONObject with your list of floats
//                    JSONObject floatList = new JSONObject();
//                    floatList.put("features_vec", getIdVector());
//
//                    // Convert the JSON object to a string
//                    String floatListString = floatList.toString();
//
//                    // Create a URL object with your server's endpoint
//                    URL url = new URL("http://10.100.102.8:5000/recMe");
//
//                    // Open a connection to the server
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoOutput(true);
//                    connection.setRequestMethod("POST");
//                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----Boundary");
//
//                    // Create the multipart data
//                    String boundary = "----Boundary";
//                    String CRLF = "\r\n";
//                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
//
//                    // Add the image
//                    outputStream.writeBytes("--" + boundary + CRLF);
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"" + CRLF);
//                    outputStream.writeBytes("Content-Type: image/jpeg" + CRLF);
//                    outputStream.writeBytes(CRLF);
//                    outputStream.write(byteArray);
//                    outputStream.writeBytes(CRLF);
//
//                    // Add the list of floats
//                    outputStream.writeBytes("--" + boundary + CRLF);
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"floatList\"" + CRLF);
//                    outputStream.writeBytes("Content-Type: application/json" + CRLF);
//                    outputStream.writeBytes(CRLF);
//                    outputStream.writeBytes(floatListString);
//                    outputStream.writeBytes(CRLF);
//
//                    outputStream.writeBytes("--" + boundary + "--" + CRLF);
//                    outputStream.flush();
//                    outputStream.close();
//*******************
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        // Parse the JSON response
//                        InputStream inputStream = connection.getInputStream(); // Corrected variable name
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        reader.close();
//                        System.out.println("Response: " + response.toString());
//                        // Parse the JSON response into a JSONObject
//                        JSONObject jsonResponse = new JSONObject(response.toString());

                            // Check if the response contains a vector
                            //**************
//                        if (jsonResponse.has("cur_vector")) {
//                            JSONArray idVectorArray = jsonResponse.getJSONArray("cur_vector");
//                            List<Float> curIdVector = new ArrayList<>();
//                            for (int i = 0; i < idVectorArray.length(); i++) {
//                                Float idVector = Float.valueOf(idVectorArray.getString(i));
//                                curIdVector.add(idVector);
//                            }
//                            updateIdVec(curIdVector);
//                        }
//                        if (jsonResponse.has("answer")) {
//                            if (jsonResponse.getString("answer").equals("True")) {
//                                System.out.println("in answer true");
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(RecMeActivity.this, "Face match!", Toast.LENGTH_LONG).show();
//                                    }
//                                });
                            else {
                                System.out.println("in answer false");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecMeActivity.this, "Face Not found", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            if (jsonResponse.has("No face")) {
                                System.out.println("in no face");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecMeActivity.this, "No face found", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else if (jsonResponse.has("No Recognition")) {
                                System.out.println("in no recognition");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecMeActivity.this, "No recognition", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException | JSONException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private List<Float> getIdVector(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String savedIdVectorJson = sharedPreferences.getString("idVector", null);

        if (savedIdVectorJson != null) {
            // Deserialize the idVector list from JSON
            Gson gson = new Gson();
            List<Float> savedIdVector = gson.fromJson(savedIdVectorJson, new TypeToken<List<Float>>() {
            }.getType());
            System.out.println("Saved idVector in camera getidvec: " + savedIdVector);
            return savedIdVector;
        }
        return null;
    }

    private void updateIdVec(List<Float> curIdVector) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(curIdVector);
        editor.putString("id vector", json);
        editor.apply();
    }

    private void printSavedUserData() {
        System.out.println("in Print saved user data in recme");
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
                System.out.println("SavedUserData isregistered: " + savedUser.isRegistered());
                System.out.println("SavedUserData id vector: " + savedUser.getIdVector());
            }
        } else {
            // User data not found in SharedPreferences
            System.out.println("SavedUserData User data not found.");
        }
    }

    private User deserializeUser(String userJson) {
        Gson gson = new Gson();
        return gson.fromJson(userJson, User.class);
    }

}