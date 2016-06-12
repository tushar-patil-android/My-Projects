package com.clustrhub.logindemo.models;

import org.json.JSONObject;

/**
 * Created by Tushar on 12-06-2016.
 */
public class RegistrationModel {
    JSONObject user;
    JSONObject device;

    public JSONObject getDevice() {
        return device;
    }

    public void setDevice(JSONObject device) {
        this.device = device;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }
}
