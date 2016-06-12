package com.clustrhub.logindemo.models;

/**
 * Created by Tushar on 12-06-2016.
 */
public class User {
    String first_name;
    String last_name;
    String email;
    String countrycode;
    String mobile_number;
    String password;
    String agreeterms;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAgreeterms() {
        return agreeterms;
    }

    public void setAgreeterms(String agreeterms) {
        this.agreeterms = agreeterms;
    }
}
