package com.felixawpw.indoormaps.mirror;

import org.json.JSONException;
import org.json.JSONObject;

public class Tenant {
    private int id;
    private String nama;
    private String googleMapsId;

    public Tenant() {

    }

    public Tenant(int id, String nama, String googleMapsId) {
        this.id = id;
        this.nama = nama;
        this.googleMapsId = googleMapsId;
    }

    public Tenant(JSONObject data) throws JSONException {
        this.id = data.getInt("id");
        this.nama = data.getString("nama");
        this.googleMapsId = data.getString("google_maps_id");
    }

    //<editor-fold desc="PROPERTIES" defaulstate="collapsed">
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGoogleMapsId() {
        return googleMapsId;
    }

    public void setGoogleMapsId(String googleMapsId) {
        this.googleMapsId = googleMapsId;
    }
    //</editor-fold>
}
