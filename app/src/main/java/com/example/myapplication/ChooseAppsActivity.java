package com.example.myapplication;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChooseAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_apps);

        ListView appListView = findViewById(R.id.appListView);
        PackageManager packageManager = getPackageManager();

        // Get a list of installed apps
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Create an adapter for the ListView
        AppAdapter adapter = new AppAdapter(this, apps);
        appListView.setAdapter(adapter);

        // Handle user selections
        appListView.setOnItemClickListener((parent, view, position, id) -> {
            // Toggle the lock/unlock status of the selected app
            ApplicationInfo selectedApp = (ApplicationInfo) parent.getItemAtPosition(position);
            boolean isLocked = isAppLocked(selectedApp.packageName);
            setAppLockStatus(selectedApp.packageName, !isLocked);
            adapter.notifyDataSetChanged(); // Update the UI
        });
    }

    // Helper methods for locking/unlocking apps
    private boolean isAppLocked(String packageName) {
        // Implement logic to check if the app is locked
        // You can use SharedPreferences or a database to store the list of locked apps
        // Return true if locked, false if unlocked
        return true;
    }

    private void setAppLockStatus(String packageName, boolean locked) {
        // Implement logic to set the lock status of the app
        // You can use SharedPreferences or a database to store the list of locked apps
    }
}
