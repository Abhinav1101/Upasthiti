package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.model.ClassInfo;
import com.ashish.attendancemanagerapp.ui.RecyclerItemClickListener;
import com.ashish.attendancemanagerapp.ui.TeacherRecycleAdapter;
import com.ashish.attendancemanagerapp.ui.UserApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {

    private static final String TAG = "TeacherActivity";
    ArrayList<ClassInfo> classInfoList;
    private DatabaseReference mDatabase;
    private String userId;

    private RecyclerView recyclerView;
    private TeacherRecycleAdapter teacherRecycleAdapter;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Toolbar toolbar = findViewById(R.id.teacherActivity_toolbar);
        toolbar.setTitle("Teacher Activity");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();


        recyclerView = findViewById(R.id.teacherActivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userId = getIntent().getStringExtra("UserId");

        FloatingActionButton fab = findViewById(R.id.teacherActivity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherActivity.this, TeacherAddClass.class));
            }
        });

        classInfoList = new ArrayList<>();
        getAllClassesFromDatabase();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(TeacherActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ClassInfo classInfo =teacherRecycleAdapter.getAdapterPositionClassInfo(position);
                Intent intent = new Intent(TeacherActivity.this,
                        TeacherClassAttendance.class);
                intent.putExtra("classCourseName", classInfo.getCourseName());
                intent.putExtra("classCourseId", classInfo.getCourseId());
                intent.putExtra("classCourseYear", classInfo.getCourseYear());

                startActivity(intent);

            }
        }));
    }

    private void getAllClassesFromDatabase() {
        //Log.d(TAG, UserApi.getInstance().getUserId());
        Query query = mDatabase.child("TeacherCourse").child(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    classInfoList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot ps : postSnapshot.getChildren()) {
                            ClassInfo classInfo = ps.getValue(ClassInfo.class);
                            classInfoList.add(classInfo);
                        }
                    }
                    teacherRecycleAdapter = new TeacherRecycleAdapter(TeacherActivity.this,
                            classInfoList);
                    recyclerView.setAdapter(teacherRecycleAdapter);
                    teacherRecycleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                startActivity(new Intent(TeacherActivity.this,
                        MainActivity.class));
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}