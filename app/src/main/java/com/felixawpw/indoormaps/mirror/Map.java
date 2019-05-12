package com.felixawpw.indoormaps.mirror;

import android.graphics.Bitmap;

import com.felixawpw.indoormaps.navigation.ImageCustom;

import org.json.JSONException;
import org.json.JSONObject;

public class Map {
    private int id;
    private String nama;
    private String deskripsi;
    private String processedPath;
    private String originalPath;
    private float height;
    private float scaleWidth;
    private float scaleLength;

    private ImageCustom customImage;
    private int[][] arrayData;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getprocessedPath() {
        return processedPath;
    }

    public void setprocessedPath(String processedPath) {
        this.processedPath = processedPath;
    }

    public String getoriginalPath() {
        return originalPath;
    }

    public void setoriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public Map() {

    }

    public Map(int id, String nama, String deskripsi, String processedPath, String originalPath, float height, float scaleWidth, float scaleLength) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.processedPath = processedPath;
        this.originalPath = originalPath;
        this.height = height;
        this.scaleLength = scaleLength;
        this.scaleWidth = scaleWidth;
    }

    public Map(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.nama = json.getString("nama");
        this.deskripsi = json.getString("deskripsi");
        this.processedPath = json.getString("processed_path");
        this.originalPath = json.getString("original_path");
        this.height = (float)json.getDouble("height");
        this.scaleLength = (float)json.getDouble("scale_length");
        this.scaleWidth = (float)json.getDouble("scale_width");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }

    public void getScaleWidth(float scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public float getScaleLength() {
        return scaleLength;
    }

    public void getScaleLength(float scale_height) {
        this.scaleLength = scale_height;
    }

    public ImageCustom getCustomImage() {
        return customImage;
    }

    public void setCustomImage(ImageCustom processedImage) {
        this.customImage = processedImage;
    }

    public int[][] getArrayData() {
        return arrayData;
    }

    public void setArrayData(int[][] arrayData) {
        this.arrayData = arrayData;
    }
}
