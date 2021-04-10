package com.ashish.attendancemanagerapp.model;

import java.io.Serializable;

public class CourseInfo implements Serializable {
    private String courseId, courseName, courseDepartment;

    public CourseInfo() {
    }

    public CourseInfo(String courseId, String courseName, String courseDepartment) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDepartment = courseDepartment;
    }

    public CourseInfo(CourseInfo courseInfo) {
        this.courseId = courseInfo.getCourseId();
        this.courseName = courseInfo.getCourseName();
        this.courseDepartment = courseInfo.getCourseDepartment();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }
}
