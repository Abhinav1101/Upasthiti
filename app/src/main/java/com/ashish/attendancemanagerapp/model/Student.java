package com.ashish.attendancemanagerapp.model;


import java.io.Serializable;
import java.util.ArrayList;


public class Student extends User implements Serializable {
    //private String userId, userName, userPassword, userEmail, userPhoneNumber;
    private String deptName;
    private ArrayList<String> courseEnrolled = new ArrayList<>();

    public Student() {

    }
    public Student(String userId, String userName, String userPassword,
                   String userEmail, String userPhoneNumber, String deptName, ArrayList<String> courseEnrolled) {
        super(userId,userName,userPassword,userEmail,userPhoneNumber);
        this.deptName = deptName;
        this.courseEnrolled =courseEnrolled;

    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public ArrayList<String> getCourseEnrolled() {
        return courseEnrolled;
    }

    public void setCourseEnrolled(ArrayList<String> courseEnrolled) {

        this.courseEnrolled = courseEnrolled;
    }
    public void addToCourseEnrolled(String course) {
        this.courseEnrolled.add(course);
    }


}
