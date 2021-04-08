package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {
    private ImageView homeButton;
    private ImageView logoutButton;
    private final Activity activity = this;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);

        /**
         * goes in on click listener for logout button.
         *
         * Intent userServiceIntent = new Intent(activity, UserService.class);
         * bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
         */

        initListeners();
    }

    protected void initListeners() {
        homeButton = (ImageView) findViewById(R.id.home);
        logoutButton = (ImageView) findViewById(R.id.logout);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userServiceIntent = new Intent(activity, UserService.class);
                bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
                Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("FORM", "User Service discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // de-register this user
            userService = binder.getService();
            userService.deregisterUser(userService.getActiveUser());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}