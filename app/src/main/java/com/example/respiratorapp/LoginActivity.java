package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
                    RespiratoryUser user = new RespiratoryUser( RespiratoryUserString );
                    if (passwordValue.equals(user.getPassword())) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
}