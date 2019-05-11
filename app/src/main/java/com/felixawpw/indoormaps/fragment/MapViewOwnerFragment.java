package com.felixawpw.indoormaps.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.felixawpw.indoormaps.AddMarkerWizardActivity;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapViewOwnerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Context context;
    private ImageView imagePlan;
    private Button buttonAddMarker;
    Map map;
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static final String TAG = MapViewOwnerFragment.class.getSimpleName();
    private SupportMapFragment mapFragment;

    public MapViewOwnerFragment() {

    }

    public static MapViewOwnerFragment newInstance(Map map) {
        MapViewOwnerFragment fragment = new MapViewOwnerFragment();
        Bundle args = new Bundle();
        fragment.map = map;
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
        View v = inflater.inflate(R.layout.fragment_map_view_owner, container, false);
        context = getContext();

        imagePlan = v.findViewById(R.id.fragment_map_view_owner_image_plan);
        buttonAddMarker = v.findViewById(R.id.fragment_map_view_owner_button_add_marker);
        buttonAddMarker.setOnClickListener(buttonAddMarkerListener);
        ImageUtil.displayImage(imagePlan, VolleyServices.LOAD_MAP_IMAGE_BY_ID + map.getId(), null);

        return v;
    }

    View.OnClickListener buttonAddMarkerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddMarkerWizardActivity.class);
            intent.putExtra("mapId", map.getId());
            context.startActivity(intent);
        }
    };

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    //</editor-fold>
}
