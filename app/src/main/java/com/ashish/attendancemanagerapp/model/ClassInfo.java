package com.ashish.attendancemanagerapp.model;

public class ClassInfo extends CourseInfo{
    private String courseTiming, courseYear;

    public ClassInfo(){}

    public ClassInfo(String courseId, String courseName, String courseDepartment ,
                     String courseTiming, String courseYear) {
        super(courseId, courseName, courseDepartment);
        this.courseTiming = courseTiming;
        this.courseYear = courseYear;
    }

    public ClassInfo(CourseInfo courseInfo, String courseTiming, String courseYear){
        super(courseInfo);
        this.courseTiming = courseTiming;
        this.courseYear = courseYear;
    }


    public String getCourseTiming() {
        return courseTiming;
    }

    public void setCourseTiming(String courseTiming) {
        this.courseTiming = courseTiming;
    }

    public String getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(String courseYear) {
        this.courseYear = courseYear;
    }
}
