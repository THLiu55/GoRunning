package com.example.gorunning.models;

import cn.bmob.v3.BmobObject;

public class Captcha extends BmobObject {
    String email;
    String captcha;

    public Captcha(String email, String captcha) {
        this.captcha = captcha;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
