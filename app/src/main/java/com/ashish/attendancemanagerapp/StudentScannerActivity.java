package com.ashish.attendancemanagerapp;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashish.attendancemanagerapp.model.ClassDateInfo;
import com.ashish.attendancemanagerapp.model.CourseInfo;
import com.ashish.attendancemanagerapp.ui.UserApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class StudentScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "StudentScannerActivity";
    ZXingScannerView scannerView;
    private String userId;
    DatabaseReference mDataBase;
    CourseInfo course;
    ClassDateInfo classDateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        //setContentView(R.layout.activity_student_scanner);
        //student =  (Student) getIntent().getSerializableExtra("STUDENT_OBJECT");
        userId = UserApi.getInstance().getUserId();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void handleResult(Result rawResult) {


        String scannedData = rawResult.getText().toString().trim();

        //verify if this is valid qr code or not


        String[] token = scannedData.split(",");
        String validTime = token[3];
        long timeInSec = Long.parseLong(validTime);
        if(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()<timeInSec) {
            addStudentAttendanceInfo(scannedData);
        } else{
            Toast.makeText(StudentScannerActivity.this, "Class time is over.",
                    Toast.LENGTH_LONG).show();
            // goBack
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    private void addStudentAttendanceInfo(String scannedData) {

        String[] token = scannedData.split(",");

        final String courseId = token[0];
        final String date = token[1];
        final String duration = token[2];

        int n = token[1].length();
        final String year = token[1].substring(n-4);

        mDataBase.child("StudentAttendance").child(userId)
                .child(year).child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String str = snapshot.getValue(String.class);
                    str = str.substring(0, str.length() - 1);
                    str += "1";
                    mDataBase.child("StudentAttendance").child(userId)
                            .child(year).child(courseId).setValue(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String directoryDate = date.replace("/", "");
        Log.d(TAG, directoryDate);

        mDataBase.child("CourseAttendance").child(courseId)
                .child(year).child(directoryDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String str = snapshot.getValue(String.class);
                            String[] lst = str.split(",");
                            int idx = Arrays.binarySearch(lst, userId + duration + "0");
                            if (idx >= 0 && idx < lst.length) {
                                lst[idx] = userId + duration + "1";
                            }
                            str = "";
                            str += lst[0];
                            for (int i = 1; i < lst.length; i++) {
                                str += ",";
                                str += lst[i];
                            }
                            mDataBase.child("CourseAttendance").child(courseId)
                                    .child(year).child(directoryDate).setValue(str);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Toast.makeText(StudentScannerActivity.this, "Added",
                Toast.LENGTH_LONG).show();

    }


}