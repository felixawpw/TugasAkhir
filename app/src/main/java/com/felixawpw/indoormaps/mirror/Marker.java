package com.felixawpw.indoormaps.mirror;

import org.json.JSONException;
import org.json.JSONObject;

public class Marker {
    public static final int TYPE_PUBLIC = 1;
    public static final int TYPE_UPSTAIR = 2;
    public static final int TYPE_DOWNSTAIR = 3;
    public static final int TYPE_STAIR_UP_END = 4;
    public static final int TYPE_STAIR_DOWN_END = 5;

    public static final int TYPE_TOILET = 6;
    public static final int TYPE_SCAN_POINT = 7;

    private String name;
    private String description;
    private int id;
    private int pointX;
    private int pointY;
    private int markerType;
    private int mapId;
    private int targetedMarkerId;
    private float heading;
    private int calibrate_x;
    private int calibrate_y;
    public Marker() {

    }

    public Marker(JSONObject data) throws JSONException {
        this.setId(data.getInt("id"));
        this.setName(data.getString("name"));
        this.setDescription(data.getString("description"));
        this.setPointX(data.getInt("point_x"));
        this.setPointY(data.getInt("point_y"));
        this.setMarkerType(data.getInt("marker_type"));
        this.setMapId(data.getInt("map_id"));
        if (data.get("connecting_marker_id") != JSONObject.NULL)
            this.setTargetedMarkerId(data.getInt("connecting_marker_id"));
        if (data.get("heading") != JSONObject.NULL)
            this.heading = ((float)data.getDouble("heading"));
        if (data.get("calibrate_x") != JSONObject.NULL)
            this.calibrate_x = data.getInt("calibrate_x");
        if (data.get("calibrate_y") != JSONObject.NULL)
            this.calibrate_y = data.getInt("calibrate_y");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public int getMarkerType() {
        return markerType;
    }

    public void setMarkerType(int markerType) {
        this.markerType = markerType;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getTargetedMarkerId() {
        return targetedMarkerId;
    }

    public void setTargetedMarkerId(int targetedMarkerId) {
        this.targetedMarkerId = targetedMarkerId;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public int getCalibrate_x() {
        return calibrate_x;
    }

    public void setCalibrate_x(int calibrate_x) {
        this.calibrate_x = calibrate_x;
    }

    public int getCalibrate_y() {
        return calibrate_y;
    }

    public void setCalibrate_y(int calibrate_y) {
        this.calibrate_y = calibrate_y;
    }
}
