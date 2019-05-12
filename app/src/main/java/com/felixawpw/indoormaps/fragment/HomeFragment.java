package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.services.PlacesServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

import java.security.Permission;
import java.util.List;

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
    Button buttonShowMap;
    Context context;

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
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this );
        }

        context = getContext();
        textPlaceLikelihood = v.findViewById(R.id.fragment_home_text_place_likelihood);
        textPlaceLikelihoodTitle = v.findViewById(R.id.fragment_home_text_likelihood_title);
        buttonShowMap = v.findViewById(R.id.fragment_home_button_show_map);

        textPlaceLikelihoodTitle.setVisibility(View.INVISIBLE);
        textPlaceLikelihood.setVisibility(View.INVISIBLE);
        buttonShowMap.setVisibility(View.INVISIBLE);

        buttonShowMap.setOnClickListener(buttonShowMapListener);

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_home_map_layout, mapFragment).commit();

        return v;
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
//        if (Permissions.PERMISSION_ACCESS_FINE_LOCATION)
//        {
//            PlacesServices.getInstance().getDeviceLocation(googleMap, getActivity());
//            PlacesServices.getInstance().getLikelihood(getActivity(), getString(R.string.geolocation_api_key), HomeFragment.this);
//        }
//        else
//            Permissions.requestPermissions(getActivity());
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
