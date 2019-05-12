package com.felixawpw.indoormaps.navigation;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.view.PinView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Agent extends AsyncTask<Marker, String, Path> {
    public Path path;
    public PathFinder pathFinder;
    int currentStep;
    int currX, currY;
    PinView imagePlan;
    Map[] maps;
    Marker targetedMarker;
    public static final String TAG = Agent.class.getSimpleName();

    public Agent() {

    }

    public Agent(GridMap map, PointF startPoint, PinView imagePlan, Map[] maps) {
        this.currX = (int)startPoint.x;
        this.currY = (int)startPoint.y;
        this.pathFinder = new PathFinder(map, 500, false);
        this.imagePlan = imagePlan;
        this.maps = maps;
    }

    public Path goTo(int x, int y) {
        currentStep = 0;
        System.out.println("Read goto");
        path = pathFinder.findPath(currY, currX, y, x);
        return path;
    }

    @Override
    protected Path doInBackground(Marker... params) {
        Marker end = params[0];
        targetedMarker = end;
        return goTo(end.getPointX(), end.getPointY());
    }

    @Override
    protected void onPostExecute(Path path) {
        if (path != null) {
            System.out.println("Steps = " + path.steps.size());
            for (Object step : path.steps) {
                Step st = (Step)step;
                Log.d(TAG, String.format("Step %s -> %s", st.getX(), st.getY()));
            }

            if (path.steps.isEmpty()) {
            }
            else {
                PointF[] points = new PointF[path.steps.size()];
                for (int i = 0; i < path.steps.size(); i++) {
                    Step step = (Step)path.steps.get(i);
                    points[i] = new PointF(step.getX(), step.getY());
                }

                Map targetedMap = null;
                for (Map map : maps) {
                    if (map.getId() == targetedMarker.getMapId())
                        targetedMap = map;
                }

                Bitmap parent = targetedMap.getCustomImage().getImage();

                Bitmap tempParent = parent.copy(parent.getConfig(), true);

                Bitmap bitmap = generateImageWithPath(parent.copy(parent.getConfig(), true), points);
                parent.recycle();

                targetedMap.getCustomImage().setImage(tempParent);

                imagePlan.setImage(ImageSource.bitmap(bitmap));

                //imagePlan.refreshDrawableState();
                Log.d(TAG, "Generating path finished");

            }
        }
        else
            System.err.println("Cannot find path");
    }

    public Bitmap generateImageWithPath(Bitmap parent, PointF[] points) {
//        Bitmap bmOverlay = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), parent.getConfig());
//        Canvas canvas = new Canvas(bmOverlay);
//
//        canvas.drawBitmap(parent, 0, 0, null);
        for (int i = 0; i < points.length; i++) {
            changeAdjacentPixelColor(parent, points[i],2, Color.RED);
        }
        return parent;
//        Canvas canvas = new Canvas(bmOverlay);
//        canvas.drawBitmap(parent, 0, 0, null);
//
//        Bitmap pathMarkerResource = Bitmap.createBitmap(me)
//
//        canvas.drawBitmap(bmp2, new Matrix(), null);
//        canvas.drawBitmap();
//        return bmOverlay;
    }

    public void changeAdjacentPixelColor(Bitmap parent, PointF centerPoint, int tileNumber, int color) {
        for (int i = -tileNumber; i < tileNumber; i++) {
            for (int j = -tileNumber; j < tileNumber; j++) {
                if (centerPoint.y + i < parent.getWidth()) {
                    if (centerPoint.x + j < parent.getHeight()) {
                        parent.setPixel((int)(centerPoint.y + i), (int)(centerPoint.x + j), color);
                    }
                }
            }
        }
    }
}
