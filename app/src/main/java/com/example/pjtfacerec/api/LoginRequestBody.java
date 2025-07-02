package com.example.pjtfacerec.api;


public class LoginRequestBody {
    private String email;
    private String password;

    public LoginRequestBody() {
//        this.email = System.getenv("LOGIN_EMAIL");
        this.email = "znatni.yasmine@gmail.com";
//        this.pwd = System.getenv("LOGIN_PASSWORD");
        this.password = "Password@1";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return password;
    }

    public void setPwd(String pwd) {
        this.password = pwd;
    }
}
