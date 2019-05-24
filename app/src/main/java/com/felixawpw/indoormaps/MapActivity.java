package com.felixawpw.indoormaps;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.util.LruCache;
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
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.felixawpw.indoormaps.adapter.DefaultAdapter;
import com.felixawpw.indoormaps.adapter.MarkerAdapter;
import com.felixawpw.indoormaps.adapter.MarkerListAdapter;
import com.felixawpw.indoormaps.barcode.BarcodeCaptureActivity;
import com.felixawpw.indoormaps.dialog.LoadingDialog;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.fragment.MapListFragment;
import com.felixawpw.indoormaps.fragment.MapListOwnerFragment;
import com.felixawpw.indoormaps.fragment.MapViewFragment;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.navigation.Agent;
import com.felixawpw.indoormaps.navigation.GridMap;
import com.felixawpw.indoormaps.navigation.ImageCustom;
import com.felixawpw.indoormaps.navigation.ProcessedImage;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.DummyContent;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.felixawpw.indoormaps.view.FloatLabeledEditText;
import com.felixawpw.indoormaps.view.PagerSlidingTabStrip;
import com.felixawpw.indoormaps.view.PinView;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
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
    public static final int BARCODE_READER_REQUEST_CODE = 1;
    LoadingDialog firstLoadDialog;

    PointF startDummy = new PointF(538, 248);

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
                    showMarkerDetailDialog(nearestMarker.getId());
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
        public static final int GET_PROCESSED_MAP_DATA = 3;
        public static final String GET_ALL_MARKERS_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/by_tenant_id/";
        public static final String REQUEST_MAPS_BY_TENANT_GID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/tenant/map/";
        public static final String GET_PROCESSED_MAP_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/map/map_array_data/";
        public void requestMapsFromServer() {
            firstLoadDialog = new LoadingDialog(this, "Downloading data from server . . .", "Please wait . . .");
            firstLoadDialog.show();
            VolleyServices.getInstance(context).httpRequest(Request.Method.GET,  REQUEST_MAPS_BY_TENANT_GID_ADDRESS + MapActivity.placeId, context, MapActivity.this, REQUEST_MAPS_FROM_SERVER, null);
        }

        public void loadArrayMapData(Map map) {
            VolleyServices.getInstance(context).httpRequest(Request.Method.GET,
                    GET_PROCESSED_MAP_DATA_ADDRESS + map.getId(),
                    context,
                    MapActivity.this,
                    GET_PROCESSED_MAP_DATA,
                    null);
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
                            loadMapImage(maps[i]);
                            loadArrayMapData(maps[i]);
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
                                    list.add(new MarkerModel(i,
                                            "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
                                            marker.getName(),
                                            R.string.fontello_heart_empty,
                                            marker));
                                }
                                mMarkerListAdapter.addAll(list);
                                appearanceAnimate(0);
                            }
                            firstLoadDialog.dismiss();

                        } catch (JSONException ex) {
                            Log.e(TAG, "Error handling response : " + ex.getMessage());
                        }
                        break;
                    case GET_PROCESSED_MAP_DATA:
                        boolean status = response.getBoolean("status");
                        int mapId = response.getInt("map_id");
                        String data = response.getString("data");

                        String[] token = data.split("\n");
                        int[][] mapData = new int[token.length][];
                        for (int i = 0; i < token.length; i++) {
                            char[] charArray = token[i].toCharArray();
                            mapData[i] = new int[charArray.length];
                            for (int j =0 ; j < charArray.length; j++) {
                                mapData[i][j] = charArray[j] == '1' ? 1 : 0 ;
                            }
                        }
                        Map getMap = searchMapsById(mapId);
                        getMap.setArrayData(mapData);

                        Log.d(TAG, "Get Array Data = " + getMap.getArrayData().length + " : " + getMap.getArrayData()[0].length);
                        break;
                    default:
                        break;
                }
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }
        }

        public Map searchMapsById(int id) {
            for (int i = 0; i < maps.length; i++) {
                if (maps[i].getId() == id)
                    return maps[i];
            }
            return null;
        }

        public Marker searchMarkerById(int id) {
            for (Marker mark : markerData) {
                if (mark.getId() == id)
                    return mark;
            }
            return null;
        }

        public ArrayList<Marker> getConnectingMarker() {
            ArrayList<Marker> marks = new ArrayList<>();
            for (Marker mark : markerData) {
                if (mark.getMarkerType() == Marker.TYPE_UPSTAIR || mark.getMarkerType() == Marker.TYPE_DOWNSTAIR)
                    marks.add(mark);
            }
            return marks;
        }

        public Marker searchMarkerByPosition(int position) {
            return markerData.get(position);
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

    public void loadMapImage(Map map) {
        LoadImage loadImage = new LoadImage(null, map, true);
        loadImage.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + map.getId());
    }

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
                LoadImage loadImage = new LoadImage(imagePlan, plan, true);
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
                    LoadImage loadImage = new LoadImage(imagePlan, plan, true);
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

    //<editor-fold desc="NAVIGATION DIALOG" defaultstate="collapsed">

    Dialog spDialog;
    Dialog loadingDialog;
    View viewSpDialog;
    boolean isSelectingStartingPoint = false;
    Marker startingPointMarker = null;
    Marker destinationMarker = null;
    RobotoTextView textStarting;
    RobotoTextView textDestination;

    public void showMarkerDetailDialog(int id) {
        final Marker marker = searchMarkerById(id);
        Log.i(TAG, "Clicked marker id = " + marker.getId());
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

        if (isSelectingStartingPoint)
            buttonDirection.setText("Select As Starting Point");

        buttonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maps[0].getCustomImage() == null || maps[0].getArrayData() == null)
                    Log.i(TAG, "Its not finished loading yet");
                else {
                    if (!isSelectingStartingPoint) {
                        showChooseStartingPointDialog(marker, true);
                    } else {
                        showChooseStartingPointDialog(marker, false);
                    }
                    fbDialogue.dismiss();
                }
            }
        });


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
    public void showChooseStartingPointDialog(Marker marker, boolean isNew) {
        if (isNew) {
            destinationMarker = marker;
            spDialog = new Dialog(this, R.style.MaterialDialogSheet);
            viewSpDialog = getLayoutInflater().inflate(R.layout.dialog_navigation_select_starting_point, null);
            textStarting = viewSpDialog.findViewById(R.id.dialog_navigation_select_starting_point_text_starting);
            textDestination = viewSpDialog.findViewById(R.id.dialog_navigation_select_starting_point_text_destination);
            Button buttonSelectFromMap = viewSpDialog.findViewById(R.id.dialog_navigation_select_starting_point_button_from_map);
            Button buttonScanQR = viewSpDialog.findViewById(R.id.dialog_navigation_select_starting_point_button_from_scan_qr);
            Button buttonNavigate = viewSpDialog.findViewById(R.id.dialog_navigation_select_starting_point_button_navigate);
            textDestination.setText(marker.getName());

            buttonSelectFromMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelectingStartingPoint = true;
                    spDialog.hide();
                    Toast.makeText(MapActivity.this, "Click a marker on map, or select from list to define your starting point.", Toast.LENGTH_LONG).show();
                }
            });

            buttonScanQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelectingStartingPoint = true;
                    Intent intent = new Intent(MapActivity.this, BarcodeCaptureActivity.class);
                    startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                }
            });

            buttonNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (startingPointMarker != null && destinationMarker != null) {
                        navigate(maps[0], startingPointMarker, destinationMarker);
                        startingPointMarker = null;
                        destinationMarker = null;
                        isSelectingStartingPoint = false;
                        spDialog.dismiss();
                    } else {
                        Toast.makeText(MapActivity.this, "Make sure you've selected your starting and destination point.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            spDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            spDialog.setContentView(viewSpDialog);
            spDialog.setCancelable(true);
            spDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            spDialog.getWindow().setGravity(Gravity.BOTTOM);
            spDialog.show();
        } else {
            spDialog.show();
            textStarting.setText(marker.getName());
            startingPointMarker = marker;
        }
    }
    public void createLoadingDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_navigation_loading, null);
        loadingDialog = new Dialog(this, R.style.MaterialDialogSheet);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        loadingDialog.setContentView(v);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        loadingDialog.show();
    }

    public void closeLoadingDialog() {
        loadingDialog.hide();
        loadingDialog.dismiss();
    }



    //</editor-fold>



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String barcodeVal = barcode.displayValue;

                    if (!barcodeVal.equals("")) {
                        String[] token = barcodeVal.split("_");
                        if (token.length == 3) {
                            int markerId = Integer.parseInt(token[2]);
                            showMarkerDetailDialog(markerId);
                        }
                    }
                    Log.i(TAG, "Barcode data = " + barcode.displayValue);
                } else {
                    Log.e(TAG, "Error getting data " + CommonStatusCodes.getStatusCodeString(resultCode));
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void navigate(Map map, Marker startingMarker, Marker end) {
        ImageCustom imageData = map.getCustomImage();
        ProcessedImage pImage = new ProcessedImage(imageData, map.getArrayData());
        GridMap gridMap = new GridMap(pImage, imageData);
        Agent agent = new Agent(gridMap, startingMarker, imagePlan, maps, this);
        agent.execute(end);
        createLoadingDialog();
    }
}
