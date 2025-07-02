package com.example.pjtfacerec.api;

public class SpringResponse {
    private String fullName;

    public SpringResponse(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
