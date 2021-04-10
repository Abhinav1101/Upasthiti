package com.ashish.attendancemanagerapp;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ashish.attendancemanagerapp.model.ClassInfo;
import com.ashish.attendancemanagerapp.model.CourseInfo;
import com.ashish.attendancemanagerapp.ui.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherAddClass extends AppCompatActivity implements View.OnClickListener {

    private Button addTimeButton, createClassButton;
    private Spinner weekSpinner, fromSpinner, tillSpinner;
    private EditText courseIdEditText;
    private TextView courseTimingTextView;
    private ProgressBar progressBar;

    private DatabaseReference mDatabase;
    private List<CourseInfo> courseInfoList;

    private String courseId, courseTiming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_class);

        addTimeButton = findViewById(R.id.teacherAddClass_AddTimebutton);
        createClassButton = findViewById(R.id.teacherAddClass_CreateClassbutton);
        weekSpinner = findViewById(R.id.teacherAddClass_Weekspinner);
        fromSpinner = findViewById(R.id.teacherAddClass_Fromspinner);
        tillSpinner = findViewById(R.id.teacherAddClass_Tillspinner);
        courseIdEditText = findViewById(R.id.teacherAddClass_courseIdEditText);
        courseTimingTextView = findViewById(R.id.teacherAddClass_courseTimingtextView);
        progressBar  = findViewById(R.id.teacherAddClass_progressBar);

        //create a list of items for the  spinner.
        String[] weekItems = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] timeItems = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","12",
                                            "13", "14", "15", "16", "17", "18", "19", "20", "21",
                                            "22", "23"};
        //create an adapter to describe how the items are displayed.
        ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, weekItems);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeItems);

        //set the spinners adapter to the previously created one.
        weekSpinner.setAdapter(weekAdapter);
        fromSpinner.setAdapter(timeAdapter);
        tillSpinner.setAdapter(timeAdapter);

        addTimeButton.setOnClickListener(this);
        createClassButton.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        courseInfoList = new ArrayList<>();

        getCourseInfoFromFireStore();
    }

    private void getCourseInfoFromFireStore() {
        mDatabase.child("CourseInfo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseInfoList.clear();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            CourseInfo courseInfo = postSnapshot.getValue(CourseInfo.class);
                            courseInfoList.add(courseInfo);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teacherAddClass_AddTimebutton:
                courseTiming = courseTimingTextView.getText().toString().trim();
                String week, from, till;
                week = weekSpinner.getSelectedItem().toString();
                from = fromSpinner.getSelectedItem().toString();
                till = tillSpinner.getSelectedItem().toString();

                courseTiming = courseTiming+"\n\n"+week+" "+from+" - "+till;
                courseTimingTextView.setText(courseTiming);
                break;
            case R.id.teacherAddClass_CreateClassbutton:

                courseId = courseIdEditText.getText().toString().trim().toUpperCase();
                courseTiming = courseTimingTextView.getText().toString().trim();

                CourseInfo course = getCourseInfo(courseId);
                if(!course.getCourseId().equals("-1")) {
                    if(!TextUtils.isEmpty(courseId)&&
                    !TextUtils.isEmpty(courseTiming)){
                        LocalDate currentDate = LocalDate.now();
                        int y = currentDate.getYear();
                        String currYear = Integer.toString(y);
                        ClassInfo classInfo = new ClassInfo(course, courseTiming, currYear);
                        progressBar.setVisibility(View.VISIBLE);
                        addClassInfoToDatabase(classInfo);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else{
                        Toast.makeText(TeacherAddClass.this, "Empty Fields Not Allowed.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TeacherAddClass.this, "Invalid Course Id",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private CourseInfo getCourseInfo(String courseId) {
        for(CourseInfo val : courseInfoList){
            if(val.getCourseId().equals(courseId))
                return val;
        }
        return new CourseInfo("-1", "-1", "-1");
    }

    private void addClassInfoToDatabase(ClassInfo classInfo) {
        UserApi userApi = UserApi.getInstance();
        mDatabase.child("TeacherCourse").child(userApi.getUserId()).child(classInfo.getCourseYear())
                .child(classInfo.getCourseId())
                .setValue(classInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TeacherAddClass.this, "Class Added Successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TeacherAddClass.this, "Something went wrong.\nPlease check " +
                                    "your internet connection.",
                            Toast.LENGTH_LONG).show();
                }
            });
    }
}