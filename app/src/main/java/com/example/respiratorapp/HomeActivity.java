package com.example.respiratorapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

/**
 * Represents the home screen of our application
 */
public class HomeActivity extends Activity {

    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        initListeners();

    }

    protected void initListeners() {
        ImageView aboutButton = (ImageView) findViewById(R.id.about);
        ImageView pairButton = (ImageView) findViewById(R.id.pair_btn);
        ImageView contactButton = (ImageView) findViewById(R.id.contact_btn);
        ImageView logout = (ImageView) findViewById(R.id.logout);
        ImageView beginTestButton = (ImageView) findViewById(R.id.begin_test_btn);
        ImageView prevTestButton = (ImageView) findViewById(R.id.view_prev_res_btn);
        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        });
        pairButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, PairingActivity.class);
            startActivity(intent);
        });
        logout.setOnClickListener(view -> {
            Intent userServiceIntent = new Intent(activity, UserService.class);
            bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
            Intent intent = new Intent(HomeActivity.this, UserActivity.class);
            startActivity(intent);
        });
        contactButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ContributionActivity.class);
            startActivity(intent);
        });
        beginTestButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
            startActivity(intent);
        });
        prevTestButton.setOnClickListener(view -> {
            Log.i("HOME", "BUTTON CLICKED");
            Intent intent = new Intent(HomeActivity.this, PreviousResultsActivity.class);
            startActivity(intent);
        });
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("FORM", "User Service discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // de-register this user
            UserService userService = binder.getService();
            userService.deregisterUser();

            Intent intent = new Intent(HomeActivity.this, UserActivity.class);
            startActivity(intent);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}

