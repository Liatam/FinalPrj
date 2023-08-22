package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.ByteArrayOutputStream;
public class CameraActivity extends AppCompatActivity {

//    private static final OkHttpClient client = new OkHttpClient.Builder()
////            .connectTimeout(50, TimeUnit.SECONDS)
////            .readTimeout(50, TimeUnit.SECONDS)
//            .build();

//    public static void sendImageToServer(String base64Image) {
//        String url = "https://10.100.102.8:5000/imageToId/<base64Image>";
//        MediaType mediaType = MediaType.parse("image/png"); // Or image/jpeg, depending on the image format
//        RequestBody requestBody = RequestBody.create(base64Image, mediaType);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            System.out.println("in try");
//            if (response.isSuccessful()) {
//                assert response.body() != null;
//                String responseString = response.body().string();
//                System.out.println("*******");
//                System.out.println(responseString);
//
//                //לפתוח עוד אקטיבי ולראות את התשובה אחרי שחוזרת תגובה מהשרת על התמונה ששלחנו
////                // Start the WordListActivity and pass the decoded response as an extra
////                Intent intent = new Intent(context, WordListActivity.class);
////                intent.putExtra("wordList", decodedResponse);
////                context.startActivity(intent);
//
//            } else {
//                System.out.println("Unsuccessful response: " + response.message());
//                // Handle unsuccessful response here (e.g., log or show an error message)
//                Log.e("ServerConnectBase64", "Unsuccessful response: " + response.code() + " " + response.message());
//            }
//        } catch (IOException e) {
//            System.out.println("Exception caught: " + e.getMessage());
//            // Handle IO Exception here (e.g., log or show an error message)
//            e.printStackTrace();
//        }
//    }
}

