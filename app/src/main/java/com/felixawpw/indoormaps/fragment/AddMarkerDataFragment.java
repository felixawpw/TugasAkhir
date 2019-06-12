package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.felixawpw.indoormaps.AddMarkerWizardActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.PinView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddMarkerDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ArrayAdapter<String> arrayAdapter;
    AddMarkerWizardActivity parentActivity;
    int mapId;
    public PinView imageView;
    MaterialBetterSpinner materialDesignSpinner;

    public static final String TAG = AddMarkerDataFragment.class.getSimpleName();

    public AddMarkerDataFragment() { }
    String[] SPINNERLIST = {"Android Material Design", "Material Design Spinner", "Spinner Using Material Library", "Material Spinner Example"};

    public static AddMarkerDataFragment newInstance(Integer param1, AddMarkerWizardActivity activity) {
        AddMarkerDataFragment fragment = new AddMarkerDataFragment();
        Bundle args = new Bundle();
        fragment.mapId = param1;
        fragment.parentActivity = activity;
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
        View v = inflater.inflate(R.layout.fragment_marker_data, container, false);
        imageView = v.findViewById(R.id.fragment_marker_data_imageMap);
        final GestureDetector gestureDetector = new GestureDetector(parentActivity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageView.isReady()) {
                    PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                    imageView.setPin(sCoord);
                    parentActivity.dataContainer.targetedPinX = sCoord.x;
                    parentActivity.dataContainer.targetedPinY = sCoord.y;
                }
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        materialDesignSpinner = v.findViewById(R.id.fragment_marker_data_spinner_targeted_map);
        getMapsData();

        return v;
    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">

    public static final int GET_MAPS_BY_TENANT_ID = 1;
    public static final String GET_MAPS_BY_TENANT_ID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/maps/by_map_id/";
    List<Map> mapsData = new ArrayList<>();

    public void handleResponse(int requestId, JSONObject response) {
        Log.i(TAG, "Response = " + response);
        switch (requestId) {
            case GET_MAPS_BY_TENANT_ID:
                try {
                    boolean status = response.getBoolean("status");
                    if (status) {
                        JSONArray maps = response.getJSONArray("data");
                        ArrayList<String> mapName = new ArrayList<>();
                        for (int i = 0; i < maps.length(); i++) {
                            Map map = new Map(maps.getJSONObject(i));
                            mapsData.add(map);
                            Log.i(TAG, "Array adapter = " + arrayAdapter);
                            mapName.add(map.getNama());
                        }
                        arrayAdapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, mapName);
                        materialDesignSpinner.setAdapter(arrayAdapter);

                        materialDesignSpinner.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.i(TAG, "Item changed + " + s.toString());
                                Map selectedMap = null;
                                for (Map map : mapsData) {
                                    if (map.getNama().equals(s.toString())) {
                                        selectedMap = map;
                                        break;
                                    }
                                }

                                LoadImage load = new LoadImage(imageView, false);
                                load.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + selectedMap.getId());
                                parentActivity.dataContainer.targetedMap = selectedMap;
                            }
                        });

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "Error receiving data : " + ex.getMessage());
                }
                break;
            default:
                break;
        }
    }

    public void getMapsData() {
        VolleyServices.getInstance(getContext()).httpRequest(
                Request.Method.GET,
                GET_MAPS_BY_TENANT_ID_ADDRESS + mapId,
                getContext(),
                AddMarkerDataFragment.this,
                GET_MAPS_BY_TENANT_ID,
                null);
    }
    //</editor-fold>

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
