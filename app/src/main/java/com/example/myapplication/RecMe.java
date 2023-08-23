package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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

import com.google.gson.Gson;

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

public class RecMe extends AppCompatActivity {

    private Bitmap capturedImage;
    private List<Float> curIdVector = new ArrayList<>();
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_me);

        Button recMeButton = findViewById(R.id.recMeButton);

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
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    System.out.println("Image size: " + byteArray.length);

                    URL url = new URL("http://10.100.102.8:5000/recMe");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(byteArray);
                    os.flush();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
                        if (jsonResponse.has("cur_vector")) {
                            JSONArray idVectorArray = jsonResponse.getJSONArray("cur_vector");
                            for (int i = 0; i < idVectorArray.length(); i++) {
                                Float idVector = Float.valueOf(idVectorArray.getString(i));
                                curIdVector.add(idVector);
                            }
                            updateIdVec();
                        }
                        if (jsonResponse.has("answer")) {
                            if (jsonResponse.getString("answer").equals("True")) {
                                System.out.println("in answer true");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecMe.this, "Face match!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                System.out.println("in answer false");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RecMe.this, "Face Not found", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else if (jsonResponse.has("No face")) {
                            System.out.println("in no face");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecMe.this, "No face found", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (jsonResponse.has("No Recognition")) {
                            System.out.println("in no recognition");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RecMe.this, "No recognition", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateIdVec() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(curIdVector);
        editor.putString("id vector", json);
        editor.apply();
    }

}