package com.felixawpw.indoormaps;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.felixawpw.indoormaps.adapter.DefaultAdapter;
import com.felixawpw.indoormaps.adapter.MarkerAdapter;
import com.felixawpw.indoormaps.adapter.MarkerListAdapter;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.fragment.MapListFragment;
import com.felixawpw.indoormaps.fragment.MapListOwnerFragment;
import com.felixawpw.indoormaps.fragment.MapViewFragment;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.DummyContent;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.felixawpw.indoormaps.view.PagerSlidingTabStrip;
import com.felixawpw.indoormaps.view.PinView;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    public static final String TAG = MapActivity.class.getSimpleName();
    private Toolbar toolbar;
    public static String placeId = null;
    private LinearLayout layoutFloorPlans;
    private Context context;
    private PinView imagePlan;
    private Map[] maps;
    DynamicListView mDynamicListView;
    LinearLayout mainLayout;
    EditText searchField;
    MarkerAdapter mMarkerListAdapter;
    private ArrayList<Marker> markerData;

    public Marker getMarkerDataByIndex(int position) {
        return markerData.get(position);
    }

    //<editor-fold desc="Initiate" defaultstate="collapsed">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");

        context = this;
        layoutFloorPlans = findViewById(R.id.activity_map_view_layout_floor_plans);
        imagePlan = findViewById(R.id.activity_map_view_image_plan);
        requestMapsFromServer();

        mDynamicListView = (DynamicListView) findViewById(R.id.activity_map_list_view);
        mainLayout = findViewById(R.id.activity_map_main_layout);
        searchField = (EditText) findViewById(R.id.activity_map_search_field);

        markerData = new ArrayList<>();
        //Implement Search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMarkerListAdapter != null) {
                    mMarkerListAdapter.getFilter().filter(s);
                } else {
                    Log.d(TAG, "no filter availible");
                }
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imagePlan.isReady()) {
                    PointF sCoord = imagePlan.viewToSourceCoord(e.getX(),
                            e.getY());
                    Log.i(TAG, "Selected plan index = " + selectedPlanIndex);
                    Marker nearestMarker = findNearestMarker(sCoord);
                    showMarkerDetailDialog(nearestMarker);
                }
                return true;
            }
        });
        imagePlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        mainLayout.requestFocus();
        getAllMarkers();
    }
    //</editor-fold>

    public Marker findNearestMarker(PointF point) {
        Log.i(TAG, "Selected plan index = " + selectedPlanIndex);
        double lowestDistance = Double.MAX_VALUE;
        Marker lowestDistancedMarker = null;
        for (Marker marker : markerData) {
            double dist = 0d;
            if (marker.getMapId() == maps[selectedPlanIndex].getId()) {
                dist = Math.sqrt(Math.pow(marker.getPointX() - point.x, 2) + Math.pow(marker.getPointY() - point.y, 2));
                dist = Math.min(lowestDistance, dist);

                if (dist < lowestDistance) {
                    lowestDistance = dist;
                    lowestDistancedMarker = marker;
                }
                Log.d(TAG, String.format("Distance for marker %s = %s. Current iteration lowestDistance = %s", marker.getMapId(), dist, lowestDistance));
            }
        }

        return lowestDistancedMarker;
    }

     //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
        public static final int REQUEST_MAPS_FROM_SERVER = 1;
        public static final int GET_ALL_MARKERS_DATA = 2;
        public static final String GET_ALL_MARKERS_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/by_tenant_id/";
        public static final String REQUEST_MAPS_BY_TENANT_GID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/tenant/map/";
        public void requestMapsFromServer() {
            VolleyServices.getInstance(context).httpRequest(Request.Method.GET,  REQUEST_MAPS_BY_TENANT_GID_ADDRESS + MapActivity.placeId, context, MapActivity.this, REQUEST_MAPS_FROM_SERVER, null);
        }

        public void handleResponse(int requestName, JSONObject response) {
            Log.d(TAG, "Response = " + response.toString());
            try {
                switch (requestName) {
                    case MapActivity.REQUEST_MAPS_FROM_SERVER:
                        JSONArray mapsJson = response.getJSONArray("data");
                        maps = new Map[mapsJson.length()];

                        for (int i = 0; i < mapsJson.length(); i++) {
                            maps[i] = new Map(mapsJson.getJSONObject(i));
                        }

                        createFloorPlansButton(maps);
                        break;
                    case GET_ALL_MARKERS_DATA:
                        try {
                            Log.i(TAG, "Response = " + response.toString());
                            boolean status = response.getBoolean("status");
                            JSONArray data = response.getJSONArray("data");
                            if (status) {
                                ArrayList<MarkerModel> list = new ArrayList<>();
                                mMarkerListAdapter = new MarkerAdapter(this, list, false, this);
                                JSONArray tenants = data;
                                for (int i = 0; i < tenants.length(); i++) {
                                    Marker marker = new Marker(tenants.getJSONObject(i));
                                    markerData.add(marker);
                                    list.add(new MarkerModel(i+1,
                                            "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
                                            marker.getName(),
                                            R.string.fontello_heart_empty,
                                            marker));
                                }
                                mMarkerListAdapter.addAll(list);
                                appearanceAnimate(0);
                            }
                        } catch (JSONException ex) {
                            Log.e(TAG, "Error handling response : " + ex.getMessage());
                        }
                        break;

                    default:
                        break;
                }
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void getAllMarkers() {
            try {
                VolleyServices.getInstance(this).httpRequest(
                        Request.Method.GET,
                        GET_ALL_MARKERS_DATA_ADDRESS + placeId,
                        this,
                        MapActivity.this,
                        GET_ALL_MARKERS_DATA,
                        null);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

    //</editor-fold>

    Button prevSelectedPlan = null;
    int selectedPlanIndex = 0;
    Button planButtons[];
    public void createFloorPlansButton(Map[] plans) {
        int index = 0;
        planButtons = new Button[plans.length];
        int i = 0;
        for (final Map plan : plans) {
            final Button button = new Button(context);
            planButtons[i] = button;
            i++;

            if (index++ == 0)
            {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, getResources().getColor(R.color.main_color_500));
                drawable.setColor(getResources().getColor(R.color.main_color_500_50_percent));
                button.setBackground(drawable);

                prevSelectedPlan = button;
                LoadImage loadImage = new LoadImage(imagePlan, true);
                loadImage.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + plan.getId());
            }
            else
                button.setBackgroundColor(getResources().getColor(R.color.main_color_500));

            button.setText(plan.getNama());

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

            button.setLayoutParams(p);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Button)v == prevSelectedPlan)
                        return;

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setStroke(5, getResources().getColor(R.color.main_color_500));
                    drawable.setColor(getResources().getColor(R.color.main_color_500_50_percent));
                    v.setBackground(drawable);

                    if (prevSelectedPlan != null)
                        prevSelectedPlan.setBackgroundColor(getResources().getColor(R.color.main_color_500));

                    prevSelectedPlan = (Button)v;
                    LoadImage loadImage = new LoadImage(imagePlan, true);
                    loadImage.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + plan.getId());

                    for (int i = 0; i < planButtons.length; i++) {
                        if ((Button)v == planButtons[i])
                            selectedPlanIndex = i;

                    }
                }
            });
            layoutFloorPlans.addView(button);
        }
    }

    private void appearanceAnimate(int key) {
        AnimationAdapter animAdapter;
        switch (key) {
            default:
            case 0:
                animAdapter = new AlphaInAnimationAdapter(mMarkerListAdapter);
                break;
            case 1:
                animAdapter = new ScaleInAnimationAdapter(mMarkerListAdapter);
                break;
            case 2:
                animAdapter = new SwingBottomInAnimationAdapter(mMarkerListAdapter);
                break;
            case 3:
                animAdapter = new SwingLeftInAnimationAdapter(mMarkerListAdapter);
                break;
            case 4:
                animAdapter = new SwingRightInAnimationAdapter(mMarkerListAdapter);
                break;
        }
        animAdapter.setAbsListView(mDynamicListView);
        mDynamicListView.setAdapter(animAdapter);
    }

    public void showMarkerDetailDialog(Marker selectedMarker) {
        Marker marker = markerData.get(markerData.indexOf(selectedMarker));

        View v = getLayoutInflater().inflate(R.layout.fragment_marker_detail_popup, null);
        RobotoTextView textName = v.findViewById(R.id.fragment_marker_detail_popup_text_name);
        RobotoTextView textDescription = v.findViewById(R.id.fragment_marker_detail_popup_text_description);
        RobotoTextView textType = v.findViewById(R.id.fragment_marker_detail_popup_text_type);
        Button buttonDirection = v.findViewById(R.id.fragment_marker_detail_popup_button_direction);

        textName.setText(marker.getName());
        textDescription.setText(marker.getDescription());

        int type = marker.getMarkerType();
        textType.setText(type == Marker.TYPE_PUBLIC ? "Public Places" :
                        type == Marker.TYPE_UPSTAIR ? "Stair up" :
                        type == Marker.TYPE_DOWNSTAIR ? "Stair down" :
                        type == Marker.TYPE_STAIR_UP_END ? "Stair up end" :
                        type == Marker.TYPE_STAIR_DOWN_END  ? "Stair down" : "Undefined");

        final Dialog fbDialogue = new Dialog(this, R.style.MaterialDialogSheet);
        fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        fbDialogue.setContentView(v);
        fbDialogue.setCancelable(true);

        fbDialogue.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fbDialogue.getWindow().setGravity(Gravity.BOTTOM);
        fbDialogue.show();

        imagePlan.setPin(new PointF(marker.getPointX(), marker.getPointY()));

        for (int i = 0; i < maps.length; i++) {
            if (marker.getMapId() == maps[i].getId()) {
                planButtons[i].performClick();
            }
        }
    }


    //<editor-fold desc="COMMENTED">
    /*
    private MarkerListAdapter mMarkerListAdapter;
    private EditText searchField;
    private static final int INITIAL_DELAY_MILLIS = 300;

    public void initiateListView() {

        searchField = (EditText) findViewById(R.id.activity_map_search_field);
        //Implement Search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMarkerListAdapter != null) {
                    mMarkerListAdapter.getFilter().filter(s);
                } else {
                    Log.d(TAG, "no filter availible");
                }
            }
        });

        ListView listView = (ListView) findViewById(R.id.activity_map_list_view);

        //Get Google Cards Content
        mMarkerListAdapter = new MarkerListAdapter(context, new ArrayList<MarkerModel>());

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                new SwipeDismissAdapter(mMarkerListAdapter, null));
        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);

        listView.setClipToPadding(false);
        listView.setDivider(null);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, r.getDisplayMetrics());
        listView.setDividerHeight(px);
        listView.setFadingEdgeLength(0);
        listView.setFitsSystemWindows(true);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                r.getDisplayMetrics());
        listView.setPadding(px, px, px, px);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setAdapter(swingBottomInAnimationAdapter);

    }
    */
    //</editor-fold>

}
