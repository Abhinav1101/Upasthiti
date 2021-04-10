package com.ashish.attendancemanagerapp.model;

public class DateAttendanceInfo {

    private String date;
    private String timeInterval;

    public DateAttendanceInfo(String date, String timeInterval) {
        this.date = date;
        this.timeInterval = timeInterval;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
