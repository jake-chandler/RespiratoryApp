package com.example.respiratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Represents the new or returning user screen
 */
public class UserActivity extends AppCompatActivity {

    /**
     * Buttons
     */
    private ImageView newUser;
    private ImageView newUser2;
    private ImageView existUser;
    private ImageView existUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user);

        initListeners();

    }

    protected void initListeners() {
        newUser = (ImageView) findViewById(R.id.new_usr_btn);
        newUser2 = (ImageView) findViewById(R.id.new_usr);
        existUser = (ImageView) findViewById(R.id.existing_usr_btn);
        existUser2 = (ImageView) findViewById(R.id.exist_usr);
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, FormActivity.class);
                startActivity(intent);
            }
        });
        newUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, FormActivity.class);
                startActivity(intent);
            }
        });
        existUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        existUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}