package com.example.respiratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ContributionActivity extends Activity {
    private ImageView home_btn;
    private ImageView settings_btn;
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
        home_btn = (ImageView) findViewById(R.id.imageView22);
        settings_btn = (ImageView) findViewById(R.id.imageView23);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContributionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContributionActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}