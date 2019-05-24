package com.felixawpw.indoormaps.navigation;

import java.util.ArrayList;
import java.util.List;

public class ProcessedStep {
    Step start, end;
    double distance;
    double cosAngle;
    double sinAngle;
    double tanAngle;
    double heading;
    private int mapId;

    public ProcessedStep(Step start, Step end, int mapId, double heading) {
        this.start = start;
        this.end = end;
        this.setMapId(mapId);
        this.heading = heading;
        calculateDistance();
        calculateAngleFromO();
    }

    private void calculateDistance() {
        distance = Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    }

    private void calculateAngleFromO() {
        cosAngle = Math.acos((double)(end.getX() - start.getX()) / distance) * 180 / Math.PI;
        sinAngle = Math.asin((double)(end.getY() - start.getY()) / distance) * 180 / Math.PI;
    }

    public String angleToString() {
        int quadrant;
        if (cosAngle >= 0 && sinAngle >= 0)
            quadrant = 1;
        else if (cosAngle < 0 && sinAngle >= 0)
            quadrant = 2;
        else if (cosAngle < 0 && sinAngle < 0)
            quadrant = 3;
        else
            quadrant = 4;

        double heading = this.heading;
        sinAngle = Math.abs(sinAngle);
        switch(quadrant) {
            case 1:
                heading += 0 + sinAngle;
                break;
            case 2:
                heading += 180 - sinAngle;
                break;
            case 3:
                heading += 180 + sinAngle;
                break;
            case 4:
                heading += 360 - sinAngle;
                break;
        }

        String result = String.format("Head to %s for %s meter(s)", Math.round(heading), (double)Math.round(distance * 100) / 100);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Start (%s,%s) : End (%s, %s) || Distance = %s\nCos = %s ; Sin = %s ; Tan = %s", start.getX(), start.getY(), end.getX(), end.getY(), distance,
                cosAngle, sinAngle, tanAngle);
    }

    public static List<ProcessedStep> trimSteps(List<ProcessedStep> pSteps) {
        List<ProcessedStep> result = new ArrayList<>();
        int distanceTreshold = 5;

        for (int i = 0; i < pSteps.size(); i++) {
            ProcessedStep temp = pSteps.get(i);
            if (temp.distance <= distanceTreshold) {
                ProcessedStep startTemp = temp;
                while (i < pSteps.size() - 1) {
                    if (pSteps.get(i).distance > distanceTreshold)
                        break;
                    i++;
                }
                result.add(new ProcessedStep(startTemp.start, pSteps.get(i - 1).end, pSteps.get(i - 1).getMapId(), pSteps.get(i - 1).heading));
                result.add(pSteps.get(i));
            } else
                result.add(temp);
        }
        return result;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
