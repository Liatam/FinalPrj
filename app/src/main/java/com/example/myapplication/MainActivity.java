package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views
        TextView welcomeText = findViewById(R.id.welcomeText);
        Button registerButton = findViewById(R.id.registerButton);

        // Check if the user is registered
        boolean isUserRegistered = isUserRegistered();

        if (isUserRegistered) {
            Intent cameraIntent = new Intent(MainActivity.this, RecMe.class);
            startActivity(cameraIntent);
//            System.out.println("in main registered suc///");
//            // User is registered, show welcome text and "Rec Me!" button
//            welcomeText.setText("Hello User!");
//            welcomeText.setVisibility(View.VISIBLE);
//            recMeButton.setVisibility(View.VISIBLE);

        } else {
            // User is not registered, show "Register" button
            registerButton.setVisibility(View.VISIBLE);

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start the RegisterActivity when the button is clicked
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });
        }

//        recMeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Start the CameraActivity when the button is clicked
//
//            }
//        });
    }

    // Method to check if the user is registered (simplified using SharedPreferences)
    private boolean isUserRegistered() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isRegistered", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openChooseAppsPage(View view) {
        Intent intent = new Intent(this, ChooseAppsActivity.class);
        startActivity(intent);
    }

    }

