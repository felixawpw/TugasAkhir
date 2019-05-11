package com.felixawpw.indoormaps.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.security.Permission;
import java.util.Arrays;
import java.util.List;

public class PlacesServices {
    public static final String TAG = PlacesServices.class.getSimpleName();
    private static final PlacesServices ourInstance = new PlacesServices();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    public static final float DEFAULT_ZOOM = 15.0f;
    private PlacesClient placesClient;
    public static PlacesServices getInstance() {
        return ourInstance;
    }

    private PlacesServices() {

    }

    public void getLikelihood(final Activity activity, String apiKey, final Fragment fragment) {
        Places.initialize(activity, apiKey);

        placesClient = Places.createClient(activity);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(activity, new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful()){
                        FindCurrentPlaceResponse response = task.getResult();
                        if (fragment instanceof HomeFragment )
                            ((HomeFragment)fragment).placeLikelihoodServiceFinished(response.getPlaceLikelihoods());

                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getId(),
                                    placeLikelihood.getLikelihood()));
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
        }

    }

    public void getDeviceLocation(final GoogleMap gMap, Activity baseActivity) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(baseActivity);

        final LatLng sydney = new LatLng(-33.852, 151.211);
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Log.d(TAG, "Permission = " + Permissions.PERMISSION_ACCESS_FINE_LOCATION);
            if (Permissions.PERMISSION_ACCESS_FINE_LOCATION) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(baseActivity, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            gMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .title("You are here."));

                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
                            gMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
