package com.ashish.attendancemanagerapp.model;

import java.io.Serializable;

public class ClassDateInfo extends CourseInfo implements Serializable {
    private String dateTimeListInfo;

    public ClassDateInfo() {

    }

    public ClassDateInfo(String courseId, String courseName, String courseDepartment ,
                         String dateTimeListInfo) {
        super(courseId, courseName, courseDepartment);
        this.dateTimeListInfo =  dateTimeListInfo;
    }

    public ClassDateInfo(CourseInfo courseInfo, String dateTimeListInfo){
        super(courseInfo);
        this.dateTimeListInfo += dateTimeListInfo;
    }


    public String getDateTimeListInfo() {
        return dateTimeListInfo;
    }

    public void setDateTimeListInfo(String dateTimeListInfo) {
        this.dateTimeListInfo =  dateTimeListInfo;
    }


}
