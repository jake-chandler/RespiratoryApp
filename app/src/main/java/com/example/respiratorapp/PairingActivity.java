package com.example.respiratorapp;

//  TODO:
//      import proper ble libraries
//
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Represents the BLE pairing process. Attempts to pair to the MC.
 */
public class PairingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pairing);

        if (pair()){
            //set content to activity_paired screen
            (new Handler()).postDelayed(this::changeScreens,3000);
        }
        else {
            //handle pairing failure
        }



    }

    /**
     * Helper method used to pair to MC via BLE
     * @return true on pairing success
     * @return false on pairing fail
     */
    protected boolean pair(){
        return true;
    }

    /**
     * Helper method used to change screens
     *
     * @param
     * @return void
     */
    public void changeScreens(){
        Intent intent = new Intent(this, PairedActivity.class);
        startActivity(intent);
    }

}
