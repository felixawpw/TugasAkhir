package com.felixawpw.indoormaps.mirror;

import org.json.JSONException;
import org.json.JSONObject;

public class Marker {
    public static final int TYPE_PUBLIC = 1;
    public static final int TYPE_UPSTAIR = 2;
    public static final int TYPE_DOWNSTAIR = 3;
    public static final int TYPE_STAIR_UP_END = 4;
    public static final int TYPE_STAIR_DOWN_END = 5;

    private String name;
    private String description;
    private int id;
    private int pointX;
    private int pointY;
    private int markerType;
    private int mapId;
    private int targetedMarkerId;

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
    }

    public static int getTypePublic() {
        return TYPE_PUBLIC;
    }

    public static int getTypeUpstair() {
        return TYPE_UPSTAIR;
    }

    public static int getTypeDownstair() {
        return TYPE_DOWNSTAIR;
    }

    public static int getTypeStairUpEnd() {
        return TYPE_STAIR_UP_END;
    }

    public static int getTypeStairDownEnd() {
        return TYPE_STAIR_DOWN_END;
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
}
