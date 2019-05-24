package com.felixawpw.indoormaps.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.adapter.TenantAdapter;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.services.PlacesServices;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private OnFragmentInteractionListener mListener;
    public static final String TAG = HomeFragment.class.getSimpleName();

    private SupportMapFragment mapFragment;
    GoogleMap googleMap;

    String likelyPlaceId = null;
    String likelyPlaceName = "";
    RobotoTextView textPlaceLikelihood, textPlaceLikelihoodTitle;
    DynamicListView listTenants;
    Button buttonShowMap;
    Context context;
    LinearLayout mainLayout;
    EditText searchField;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(Integer param1) {
        HomeFragment fragment = new HomeFragment();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this );
        }

        context = getContext();
        textPlaceLikelihood = v.findViewById(R.id.fragment_home_text_place_likelihood);
        textPlaceLikelihoodTitle = v.findViewById(R.id.fragment_home_text_likelihood_title);
        buttonShowMap = v.findViewById(R.id.fragment_home_button_show_map);
        listTenants = v.findViewById(R.id.fragment_home_list_tenants);
        mainLayout = v.findViewById(R.id.main_layout);
        mainLayout.requestFocus();

        searchField = v.findViewById(R.id.fragment_home_search_field);
        searchField.setFocusable(false);

        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSearchDialog();
            }
        });

        setEmptyListView(listTenants);

        textPlaceLikelihoodTitle.setVisibility(View.INVISIBLE);
        textPlaceLikelihood.setVisibility(View.INVISIBLE);
        buttonShowMap.setVisibility(View.INVISIBLE);

        buttonShowMap.setOnClickListener(buttonShowMapListener);

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_home_map_layout, mapFragment).commit();

        return v;
    }

    public void handleResponse(int requestId, JSONObject response) {
        switch (requestId) {
            case PlacesListData.GET_PLACES_DATA_BY_SEARCH:
                placeData.handleResponse(requestId, response);
                break;
            default:
                break;
        }

    }
    public PlacesListData placeData;
    public void createSearchDialog() {
        View v = getLayoutInflater().inflate(R.layout.fragment_places, null);
        placeData = new PlacesListData(v, context);

        final Dialog fbDialogue = new Dialog(context, R.style.MaterialDialogSheet);
        fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        fbDialogue.setContentView(v);
        fbDialogue.setCancelable(true);
        fbDialogue.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                1500);
        fbDialogue.getWindow().setGravity(Gravity.BOTTOM);
        fbDialogue.show();
    }

    public class PlacesListData {
        private TenantAdapter mTenantListAdapter;
        Context context;
        private EditText searchField;
        DynamicListView listView;

        public PlacesListData(View v, Context context) {
            // Inflate the layout for this fragment
            searchField = (EditText) v.findViewById(R.id.fragment_places_search_field);
            //Implement Search
            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                private Timer timer=new Timer();
                private final long DELAY = 1000; // milliseconds

                @Override
                public void afterTextChanged(final Editable s) {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject data = new JSONObject();
                                        data.put("nama", s.toString());
                                        Log.i(TAG, "Response " + data.toString());
                                        VolleyServices.getInstance(getContext()).httpRequest(
                                                Request.Method.POST,
                                                GET_PLACES_DATA_BY_SEARCH_ADDRESS,
                                                getContext(),
                                                HomeFragment.this,
                                                GET_PLACES_DATA_BY_SEARCH,
                                                data);
                                    } catch (Exception ex) {
                                        Log.e(TAG, ex.getMessage());
                                    }
                                }
                            },
                            DELAY
                    );

                    if (mTenantListAdapter != null) {
                        mTenantListAdapter.getFilter().filter(s);
                    } else {
                        Log.d(TAG, "no filter availible");
                    }
                }
            });

            listView = v.findViewById(R.id.fragment_places_list_view);
            setEmptyListView(listView);
            mTenantListAdapter = new TenantAdapter(context, new ArrayList<TenantModel>(), HomeFragment.this);
        }

        //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
        public static final int GET_PLACES_DATA_BY_SEARCH = 1;
        public static final String GET_PLACES_DATA_BY_SEARCH_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/place/search/";
        List<Tenant> tenantsData = new ArrayList<>();
        public void handleResponse(int requestId, JSONObject response) {
            Log.d(TAG, "Places list data : " + response);
            switch (requestId) {
                case GET_PLACES_DATA_BY_SEARCH:
                    try {
                        if (response.getBoolean("message")) {
                            mTenantListAdapter.clear();
                            Log.i(TAG, "Response = " + response.toString());
                            ArrayList<TenantModel> list = new ArrayList<>();
                            JSONArray tenants = response.getJSONArray("data");
                            for (int i = 0; i < tenants.length(); i++) {
                                Tenant tenant = new Tenant(tenants.getJSONObject(i));
                                tenantsData.add(tenant);
                                list.add(new TenantModel(i+1,
                                        "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
                                        tenant.getNama(),
                                        R.string.fontello_heart_empty,
                                        tenant));
                            }
                            mTenantListAdapter.addAll(list);
                            appearanceAnimate(0);
                        }
                    } catch (JSONException ex) {
                        Log.e(TAG, "Error handling response : " + ex.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
        //</editor-fold>

        private void appearanceAnimate(int key) {
            AnimationAdapter animAdapter;
            switch (key) {
                default:
                case 0:
                    animAdapter = new AlphaInAnimationAdapter(mTenantListAdapter);
                    break;
                case 1:
                    animAdapter = new ScaleInAnimationAdapter(mTenantListAdapter);
                    break;
                case 2:
                    animAdapter = new SwingBottomInAnimationAdapter(mTenantListAdapter);
                    break;
                case 3:
                    animAdapter = new SwingLeftInAnimationAdapter(mTenantListAdapter);
                    break;
                case 4:
                    animAdapter = new SwingRightInAnimationAdapter(mTenantListAdapter);
                    break;
            }
            animAdapter.setAbsListView(listView);
            listView.setAdapter(animAdapter);
        }
    }

    public void setEmptyListView(DynamicListView listView) {
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("Type something to search for places");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    //Listeners
    View.OnClickListener buttonShowMapListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("placeId", likelyPlaceId);
            intent.putExtra("placeName", likelyPlaceName);
            context.startActivity(intent);
        }
    };
    //End Listeners

    //Call this function after getLikelihood Task finished running
    public void placeLikelihoodServiceFinished(List<PlaceLikelihood> placeLikelihoods) {
        Place mostLikelyPlace = placeLikelihoods.get(0).getPlace();
        likelyPlaceId = mostLikelyPlace.getId();
        likelyPlaceName = mostLikelyPlace.getName();

        textPlaceLikelihood.setText(likelyPlaceName);

        textPlaceLikelihoodTitle.setVisibility(View.VISIBLE);
        textPlaceLikelihood.setVisibility(View.VISIBLE);
        buttonShowMap.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d(TAG, "Permission Access Fine Location = " + Permissions.PERMISSION_ACCESS_FINE_LOCATION);
        Log.d(TAG, "Permission Read External Stroage = " + Permissions.PERMISSION_READ_EXTERNAL_STORAGE);
            //Uncomment
        if (Permissions.PERMISSION_ACCESS_FINE_LOCATION)
        {
            PlacesServices.getInstance().getDeviceLocation(googleMap, getActivity());
            PlacesServices.getInstance().getLikelihood(getActivity(), getString(R.string.geolocation_api_key), HomeFragment.this);
        }
        else
            Permissions.requestPermissions(getActivity());
    }

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
