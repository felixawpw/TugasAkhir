package com.felixawpw.indoormaps.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.adapter.MarkerAdapter;
import com.felixawpw.indoormaps.adapter.PathAdapter;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.navigation.Path;
import com.felixawpw.indoormaps.navigation.ProcessedStep;
import com.felixawpw.indoormaps.navigation.Step;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.CacheManager;
import com.felixawpw.indoormaps.view.PinView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutesDialog {
    Dialog routeDialog;
    LinearLayout layoutFloorPlans;
    PinView imagePlan;
    DynamicListView listRoutes;
    PathAdapter stepsAdapter;
    Button previousFloorButton, nextFloorButton;
    RobotoTextView textFloorName;
    List<Path> paths;
    Map[] maps;
    public static final String TAG = RoutesDialog.class.getSimpleName();

    public RoutesDialog(Activity activity, Map[] maps, List<Path> paths) {
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_show_routes, null);
//        layoutFloorPlans = v.findViewById(R.id.dialog_show_routes_layout1);
        imagePlan = v.findViewById(R.id.dialog_show_routes_image_plan);
        listRoutes = v.findViewById(R.id.dialog_show_routes_list_view);
        previousFloorButton = v.findViewById(R.id.dialog_show_routes_button_previous);
        nextFloorButton = v.findViewById(R.id.dialog_show_routes_button_next);
        textFloorName = v.findViewById(R.id.dialog_show_routes_floor_name);

        previousFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayedMapIndex = Math.max(displayedMapIndex - 1, 0);

                if (displayedMapIndex == 0) {
                    previousFloorButton.setEnabled(false);
                }
                nextFloorButton.setEnabled(true);
                setDisplayedMap(displayedMapIndex);
            }
        });

        nextFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayedMapIndex = Math.min(displayedMapIndex + 1, orderedMapList.size() - 1);

                if (displayedMapIndex == orderedMapList.size() - 1) {
                    nextFloorButton.setEnabled(false);
                }
                previousFloorButton.setEnabled(true);

                setDisplayedMap(displayedMapIndex);
            }
        });

        this.maps = maps;
        this.paths = paths;

        generatePath();
        routeDialog = new Dialog(activity, R.style.MaterialDialogSheet);
        routeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        routeDialog.setContentView(v);
        routeDialog.setCancelable(true);

        routeDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        routeDialog.getWindow().setGravity(Gravity.BOTTOM);

        List<ProcessedStep> processedSteps = generateSteps();

        stepsAdapter = new PathAdapter(activity, processedSteps, false, activity);
        appearanceAnimate(0);
    }

    HashMap<Map, Bitmap> mapBitmapByMaps;
    List<Map> orderedMapList;
    int displayedMapIndex;
    public void generatePath() {
        mapBitmapByMaps = new HashMap<>();
        orderedMapList = new ArrayList<>();
        displayedMapIndex = 0;

        Log.i(TAG, "Paths size = " + paths.size());
        for (Path path : paths) {
            Log.d(TAG, "Paths = " + path.start.getName() + " to " + path.end.getName());
            Map targetedMap = null;
            for (Map map : maps) {
                if (map.getId() == path.start.getMapId())
                    targetedMap = map;
            }
            orderedMapList.add(targetedMap);
            Bitmap parent = CacheManager.getInstance().getBitmapFromMemCache(CacheManager.CACHE_MAP_KEY_PREFIX + targetedMap.getId());
            Bitmap bitmap = null;
            if (path.steps.isEmpty()) {
                bitmap = parent.copy(parent.getConfig(), true);
            }
            else {
                PointF[] points = new PointF[path.steps.size()];
                for (int i = 0; i < path.steps.size(); i++) {
                    Step step = (Step)path.steps.get(i);
                    points[i] = new PointF(step.getX(), step.getY());
                }
                bitmap = generateImageWithPath(parent.copy(parent.getConfig(), true), points);
            }
            mapBitmapByMaps.put(targetedMap, bitmap);
        }

        setDisplayedMap(displayedMapIndex);
    }

    public List<ProcessedStep> generateSteps() {
        List<ProcessedStep> results = new ArrayList<>();

        for (Path path : paths) {
            List<Step> stepLists = path.steps;
            if (!path.steps.isEmpty()) {
                Step prevStep = stepLists.get(0);
                boolean xIsChanged = stepLists.get(0).getX() - stepLists.get(1).getX() != 0;
                Step lastChangedStep = stepLists.get(0);
                List<ProcessedStep> processedStepLists = new ArrayList<>();

                for (int i = 0; i < stepLists.size(); i++) {
                    Step st = stepLists.get(i);

                    if (!xIsChanged) {
                        if (prevStep.getX() != st.getX()) {
                            xIsChanged = true;
                            processedStepLists.add(new ProcessedStep(lastChangedStep, prevStep, path.mapId, 0));
                            lastChangedStep = st;
                        }
                    } else {
                        if (prevStep.getY() != st.getY()) {
                            xIsChanged = false;
                            processedStepLists.add(new ProcessedStep(lastChangedStep, prevStep, path.mapId, 0));
                            lastChangedStep = st;
                        }
                    }
                    prevStep = st;
                }
                processedStepLists.add(new ProcessedStep(lastChangedStep, prevStep, path.mapId, 0));

                List<ProcessedStep> trimmed = ProcessedStep.trimSteps(processedStepLists);
                results.addAll(trimmed);
            }
        }

        return results;
    }

    public void setDisplayedMap(int mapIndex) {
        Bitmap bitmap = mapBitmapByMaps.get(orderedMapList.get(mapIndex));
        imagePlan.setImage(ImageSource.cachedBitmap(bitmap));
        displayedMapIndex = mapIndex;
    }

    public Bitmap generateImageWithPath(Bitmap parent, PointF[] points) {
        for (int i = 0; i < points.length; i++) {
            changeAdjacentPixelColor(parent, points[i],2, Color.RED);
        }
        return parent;
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

    private void appearanceAnimate(int key) {
        AnimationAdapter animAdapter;
        switch (key) {
            default:
            case 0:
                animAdapter = new AlphaInAnimationAdapter(stepsAdapter);
                break;
            case 1:
                animAdapter = new ScaleInAnimationAdapter(stepsAdapter);
                break;
            case 2:
                animAdapter = new SwingBottomInAnimationAdapter(stepsAdapter);
                break;
            case 3:
                animAdapter = new SwingLeftInAnimationAdapter(stepsAdapter);
                break;
            case 4:
                animAdapter = new SwingRightInAnimationAdapter(stepsAdapter);
                break;
        }
        animAdapter.setAbsListView(listRoutes);
        listRoutes.setAdapter(animAdapter);
    }

    public void show() {
        routeDialog.show();
    }

    public void hide() {
        routeDialog.hide();
    }
}
