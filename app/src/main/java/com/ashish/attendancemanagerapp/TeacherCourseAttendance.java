package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashish.attendancemanagerapp.Utility.SendEmailTask;
import com.ashish.attendancemanagerapp.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherCourseAttendance extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TeacherCourseAttendance";
    private String courseId, courseName, courseYear;
    private TableLayout stk;
    HashMap<String, String> map = new HashMap<>();
    HashMap<String, Integer> percentageMap = new HashMap<>();
    HashMap<String, String> studentIdToMail = new HashMap<>();

    List<String> dateList = new ArrayList<>();
    List<String> studentEnrolledList = new ArrayList<>();
    Double totalDuration=0d;
    private DatabaseReference mDatabase;

    private EditText percentageEditText;
    private Button showButton, sendButton;
    private int currentPercentage = 101;
    private String currentPercentageString;

    ArrayList<String> emailList ;
    private String message, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course_attendance);

        Intent intent = getIntent();
        courseId = intent.getStringExtra("CourseId");
        courseName = intent.getStringExtra("CourseName");
        courseYear = intent.getStringExtra("CourseYear");

        percentageEditText = findViewById(R.id.teacherCourseAttendance_percentageEditView);
        showButton = findViewById(R.id.teacherCourseAttendance_showButton);
        sendButton = findViewById(R.id.teacherCourseAttendance_sentEmailButton);
        percentageEditText.setText("0");


        emailList = new ArrayList<>();

        setTitle(courseName);

        //message = "BSDK aa ja class.Na tho gand mar lunga.";
        message = "testing";
        subject = "Shortage of Attendance";

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("CourseAttendance").child(courseId)
                .child(courseYear).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot postSnap  : snapshot.getChildren()){
                        if(postSnap.exists()){
                            String id, duration, p;
                            dateList.add(postSnap.getKey());
                            String[] token = postSnap.getValue(String.class).split(",");
                            for(String str : token){
                                id = str.substring(0, str.length()-2);
                                duration = str.substring(str.length()-2, str.length()-1);
                                p = str.substring(str.length()-1);
                                map.put(postSnap.getKey()+id, p);
                                totalDuration = totalDuration+Double.parseDouble(duration);
                                if(!percentageMap.containsKey(id)){
                                    percentageMap.put(id, 0);
                                }
                                if(p.equals("1")) {
                                    percentageMap.put(id, percentageMap.get(id) + Integer.parseInt(duration));
                                }
                            }

                        }
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);


        getAllStudentEnrolled();
    }

    private void getEnrolledStudentEmailId() {
        for(final String id : studentEnrolledList){
            mDatabase.child("Student").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Student student = snapshot.getValue(Student.class);
                                Log.d(TAG, "onDataChange: "+student.getUserEmail() + " "+id);
                                studentIdToMail.put(id, student.getUserEmail());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }


    private void getAllStudentEnrolled() {
        mDatabase.child("CourseEnrolled").child(courseId)
                .child(courseYear).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String studentEnrolled = snapshot.getValue(String.class);
                    String[] list = studentEnrolled.split(",");
                    for (String str : list) {
                        studentEnrolledList.add(str);
                    }
                    totalDuration = totalDuration/studentEnrolledList.size();
                    getEnrolledStudentEmailId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        stk = (TableLayout) findViewById(R.id.teacherCourseAttendance_table_main);
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("   Sl.No   ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText("     Reg No.     ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);

        TextView[] tv2 = new TextView[dateList.size()];
        int idx=0;
        for(String date : dateList) {
            tv2[idx] = new TextView(this);
            tv2[idx].setText(" "+date+" ");
            tv2[idx].setTextColor(Color.WHITE);
            tbrow0.addView(tv2[idx]);
            idx++;
        }

        TextView tv3 = new TextView(this);
        tv3.setText(" Percentage ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);


        stk.addView(tbrow0);
        fill();
    }
    private void fill(){
        String currRegNo;
        emailList.clear();
        for (Map.Entry mapElement : studentIdToMail.entrySet()) {
            String key = (String)mapElement.getKey();

            // Add some bonus marks
            // to all the students and print it
            String value = (String) mapElement.getValue();

            Log.d(TAG, "fill: "+ key + " " + value);
        }
        for (int i = 0; i < studentEnrolledList.size(); i++) {

            currRegNo = studentEnrolledList.get(i);

            if((int)((percentageMap.get(currRegNo)*100)/totalDuration)>=currentPercentage) continue;

            emailList.add(studentIdToMail.get(currRegNo));
            Log.d(TAG, "fill: " + studentIdToMail.get(currRegNo));

            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + i);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText( currRegNo);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView[] t3v = new TextView[dateList.size()];
            int idx=0;

            for(String date : dateList) {
                t3v[idx] = new TextView(this);

                if(map.get(date+currRegNo).equals("1")){
                    t3v[idx].setText(" P ");
                } else {
                    t3v[idx].setText(" -- " );
                }

                t3v[idx].setTextColor(Color.WHITE);
                t3v[idx].setGravity(Gravity.CENTER);
                tbrow.addView(t3v[idx]);
                idx++;

            }

            TextView t4v = new TextView(this);
            t4v.setText( (int)((percentageMap.get(currRegNo)*100)/totalDuration) +"");
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);

            stk.addView(tbrow);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teacherCourseAttendance_showButton:

                currentPercentageString = percentageEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(currentPercentageString)&&isNumber(currentPercentageString)){
                    currentPercentage = Integer.parseInt(currentPercentageString);
                    init();
                } else{
                    Toast.makeText(TeacherCourseAttendance.this,
                            "Enter A Natural Number",
                            Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.teacherCourseAttendance_sentEmailButton:
                for(String str: emailList){
                    if(str!=null){
                        Log.d(TAG, str);
                    } else{
                        Log.d(TAG, "NULL");
                    }
                }
                SendEmailTask runnable = new SendEmailTask(TeacherCourseAttendance.this,
                        emailList, subject, message);
                new Thread(runnable).start();
                break;
        }
    }

    private boolean isNumber(String currentPercentageString) {
        char[] ch = currentPercentageString.toCharArray();
        for(int i=0;i<currentPercentageString.length();i++){
            if(!(ch[i]>='0'&&ch[i]<='9'))
                return false;
        }
        return true;
    }
}