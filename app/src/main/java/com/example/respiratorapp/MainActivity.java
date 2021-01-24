package com.example.respiratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * This class represents the load-up screen for this application.
 * This screen should display for 3 seconds, and then transition to the home screen.
 *
 * @Author Jake Chandler
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //change to home screen after 3 sec
        //NOTE: while it could make more sense just to change the layout of this activity rather than
        //making separate activities for the startup screen & the home screen, I chose the ladder option in order
        //to start familiarizing myself with the Intent concept.
        (new Handler()).postDelayed(this::changeScreens,3000);

    }

    /**
     * Helper method used to change to home screen
     *
     * @param 
     * @return void
     */
    public void changeScreens(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }


}