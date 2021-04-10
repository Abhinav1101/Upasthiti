package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ashish.attendancemanagerapp.ui.UserApi;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button teacherViewButton, courseViewButton, studentViewButton;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        Toolbar toolbar = findViewById(R.id.adminActivity_toolbar);
        toolbar.setTitle("Admin Activity");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();

        teacherViewButton = findViewById(R.id.adminActivity_teacherActivityButton);
        courseViewButton = findViewById(R.id.adminActivity_courseActivity);
        studentViewButton = findViewById(R.id.adminActivity_studentActivityButton);

        teacherViewButton.setOnClickListener(this);
        courseViewButton.setOnClickListener(this);
        studentViewButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.adminActivity_courseActivity:
                //Goto Admin Courses Activity

                break;
            case R.id.adminActivity_teacherActivityButton:
                //Goto Admin Teacher Activity

                break;
            case R.id.adminActivity_studentActivityButton:
                //Goto Admin Student Activity
                
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_signout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_action_signout:
                firebaseAuth.signOut();
                //Delete UserApi
                UserApi userApi = UserApi.getInstance();

                userApi.setUserId(null);
                userApi.setUserName(null);
                userApi.setUserPassword(null);
                userApi.setUserEmail(null);
                userApi.setUserPhoneNumber(null);
                startActivity(new Intent(AdminActivity.this,
                        MainActivity.class));
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}


