package com.ashish.attendancemanagerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TeacherClassAttendance extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TeacherClassAttendance";
    private TextView courseNameTextView, courseIdTextView;
    private ImageView qrCodeImageView;
    private Button startClassButton;
    private String courseName, courseId, courseYear, todayDate, allStudent, duration, directoryTodayDate;
    private TableLayout stk;
    private Spinner spinner;
    ArrayList<String> studentEnrolledList;
    private Boolean firstTime = true;
    HashMap<String, Boolean> attendance;
    LocalDateTime validDate;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_attendance);

        Toolbar toolbar = findViewById(R.id.teacherClassAttendance_toolbar);




        Intent intent = getIntent();
        courseName = intent.getStringExtra("classCourseName");
        toolbar.setTitle(courseName);
        courseId = intent.getStringExtra("classCourseId");
        courseYear = intent.getStringExtra("classCourseYear");
        courseIdTextView = findViewById(R.id.teacherClassAttendance_courseIdtextView);
        startClassButton = findViewById(R.id.teacherClassAttendance_startClassbutton);
        qrCodeImageView = findViewById(R.id.teacherClassAttendance_qrCodeimageView);
        stk = (TableLayout) findViewById(R.id.teacherClassAttendance_table_main);
        spinner = findViewById(R.id.teacherClassAttendance_durationSpinner);


        //create a list of items for the spinner.
        String[] items = new String[]{"1","2","3","4","5","6","7","8","9"};
        //create an adapter to describe how the items are displayed.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        courseIdTextView.setText(courseId + " - " + courseYear);

        duration="1";
        //Getting Today Date
        LocalDateTime currentDate = LocalDateTime.now();
        String day = String.valueOf(currentDate.getDayOfMonth());
        String month = String.valueOf(currentDate.getMonthValue());
        String year = String.valueOf(currentDate.getYear());
        day = "0"+day;
        day = day.substring(day.length()-2);
        month = "0"+month;
        month = month.substring(month.length()-2);
        todayDate = day + "/" + month + "/" + year;
        directoryTodayDate = day+month+year;
        duration="1";

        mDatabase = FirebaseDatabase.getInstance().getReference();

        startClassButton.setOnClickListener(this);

        studentEnrolledList = new ArrayList<>();
        getAllStudentEnrolled();
        attendance = new HashMap<>();
        //init();

        realTimeAttendanceRecord();

    }

    

    private void realTimeAttendanceRecord() {
        mDatabase.child("CourseAttendance").child(courseId)
                .child(courseYear).child(directoryTodayDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String str = snapshot.getValue(String.class);
                            String[] list = str.split(",");
                            String id;
                            for (String s : list) {
                                id = s.substring(0, s.length() - 2);
                                if(s.charAt(s.length()-1)=='1'){
                                    attendance.put(id, true);
                                } else{
                                    attendance.put(id, false);
                                }
                            }
                            if(list.length>0) init();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllStudentEnrolled() {
        mDatabase.child("CourseEnrolled").child(courseId)
                .child(courseYear).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String studentEnrolled = snapshot.getValue(String.class);
                    String[] list = studentEnrolled.split(",");
                    allStudent="";
                    for (String str : list) {
                        studentEnrolledList.add(str);
                        allStudent += str;
                        allStudent += duration;
                        allStudent += "0,";
                        attendance.put(str, false);
                    }
                    allStudent = allStudent.substring(0, allStudent.length() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if(firstTime) {
            LocalDateTime currentDate = LocalDateTime.now();
            validDate = currentDate.plusHours(Integer.parseInt(duration));
            duration = spinner.getSelectedItem().toString();
            updateDataBaseOfCourseStudentAttendance();
            generateQrCode();
            firstTime=false;
        }

    }

    private void updateDataBaseOfCourseStudentAttendance() {
        //Log.d(TAG, allStudent);
        mDatabase.child("CourseAttendance").child(courseId)
                .child(courseYear).child(directoryTodayDate)
                .setValue(allStudent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Nacho
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherClassAttendance.this,
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG).show();
            }
        });


        for(final String studentID : studentEnrolledList){
            mDatabase.child("StudentAttendance").child(studentID)
                    .child(courseYear).child(courseId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String str;
                            if(snapshot.exists()) {
                                str = snapshot.getValue(String.class);
                                str+=","+todayDate + duration + "0";
                            }
                            else {
                                str = todayDate + duration + "0";
                            }
                            mDatabase.child("StudentAttendance").child(studentID)
                                    .child(courseYear).child(courseId)
                                    .setValue(str);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void generateQrCode() {
        String text = courseId + "," + todayDate + "," + duration +","+validDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Log.d(TAG, validDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()+"");
        Log.d(TAG, LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()+"");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("   Sl.No   ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("   Reg No.   ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("   Present   ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        stk.addView(tbrow0);
        fill();
    }
    private void fill(){
        String currRegNo;
        for (int i = 0; i < studentEnrolledList.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + i);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            currRegNo = studentEnrolledList.get(i);

            TextView t2v = new TextView(this);
            t2v.setText( currRegNo);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);

            if(attendance.get(currRegNo)){
                t3v.setText(" P ");
            } else {
                t3v.setText(" -- " );
            }
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            stk.addView(tbrow);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_teacher_class_attendance, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.toolbar_courseAttendance:
                Intent intent  = new Intent(TeacherClassAttendance.this,
                        TeacherCourseAttendance.class);
                intent.putExtra("CourseId", courseId);
                intent.putExtra("CourseName", courseName);
                intent.putExtra("CourseYear", courseYear);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}