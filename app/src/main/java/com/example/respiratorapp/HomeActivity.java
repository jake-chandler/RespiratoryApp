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
 * @brief Represents the home screen of our application
 */
public class HomeActivity extends Activity {

    /**
     * Buttons
     */
    private ImageView pairButton;
    private ImageView logout;
    private ImageView beginTestButton;
    private ImageView prevTestButton;
    private ImageView contactButton;
    private ImageView aboutButton;
    private Activity activity = this;

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
        aboutButton = (ImageView) findViewById(R.id.about);
        pairButton = (ImageView) findViewById(R.id.pair_btn);
        contactButton = (ImageView) findViewById(R.id.contact_btn);
        logout = (ImageView) findViewById(R.id.logout);
        beginTestButton = (ImageView) findViewById(R.id.begin_test_btn);
        prevTestButton = (ImageView) findViewById(R.id.view_prev_res_btn);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PairingActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userServiceIntent = new Intent(activity, UserService.class);
                bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
                Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ContributionActivity.class);
                startActivity(intent);
            }
        });
        beginTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
        prevTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("HOME", "BUTTON CLICKED");
                Intent intent = new Intent(HomeActivity.this, PreviousResultsActivity.class);
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

