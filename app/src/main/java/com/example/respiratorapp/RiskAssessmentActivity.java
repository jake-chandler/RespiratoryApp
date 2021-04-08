package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @brief Represents the risk assessment screen of our application
 */
public class RiskAssessmentActivity extends AppCompatActivity {

    private ImageView export;
    private ImageView home;
    private TextView riskText;
    private TextView meansText;
    private TestResults RiskAssessment;
    private String Information;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        export = (ImageView) findViewById(R.id.export_res);
        home = (ImageView) findViewById(R.id.home);
        riskText = (TextView) findViewById(R.id.textViewRiskAssessment);
        meansText = (TextView) findViewById(R.id.textViewThisMeans);

        Information = (RiskAssessment.getBo2Risk().toString() + "\n" + RiskAssessment.getHrRisk() + "\n" + RiskAssessment.getRrRisk());
        riskText.setText( RiskAssessment.getOverallRisk().toString() );
        meansText.setText( Information );

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