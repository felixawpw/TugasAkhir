package com.felixawpw.indoormaps.util;

import com.felixawpw.indoormaps.mirror.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerNode {
    private Marker mark;
    private MarkerNode parent;
    private int targetedMapId;

    public MarkerNode(Marker map, int targetedMapId) {
        this.mark = map;
        this.targetedMapId = targetedMapId;
    }

    public Marker getMark() {
        return mark;
    }

    public void setMark(Marker mark) {
        this.mark = mark;
    }

    public MarkerNode getParent() {
        return parent;
    }

    public void setParent(MarkerNode parent) {
        this.parent = parent;
    }

    public int getTargetedMapId() {
        return targetedMapId;
    }

    public void setTargetedMapId(int targetedMapId) {
        this.targetedMapId = targetedMapId;
    }
}
