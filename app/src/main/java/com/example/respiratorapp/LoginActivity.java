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
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * @brief Represents the login screen of our application
 */
public class LoginActivity extends AppCompatActivity {

    private RespiratoryUser user;

    private EditText username;
    private final Activity activity = this;
    private EditText password;

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
    @Override
    public void onStart(){
        super.onStart();
        checkSession();
    }

    private void checkSession(){
        SessionManagement sessionManagement = new SessionManagement((getApplicationContext()));
        String userID = sessionManagement.getSession();

        if(userID != "-1"){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            //do nothing
        }
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
                        // save this user as this the user for this session
                        Intent userServiceIntent = new Intent(activity, UserService.class);
                        bindService(userServiceIntent, userServiceConnection, Context.BIND_AUTO_CREATE);
                        // continue to home page.
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else{
                        Log.i("FORM", "wrong password");
                    }
                } catch (FileNotFoundException e) {
                    Log.i("FORM", "user not found");
                }
            }
        });
    }

    private ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;

            // save this session's user.
            UserService userService = binder.getService();
            userService.registerUser(user);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}