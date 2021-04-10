package com.ashish.attendancemanagerapp.model;

public class Teacher extends User {
    private String deptName, designation;

    public Teacher() {

    }
    public Teacher(String userId, String userName, String userPassword,
                   String userEmail, String userPhoneNumber, String deptName,String designation) {

        super(userId, userName, userPassword, userEmail, userPhoneNumber);
        this.deptName = deptName;
        this.designation = designation;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
