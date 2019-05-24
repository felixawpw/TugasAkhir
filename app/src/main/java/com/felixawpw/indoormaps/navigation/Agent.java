package com.felixawpw.indoormaps.navigation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.dialog.RoutesDialog;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.util.DirectedGraph;
import com.felixawpw.indoormaps.util.MarkerNode;
import com.felixawpw.indoormaps.view.PinView;
import com.google.common.graph.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Agent extends AsyncTask<Marker, String, List<Path>> {
    public PathFinder pathFinder;
    int currentStep;
    int currX, currY;
    PinView imagePlan;
    Map[] maps;
    Marker targetedMarker;
    Marker startPoint;
    public static final String TAG = Agent.class.getSimpleName();
    Activity activity;

    public Agent() {

    }

    public Agent(GridMap map, Marker startPoint, PinView imagePlan, Map[] maps, Activity activity) {
        this.startPoint = startPoint;
        this.currX = startPoint.getPointX();
        this.currY = startPoint.getPointY();
        this.pathFinder = new PathFinder(map, 500, false);
        this.imagePlan = imagePlan;
        this.maps = maps;
        this.activity = activity;
    }

//    public void searchStairs(Marker start, Marker destination) {
//        ArrayList<Marker> connectingMarker = ((MapActivity)activity).getConnectingMarker();
//        DirectedGraph graph = new DirectedGraph();
//
//        for (Marker mark : connectingMarker) {
//            Marker targetedMarker = ((MapActivity)activity).searchMarkerById(mark.getTargetedMarkerId());
//            graph.addEdge(mark, targetedMarker);
//        }
//
//        Marker parentNode = graph.isReachable(start, destination);
//        Log.d(TAG, "Stair search result = " + parentNode.getId());
//        int i = 0;
//        while (parentNode.getParent() != null) {
//            Log.i(TAG, "Iteration " + i + " : content " + parentNode.getMarker().getMapId());
//            parentNode = parentNode.getParent();
//            i++;
//        }
//        Log.i(TAG, "Is map connected : " + parentNode);
//    }

    public Path goTo(Marker start, Marker goal, int mapId) {
        currentStep = 0;
        System.out.println("Read goto");
        Path path = pathFinder.findPath(start.getPointY(), start.getPointX(), goal.getPointY(), goal.getPointX());
        path.start = start;
        path.mapId = mapId;
        path.end = goal;
        return path;
    }

    @Override
    protected List<Path> doInBackground(Marker... params) {
        Marker end = params[0];
        targetedMarker = end;
        ArrayList<Marker> connectingMarker = ((MapActivity)activity).getConnectingMarker();
        List<Path> paths = new ArrayList<>();

        if (startPoint.getMapId() == targetedMarker.getMapId()) {
            paths.add(goTo(startPoint, targetedMarker, startPoint.getMapId()));
        } else {
            DirectedGraph directedGraph = new DirectedGraph();

            List<MarkerNode> possibleStartingNodes = new ArrayList<>();

            for (Marker mark : connectingMarker) {
                MarkerNode mNode = new MarkerNode(mark, ((MapActivity)activity).searchMarkerById(mark.getTargetedMarkerId()).getMapId());

                if (mark.getMapId() == startPoint.getMapId())
                    possibleStartingNodes.add(mNode);

                directedGraph.addEdge(mNode);
            }

            List<Marker> markerPaths = new ArrayList<>();
            for (MarkerNode pS : possibleStartingNodes) {
                MarkerNode path = directedGraph.bfs(pS, end.getMapId());

                if (path == null)
                    continue;
                while (path != null && path.getParent() != null) {
                    Log.i(TAG, "Path : " + path.getMark().getMapId()  + " " + path.getMark().getTargetedMarkerId());
                    markerPaths.add(path.getMark());
                    path = path.getParent();
                }
                markerPaths.add(path.getMark());
                Log.i(TAG, "Path : " + path.getMark().getMapId()  + " " + path.getMark().getTargetedMarkerId());
            }

            Marker startTemp = markerPaths.get(markerPaths.size() - 1);
            paths.add(goTo(startPoint, startTemp, startPoint.getMapId()));
            //Connecting map paths

            Marker prev = null;
            for (int i = 0; i < markerPaths.size(); i++) {
                Marker temp = markerPaths.get(i);
                if (prev != null) {
                    paths.add(1, goTo(((MapActivity)activity).searchMarkerById(temp.getTargetedMarkerId()), prev, ((MapActivity)activity).searchMarkerById(temp.getTargetedMarkerId()).getMapId()));
                    Log.d(TAG, "3rd condition " + temp.getMapId());
                }
                prev = temp;
            }

            Marker endTemp = markerPaths.get(0);
            paths.add(goTo(((MapActivity)activity).searchMarkerById(endTemp.getTargetedMarkerId()), targetedMarker, targetedMarker.getMapId()));

        }
        return paths;
    }


    @Override
    protected void onPostExecute(List<Path> paths) {
        ((MapActivity)activity).closeLoadingDialog();

        if (paths != null) {
            RoutesDialog dialog = new RoutesDialog(activity, maps, paths);
            dialog.show();

//            System.out.println("Steps = " + path.steps.size());
//            for (Object step : path.steps) {
//                Step st = (Step)step;
//                Log.d(TAG, String.format("Step %s -> %s", st.getX(), st.getY()));
//            }
        }
        else
            System.err.println("Cannot find path");
    }
}
