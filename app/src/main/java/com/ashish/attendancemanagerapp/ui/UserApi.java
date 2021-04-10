package com.ashish.attendancemanagerapp.ui;

import com.ashish.attendancemanagerapp.model.User;

public class UserApi {

    private String userId, userName, userPassword, userEmail, userPhoneNumber;

    private static UserApi instance;

    public static UserApi getInstance() {
        if (instance == null)
            instance = new UserApi();

        return instance ;
    }

    public UserApi() { }

    public void setValue(User user){
        instance.userId = user.getUserId();
        instance.userName = user.getUserName();
        instance.userPassword = user.getUserPassword();
        instance.userEmail = user.getUserEmail();
        instance.userPhoneNumber = user.getUserPhoneNumber();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

}
