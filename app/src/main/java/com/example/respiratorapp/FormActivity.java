package com.example.respiratorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;

public class FormActivity extends AppCompatActivity {
    private EditText name;
    private EditText age;
    private EditText height;
    private EditText weight;
    private Spinner sex;
    private Spinner activityLevel;
    private EditText username;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //makes this activity full-screen (removes notification bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_form);

        initListeners();

        name = (EditText) findViewById(R.id.editTextPersonName);
        age = (EditText) findViewById(R.id.editTextAge);
        activityLevel = (Spinner) findViewById(R.id.activitySpinner);
        sex = (Spinner) findViewById(R.id.sexSpinner);
        height = (EditText) findViewById(R.id.editTextHeight);
        weight = (EditText) findViewById(R.id.editTextWeight);
        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);

        String[] activities = new String[]{"LOW", "MODERATE", "HIGH" };
        String[] sexes = new String[]{"MALE", "FEMALE"};
        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        activityLevel.setAdapter(activitiesAdapter);
        sex.setAdapter(sexesAdapter);
    }

    private ImageView submitButton;
    protected void initListeners() {
        submitButton = (ImageView) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                String nameValue = name.getText().toString();
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();
                int ageValue = Integer.parseInt(age.getEditableText().toString());
                int heightValue = Integer.parseInt(height.getEditableText().toString());
                int weightValue = Integer.parseInt(weight.getEditableText().toString());


                RespiratoryUser.Sex sexValue;
                if (sex.getSelectedItem().toString() == "MALE" ) {
                    sexValue = RespiratoryUser.Sex.MALE;
                }
                else {
                    sexValue = RespiratoryUser.Sex.FEMALE;
                }


                RespiratoryUser.ActivityLevel activityLevelValue;
                if (activityLevel.getSelectedItem().toString() == "LOW"){
                    activityLevelValue = RespiratoryUser.ActivityLevel.LOW;
                }
                else if (activityLevel.getSelectedItem().toString() == "MODERATE"){
                    activityLevelValue = RespiratoryUser.ActivityLevel.MODERATE;
                }
                else{
                    activityLevelValue = RespiratoryUser.ActivityLevel.HIGH;
                }


                RespiratoryUser user = new RespiratoryUser(usernameValue, passwordValue, nameValue,
                        sexValue, activityLevelValue, ageValue, heightValue, weightValue);
                try{
                    user.saveUser(getApplicationContext());
                }

                catch  (IOException e) {
                    Log.i("FORM", "An exception has occurred.");
                }
                Intent intent = new Intent(FormActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
