package com.example.respiratorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * @brief Represents the home screen of our application
 */
public class HomeActivity extends Activity {

    /**
     * Buttons
     */
    private ImageView pairButton;
    private ImageView settingsCog;
    private ImageView beginTestButton;
    private ImageView prevTestButton;
    private ImageView contactButton;

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
        pairButton = (ImageView) findViewById(R.id.pair_btn);
        contactButton = (ImageView) findViewById(R.id.contact_btn);
        settingsCog = (ImageView) findViewById(R.id.settings_cog);
        beginTestButton = (ImageView) findViewById(R.id.begin_test_btn);
        prevTestButton = (ImageView) findViewById(R.id.view_prev_res_btn);
        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PairingActivity.class);
                startActivity(intent);
            }
        });
        settingsCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
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
                Intent intent = new Intent(HomeActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });
    }
}
