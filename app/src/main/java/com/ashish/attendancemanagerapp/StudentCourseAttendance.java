package com.ashish.attendancemanagerapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.model.DateAttendanceInfo;
import com.ashish.attendancemanagerapp.ui.AttendanceTableViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentCourseAttendance extends AppCompatActivity {

    private TextView courseNameTextView, courseIdTextView ;
    private List<DateAttendanceInfo> dateSheet;
    private DatabaseReference mDatabase;
    private String courseID, studentID;
    private String courseName, courseYear;
    private RecyclerView recyclerView;
    private AttendanceTableViewAdapter adapter;
    private TableLayout stk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_attendance);

        courseID = getIntent().getExtras().getString("COURSE_ID");
        courseName = getIntent().getExtras().getString("COURSE_NAME");
        courseYear = courseID.substring(courseID.length()-4);
        studentID = getIntent().getExtras().getString("STUDENT_ID");
        courseID = courseID.substring(0, courseID.length()-5);

        courseNameTextView = findViewById(R.id.courseNameTextView);
        courseIdTextView = findViewById(R.id.courseid);
        courseNameTextView.setText(courseName);
        courseIdTextView.setText(courseID+"-"+courseYear);
        stk = (TableLayout) findViewById(R.id.teacherClassAttendance_table_main);

        recyclerView = findViewById(R.id.recyclerViewStudentAttendanceList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dateSheet = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
         mDatabase.child("StudentAttendance").child(studentID).child(courseYear).
                child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String str = snapshot.getValue(String.class);
                    String []token = str.split(",");

                    for(int i=0;i<token.length;i++) {
                        int idx = 0;
                        str=token[i];
                        if(!TextUtils.isEmpty(str)) {
                            String date = str.substring(0, str.length()-2);
                            String timeDuration = str.substring(str.length()-2);
                            DateAttendanceInfo dateAttendanceInfo = new DateAttendanceInfo(date, timeDuration);
                            dateSheet.add(dateAttendanceInfo);
                        }

                    }

                    adapter = new AttendanceTableViewAdapter(dateSheet);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}