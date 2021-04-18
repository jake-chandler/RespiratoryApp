package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private RespiratoryUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);


        initListeners();
    }

    private void initListeners() {
        ImageView submitButton;
        submitButton = (ImageView) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View view) {
                username = (EditText) findViewById(R.id.editTextUsername);
                password = (EditText) findViewById(R.id.editTextPassword);
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();
                try {
                    String RespiratoryUserString = RespiratoryUser.retrieveUser(getApplicationContext(), usernameValue);
                    user = new RespiratoryUser( RespiratoryUserString );
                    if (passwordValue.equals(user.getPassword())) {
                        Intent userServiceIntent = new Intent(LoginActivity.this, UserService.class);
                        bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
                    }
                    else{
                        Log.i("LOGIN", "wrong password");
                    }
                } catch (FileNotFoundException e) {
                    Log.i("LOGIN", "user not found");
                }
            }
        });
    }
    private ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("LOGIN", "User Service discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // register this user to the User Service.
            UserService userService = binder.getService();
            userService.registerUser(user);

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}