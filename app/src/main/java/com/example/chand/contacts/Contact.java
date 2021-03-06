package com.example.chand.contacts;

/**
 * Created by chand on 7/27/2016.
 */
public class Contact implements Comparable {

    private String fullName;
    private String email;
    private String phNumber;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    @Override
    public int compareTo(Object another) {
        Contact copy =  (Contact) another;
        return fullName.toLowerCase().compareTo(copy.getFullName().toLowerCase());
    }
}
