package com.example.respiratorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Represents the contribution screen of our application
 */
public class ContributionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_contribution);

        initListeners();
    }

    protected void initListeners() {
        ImageView homeButton = (ImageView) findViewById(R.id.home);
        //allows the user to move bac to the home screen
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ContributionActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}