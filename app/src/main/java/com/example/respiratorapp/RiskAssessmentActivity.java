package com.example.respiratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class RiskAssessmentActivity extends AppCompatActivity {

    private ImageView export;
    private ImageView home;
    private TextView riskText;
    private TextView meansText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_riskassessment);

        initListeners();

    }

    protected void initListeners() {
        export = (ImageView) findViewById(R.id.export_res);
        home = (ImageView) findViewById(R.id.home);
        riskText = (TextView) findViewById(R.id.textViewRiskAssessment);
        meansText = (TextView) findViewById(R.id.textViewThisMeans);


        riskText.setText( );

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RiskAssessmentActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}