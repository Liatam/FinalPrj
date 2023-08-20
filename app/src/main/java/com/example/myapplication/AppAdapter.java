package com.example.myapplication;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends BaseAdapter {
    private Context context;
    private List<ApplicationInfo> appList;

    public AppAdapter(Context context, List<ApplicationInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false);
        }

        // Populate the list item view with app information
        ApplicationInfo appInfo = appList.get(position);
        TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
        appNameTextView.setText(appInfo.loadLabel(context.getPackageManager()));

        return convertView;
    }
}

