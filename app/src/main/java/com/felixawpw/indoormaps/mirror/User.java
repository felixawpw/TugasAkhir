package com.felixawpw.indoormaps.mirror;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String nama;
    private String roles;
    private String googleAuthId;
    private int id;
    public User() {

    }

    public User(JSONObject json) throws JSONException {
        id = json.getInt("id");
        nama = json.getString("nama");
        roles = json.getString("roles");
        googleAuthId = json.getString("google_auth_id");
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getGoogleAuthId() {
        return googleAuthId;
    }

    public void setGoogleAuthId(String googleAuthId) {
        this.googleAuthId = googleAuthId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("id = %s ; nama = %s ; roles = %s ; authId = %s", id, nama, roles, googleAuthId);
    }
}
