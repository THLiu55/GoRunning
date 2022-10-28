package com.example.gorunning.models;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
    String email;
    String username;
    String password;
    WeeklyData weeklyData;

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public WeeklyData getWeeklyData() {
        return weeklyData;
    }

    public void setWeeklyData(WeeklyData weeklyData) {
        this.weeklyData = weeklyData;
    }
}
