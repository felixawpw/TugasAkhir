package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.adapter.MarkerListAdapter;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.DummyContent;
import com.google.android.gms.maps.SupportMapFragment;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapListOwnerFragment extends Fragment implements OnDismissCallback {
    Map map;
    private MapListOwnerFragment.OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static final String TAG = MapListOwnerFragment.class.getSimpleName();
    private SupportMapFragment mapFragment;


    //Google Cards Travel
    private static final int INITIAL_DELAY_MILLIS = 300;

    private MarkerListAdapter mMarkerListAdapter;
    Context context;
    private EditText searchField;

    public MapListOwnerFragment() {

    }

    public static MapListOwnerFragment newInstance(Map map) {
        MapListOwnerFragment fragment = new MapListOwnerFragment();
        fragment.map = map;
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
        context = getContext();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_list_owner, container, false);
        searchField = (EditText) v.findViewById(R.id.fragment_map_list_owner_search_field);
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

        ListView listView = (ListView) v.findViewById(R.id.fragment_map_list_owner_list_view);

        //Get Google Cards Content
        mMarkerListAdapter = new MarkerListAdapter(context, new ArrayList<MarkerModel>());

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                new SwipeDismissAdapter(mMarkerListAdapter, this));
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

        getAllMarkers();

        return v;
    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int GET_ALL_MARKERS_DATA = 1;
    public static final String GET_ALL_MARKERS_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/by_map_id/";
    List<Marker> tenantsData = new ArrayList<>();
    public void handleResponse(int requestId, JSONObject response) {
        switch (requestId) {
            case GET_ALL_MARKERS_DATA:
                try {
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    JSONArray data = response.getJSONArray("data");
                    if (status) {
                        ArrayList<MarkerModel> list = new ArrayList<>();
                        JSONArray tenants = data;
                        for (int i = 0; i < tenants.length(); i++) {
                            Marker marker = new Marker(tenants.getJSONObject(i));
                            tenantsData.add(marker);
                            list.add(new MarkerModel(i+1,
                                    "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
                                    marker.getName(),
                                    R.string.fontello_heart_empty,
                                    marker));
                        }
                        mMarkerListAdapter.addAll(list);
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "Error handling response : " + ex.getMessage());
                }
                break;
            default:
                break;
        }
    }

    public void getAllMarkers() {
        try {
            VolleyServices.getInstance(getContext()).httpRequest(
                    Request.Method.GET,
                    GET_ALL_MARKERS_DATA_ADDRESS + map.getId(),
                    getContext(),
                    MapListOwnerFragment.this,
                    GET_ALL_MARKERS_DATA,
                    null);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    //</editor-fold>

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(@NonNull final ViewGroup listView,
                          @NonNull final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mMarkerListAdapter.remove(mMarkerListAdapter.getItem(position));
        }
    }


}
