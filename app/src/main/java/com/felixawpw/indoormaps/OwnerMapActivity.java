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
import android.view.MenuItem;
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
import java.util.List;

public class OwnerMapActivity extends AppCompatActivity {

    public static final String TAG = MapActivity.class.getSimpleName();
    private Toolbar toolbar;
    public static String mapId = null;
    private Context context;
    private PinView imagePlan;
    private Map map;
    DynamicListView mDynamicListView;
    LinearLayout mainLayout;
    EditText searchField;
    MarkerAdapter mMarkerListAdapter;
    private ArrayList<Marker> markerData;
    Button buttonAddMarker;
    PointF startDummy = new PointF(538, 248);
    int showMarkerOnStartId = -1;

    public Marker getMarkerDataByIndex(int position) {
        return markerData.get(position);
    }

    //<editor-fold desc="Initiate" defaultstate="collapsed">

    View.OnClickListener buttonAddMarkerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddMarkerWizardActivity.class);
            intent.putExtra("mapId", map.getId());
            context.startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_map);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        Intent intent = getIntent();
        map = new Map(
                intent.getIntExtra("mapId", 0),
                intent.getStringExtra("nama"),
                intent.getStringExtra("deskripsi"),
                intent.getStringExtra("processedPath"),
                intent.getStringExtra("originalPath"),
                intent.getFloatExtra("height", 0f),
                intent.getFloatExtra("scaleWidth", 0f),
                intent.getFloatExtra("scaleLength", 0f)
        );
        if (intent.hasExtra("show_on_start_marker_id")) {
            showMarkerOnStartId = intent.getIntExtra("show_on_start_marker_id", -1);
        }


        context = this;
        imagePlan = findViewById(R.id.activity_owner_map_view_image_plan);
        mDynamicListView = (DynamicListView) findViewById(R.id.activity_owner_map_list_view);
        mainLayout = findViewById(R.id.activity_owner_map_main_layout);
        searchField = (EditText) findViewById(R.id.activity_owner_map_search_field);
        buttonAddMarker = findViewById(R.id.activity_owner_map_view_button_add_marker);
        buttonAddMarker.setOnClickListener(buttonAddMarkerListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(map.getNama());

        loadMapImage(map);
        loadArrayMapData(map);
        getAllMarkers();

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
                    if (nearestMarker != null)
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
    }
    //</editor-fold>

    public Marker findNearestMarker(PointF point) {
        Log.i(TAG, "Selected plan index = " + selectedPlanIndex);
        double lowestDistance = Double.MAX_VALUE;
        Marker lowestDistancedMarker = null;
        for (Marker marker : markerData) {
            double dist = 0d;
            dist = Math.sqrt(Math.pow(marker.getPointX() - point.x, 2) + Math.pow(marker.getPointY() - point.y, 2));
            dist = Math.min(lowestDistance, dist);

            if (dist < lowestDistance) {
                lowestDistance = dist;
                lowestDistancedMarker = marker;
            }
            Log.d(TAG, String.format("Distance for marker %s = %s. Current iteration lowestDistance = %s", marker.getMapId(), dist, lowestDistance));
        }

        return lowestDistancedMarker;
    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int GET_ALL_MARKERS_DATA = 1;
    public static final int GET_PROCESSED_MAP_DATA = 2;
    public static final String GET_ALL_MARKERS_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/by_map_id/";
    public static final String GET_PROCESSED_MAP_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/map/map_array_data/";

    public void loadArrayMapData(Map map) {
        VolleyServices.getInstance(context).httpRequest(Request.Method.GET,
                GET_PROCESSED_MAP_DATA_ADDRESS + map.getId(),
                context,
                OwnerMapActivity.this,
                GET_PROCESSED_MAP_DATA,
                null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
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
                        if (showMarkerOnStartId != -1)
                            showMarkerDetailDialog(showMarkerOnStartId);
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
                    map.setArrayData(mapData);

                    Log.d(TAG, "Get Array Data = " + map.getArrayData().length + " : " + map.getArrayData()[0].length);
                    break;

                default:
                    break;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }

    }

    public Marker searchMarkerById(int id) {
        for (Marker mark : markerData) {
            if (mark.getId() == id)
                return mark;
        }
        return null;
    }

    public Marker searchMarkerByPosition(int position) {
        return markerData.get(position);
    }

    public void getAllMarkers() {
        try {
            VolleyServices.getInstance(this).httpRequest(
                    Request.Method.GET,
                    GET_ALL_MARKERS_DATA_ADDRESS + map.getId(),
                    this,
                    OwnerMapActivity.this,
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
        LoadImage loadImage = new LoadImage(imagePlan, map);
        loadImage.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + map.getId());
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


    public void showMarkerDetailDialog(int id) {
        final Marker marker = searchMarkerById(id);
        Log.i(TAG, "Clicked marker id = " + marker.getId());
        View v = getLayoutInflater().inflate(R.layout.dialog_marker_detail_owner, null);
        RobotoTextView textName = v.findViewById(R.id.dialog_marker_detail_owner_text_name);
        RobotoTextView textDescription = v.findViewById(R.id.dialog_marker_detail_owner_text_description);
        RobotoTextView textType = v.findViewById(R.id.dialog_marker_detail_owner_text_type);
        Button buttonEdit = v.findViewById(R.id.dialog_marker_detail_owner_button_edit);
        Button buttonDelete = v.findViewById(R.id.dialog_marker_detail_owner_button_delete);

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

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.getCustomImage() == null || map.getArrayData() == null) {

                } else {
                    Toast.makeText(OwnerMapActivity.this, "Click edit button", Toast.LENGTH_SHORT).show();
                    fbDialogue.hide();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.getCustomImage() == null || map.getArrayData() == null) {

                } else {
                    Toast.makeText(OwnerMapActivity.this, "Click delete button", Toast.LENGTH_SHORT).show();
                    fbDialogue.hide();
                }
            }
        });

        fbDialogue.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fbDialogue.getWindow().setGravity(Gravity.BOTTOM);
        fbDialogue.show();

        imagePlan.setPin(new PointF(marker.getPointX(), marker.getPointY()));
    }


}
