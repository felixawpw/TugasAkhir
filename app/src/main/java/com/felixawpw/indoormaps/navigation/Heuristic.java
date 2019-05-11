package com.felixawpw.indoormaps.navigation;

public class Heuristic {
    public double getCost(GridMap map, int x, int y, int xGoal, int yGoal)
    {
        double dx = Math.abs(xGoal - x);
        double dy = Math.abs(yGoal - y);
        double D = 1;
        double D2 = 1;
        return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx,dy);
    }
}
