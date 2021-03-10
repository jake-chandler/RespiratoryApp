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
    private ImageView home_btn;
    private ImageView settings_btn;
    protected void initListeners() {
        home_btn = (ImageView) findViewById(R.id.home);
        settings_btn = (ImageView) findViewById(R.id.settings_cog);
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