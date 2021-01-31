package com.example.respiratorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * @brief Represents the home screen of our applications
 */
public class HomeActivity extends Activity {

    //buttons on home screen
    private ImageView pair_btn;
    private ImageView help_btn;
    private ImageView settings_cog;
    private ImageView begin_test_btn;
    private ImageView prev_test_btn;
    private ImageView contact_btn;

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

    /**
     *  Initializes onClickListener for each button on the home screen
     */
    protected void initListeners(){
        pair_btn = (ImageView) findViewById(R.id.pair_btn);
        pair_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PairingActivity.class);
                startActivity(intent);
            }
        });
    }



}
