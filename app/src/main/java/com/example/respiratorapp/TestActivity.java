package com.example.respiratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class TestActivity extends AppCompatActivity {

    private ImageView next;
    private ImageView retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);

        initListeners();

    }

    protected void initListeners() {
        next = (ImageView) findViewById(R.id.next_btn);
        retry = (ImageView) findViewById(R.id.retry_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Test2Activity.class);
                startActivity(intent);
                //setContentView(R.layout.activity_test2);
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
    }
}