package com.felixawpw.indoormaps.navigation;

import com.felixawpw.indoormaps.mirror.Marker;

import java.util.ArrayList;

public class Path {
    public ArrayList steps = new ArrayList();
    public Marker start, end;
    public int mapId;
    public Path()
    {

    }
}
