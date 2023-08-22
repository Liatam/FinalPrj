package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private ImageView photoImageView;
    private Bitmap capturedImage;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_REQUEST_CODE = 1;
    private List<Float> idVectors = new ArrayList<>();
    private ProgressBar progressBar;
    private int uploadedPictureCount = 0;
    private Button finishButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        photoImageView = findViewById(R.id.photoImageView);
        Button captureButton = findViewById(R.id.captureButton);
        progressBar = findViewById(R.id.progressBar);
        Button finishButton = findViewById(R.id.finishButton);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadedPictureCount == 5) {
                    setRegisteredStatus(true);
                    // Navigate back to the main activity
                    Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                } else {
                    // Show a toast message
                    Toast.makeText(CameraActivity.this, "Please take 5 pictures", Toast.LENGTH_SHORT).show();
                    // Handle the case when the button is clicked but not enabled (optional)
                    // You can show a message or take any other action here
                }

            }
        });


        // Initialize the cameraLauncher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Photo capture was successful, handle the result
                Bundle extras = result.getData().getExtras();
                if (extras != null) {
                    capturedImage = (Bitmap) extras.get("data");
                    photoImageView.setImageBitmap(capturedImage);
                    uploadPhoto(capturedImage);
                }
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Capture photo clicked");
                // Check if the camera permission is not granted
                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    System.out.println("Requesting permission");
                } else {
                    // Permission is already granted, you can open the camera intent here
                    System.out.println("Permission already granted");
                    capturePhoto();
                }
            }
        });

    }
    public void setRegisteredStatus(boolean isRegistered) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRegistered", isRegistered);
        editor.apply();
        System.out.println("User registration status updated to: " + isRegistered);
    }
    private void capturePhoto() {
        System.out.println("Capture photo");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // Calculate the maximum allowed size for the image in bytes (e.g., 2MB)
    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024; // 2MB

    private void uploadPhoto(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // Save the Bitmap as a JPEG image without compression
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    System.out.println("Image size: " + byteArray.length);

                    URL url = new URL("http://10.100.102.8:5000/imageToId");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(byteArray);
                    os.flush();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Handle success
                        System.out.println("*********Success");
                        // Parse the JSON response
                        InputStream inputStream = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        System.out.println("Response: " + response.toString());
                            System.out.println("in else Face found");
                            // Parse the JSON response into a JSONObject
                            JSONObject jsonResponse = new JSONObject(response.toString());

                            // Check if the response contains a vector
                            if (jsonResponse.has("id_vector")) {
                                System.out.println("in if has id_vector");
                                JSONArray idVectorArray = jsonResponse.getJSONArray("id_vector");
                                System.out.println("in if idVectors not null");
                                // Compute the average between idVectors and the response vector
                                List<Float> averageVector = new ArrayList<>();
                                System.out.println("idVectorArray" + idVectorArray);
                                //first image
                                if(idVectors.size()==0)
                                {
                                    for (int i = 0; i < idVectorArray.length(); i++) {
                                        Float idVector = Float.valueOf(idVectorArray.getString(i));
                                        idVectors.add(idVector);
                                    }
                                }
                                else {
                                    for (int i = 0; i < idVectorArray.length(); i++) {
                                        float oldValue = idVectors.get(i);
                                        float responseValue = (float) idVectorArray.getDouble(i);
                                        averageVector.add((oldValue + responseValue) / 2);
                                    }
                                }
                                // Update idVectors with the average vector
                                idVectors = averageVector;
                                System.out.println("Updated idVectors: " + idVectors);

                                // Increment the uploaded picture count
                                uploadedPictureCount++;

                                // Update the progress bar
                                updateProgressBar();


                            }
                        if(jsonResponse.has("No face")) {
                            System.out.println("No face found. Try again!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No face found. Try again!", Toast.LENGTH_LONG).show();
                                    System.out.println("after toast");
                                }
                            });
                            }
                    } else {
                        // Handle error
                        System.out.println("Error");
                    }
                } catch (IOException e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
    private void updateProgressBar() {
        // Calculate the progress as a percentage
        int progress = (uploadedPictureCount * 100) / 5;

        // Update the progress bar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);

                // Check if all pictures are uploaded
                if (uploadedPictureCount == 5) {
                    progressBar.setVisibility(View.INVISIBLE);
                    // Reset the uploaded picture count for the next batch of uploads
                   // uploadedPictureCount = 0;
                }
            }
        });
    }

}
