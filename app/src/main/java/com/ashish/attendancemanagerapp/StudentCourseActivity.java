package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.model.CourseInfo;
import com.ashish.attendancemanagerapp.model.Student;
import com.ashish.attendancemanagerapp.ui.RecyclerItemClickListener;
import com.ashish.attendancemanagerapp.ui.StudentCoursesRecyclerAdapter;
import com.ashish.attendancemanagerapp.ui.UserApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StudentCourseActivity extends AppCompatActivity {

    private static final String TAG = "StudentCourseActivity";
    private DatabaseReference mDatabase;
    private ArrayList<String> courseEnrolledList;
    private FirebaseAuth mAuth;

    private List<CourseInfo> courseInfoList;
    private  HashMap<String, Integer> map = new HashMap<>();
    private  HashMap<String, String> yearMap = new HashMap<>();
    private  HashMap<String, Integer> percentageMap = new HashMap<>();
    private RecyclerView recyclerView;
    private StudentCoursesRecyclerAdapter studentCoursesRecyclerAdapter;
    private String studentId;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course);

        Toolbar toolbar = findViewById(R.id.studentCourseActivity_toolbar);
        toolbar.setTitle("Courses");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.studentCourses_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        studentId =intent.getStringExtra("STUDENT_ID");

        FloatingActionButton fab = findViewById(R.id.studentCourses_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Goto QR SCANNER
                Intent intent = new Intent(StudentCourseActivity.this,StudentScannerActivity.class);
                //intent.putExtra("STUDENT_OBJECT",student);

                startActivity(intent);

            }
        });

        courseEnrolledList = new ArrayList<>();
        courseInfoList = new ArrayList<>();
        getCourseEnrolledOfStudent();



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(StudentCourseActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CourseInfo courseinfo = studentCoursesRecyclerAdapter.getAdapterPositionCourseInfo(position);
                Intent intent = new Intent(StudentCourseActivity.this,StudentCourseAttendance.class);
                intent.putExtra("COURSE_ID",courseinfo.getCourseId().split(",")[0]);
                intent.putExtra("COURSE_NAME",courseinfo.getCourseName());
                intent.putExtra("STUDENT_ID", studentId);
                startActivity(intent);
            }
        }));

    }

    private void getCourseEnrolledOfStudent() {

        mDatabase.child("Student").child(studentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Student student = snapshot.getValue(Student.class);

                    if (student != null) {
                        courseEnrolledList = student.getCourseEnrolled();

                        String str, course, year;

                        for (int i = 0; i < courseEnrolledList.size(); i++) {
                            str = courseEnrolledList.get(i);
                            if (!TextUtils.isEmpty(str)) {
                                course = str.substring(0, str.length() - 5);
                                year = str.substring(str.length() - 4);
                                map.put(course, 1);
                                yearMap.put(course, year);
                                final String finalCourse = course;
                                mDatabase.child("StudentAttendance")
                                        .child(studentId).child(year)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()) {
                                                    for (DataSnapshot postSnap : snapshot.getChildren()) {
                                                        if(postSnap.exists()) {
                                                            Double totalHour = 0.0;
                                                            Double presentHour = 0.0;
                                                            String str = postSnap.getValue(String.class);
                                                            //Log.d(TAG, postSnap.getKey() + "  " + str);
                                                            String[] tokens = str.split(",");
                                                            for (String s : tokens) {
                                                                //Log.d(TAG, s);
                                                                totalHour += (s.charAt(s.length() - 2) - '0');
                                                                if (s.charAt(s.length() - 1) == '1') {
                                                                    presentHour += (s.charAt(s.length() - 2) - '0');
                                                                }

                                                            }
                                                            if (!percentageMap.containsKey(postSnap.getKey())) {
                                                                percentageMap.put(postSnap.getKey(), (int) ((presentHour / totalHour)*100 +0.5));

                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }

                    }
                    fillCourseInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillCourseInfo() {
        mDatabase.child("CourseInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    courseInfoList.clear();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.exists()) {
                            CourseInfo courseInfo = postSnapshot.getValue(CourseInfo.class);
                            if (map.containsKey(courseInfo.getCourseId())) {
                                String courseId = courseInfo.getCourseId() + "-" + yearMap.get(courseInfo.getCourseId());
                                if (percentageMap.containsKey(courseInfo.getCourseId())) {
                                    courseId += ",";
                                    courseId += percentageMap.get(courseInfo.getCourseId());
                                } else {
                                    courseId += ",";
                                    courseId += "111";
                                }
                                courseInfo.setCourseId(courseId);
                                courseInfoList.add(courseInfo);
                            }
                            Log.d(TAG, courseInfo.getCourseId());
                        }
                    }
                    studentCoursesRecyclerAdapter = new StudentCoursesRecyclerAdapter(StudentCourseActivity.this,
                            courseInfoList);
                    recyclerView.setAdapter(studentCoursesRecyclerAdapter);
                    studentCoursesRecyclerAdapter.notifyDataSetChanged();

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
                startActivity(new Intent(StudentCourseActivity.this,
                        MainActivity.class));
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}