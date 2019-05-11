package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;

import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapViewFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private LinearLayout layoutFloorPlans;
    private Context context;
    private ImageView imagePlan;
    private Map[] maps;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static final String TAG = MapViewFragment.class.getSimpleName();
    private SupportMapFragment mapFragment;

    public MapViewFragment() {

    }

    public static MapViewFragment newInstance(Integer param1) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        context = getContext();
        layoutFloorPlans = v.findViewById(R.id.fragment_map_view_layout_floor_plans);
        imagePlan = v.findViewById(R.id.fragment_map_view_image_plan);
        requestMapsFromServer();
        return v;
    }

//    private Map[] generateDummyMap() {
//        Map[] map = new Map[3];
//        map[0] = new Map(1,"GF", "Lantai GF", "krfrVLF0iEg2iRIq6iUu8M8SYyPvXMoksv9ZUAuD.txt", "public/krfrVLF0iEg2iRIq6iUu8M8SYyPvXMoksv9ZUAuD.jpeg");
//        map[1] = new Map(2,"G", "Lantai G", "", "public/VbuTkEwHEhxbZ4IMzUA7RzvYEHyfMWoeUBbOGnFU.jpeg");
//        map[2] = new Map(3,"1", "Lantai G", "", "public/VbuTkEwHEhxbZ4IMzUA7RzvYEHyfMWoeUBbOGnFU.jpeg");
//
//        return map;
//    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int REQUEST_MAPS_FROM_SERVER = 1;
    public static final String REQUEST_MAPS_BY_TENANT_GID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/tenant/map/";
    public void requestMapsFromServer() {
        VolleyServices.getInstance(context).httpRequest(Request.Method.GET,  REQUEST_MAPS_BY_TENANT_GID_ADDRESS + MapActivity.placeId, context, MapViewFragment.this, REQUEST_MAPS_FROM_SERVER, null);
    }

    public void handleResponse(int requestName, JSONObject response) {
        Log.d(TAG, response.toString());
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
                default:
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }
    //</editor-fold>


    Button prevSelectedPlan = null;
    public void createFloorPlansButton(Map[] plans) {
        int index = 0;
        for (final Map plan : plans) {
            Button button = new Button(context);
            if (index++ == 0)
            {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, getResources().getColor(R.color.main_color_500));
                drawable.setColor(getResources().getColor(R.color.main_color_500_50_percent));
                button.setBackground(drawable);

                prevSelectedPlan = button;
                ImageUtil.displayImage(imagePlan, VolleyServices.LOAD_MAP_IMAGE_BY_ID + plan.getId(), null);
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

                    ImageUtil.displayImage(imagePlan, VolleyServices.LOAD_MAP_IMAGE_BY_ID + plan.getId(), null);
                }
            });
            layoutFloorPlans.addView(button);
        }
    }
}
