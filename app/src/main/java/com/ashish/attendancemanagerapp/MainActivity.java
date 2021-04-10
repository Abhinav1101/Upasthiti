package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashish.attendancemanagerapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button getStartedButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Check whether user is already login
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String firebaseUserId = user.getUid();
                    //Log.d(TAG, firebaseUserId);
                    mDatabase.child("UserId")
                            .child(firebaseUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if( snapshot.exists()){
                                        String userId = snapshot.getValue(String.class);
                                        String[] userType = {"Student", "Teacher", "Admin"};

                                        for(String type : userType){
                                            Log.d(TAG, type);
                                            alreadyLogin(type, userId);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }

            }
        };

        getStartedButton = findViewById(R.id.mainActivity_getStartedButton);

        //Go to Login Activity if user is not already login
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    private void alreadyLogin(final String type, final String userId) {
        mDatabase.child(type).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if(type.equals("Student")){
                                Intent intent = new Intent(MainActivity.this,
                                        StudentCourseActivity.class);
                                intent.putExtra("STUDENT_ID", userId);
                                startActivity(intent);
                            } else if(type.equals("Teacher")){
                                Intent intent = new Intent(MainActivity.this,
                                        TeacherActivity.class);
                                intent.putExtra("UserId", userId);
                                startActivity(intent);
                            } else {
                                startActivity(new Intent(MainActivity.this,
                                        AdminActivity.class));
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
