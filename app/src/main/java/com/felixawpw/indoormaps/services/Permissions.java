package com.felixawpw.indoormaps.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.security.Permission;

public class Permissions {
    public static final String TAG = Permission.class.getSimpleName();

    public static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 2;
    public static final int REQUEST_INTERNET = 3;
    public static final int REQUEST_MAPS_RECEIVE = 4;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 5;

    public static boolean PERMISSION_ACCESS_FINE_LOCATION = false;
    public static boolean PERMISSION_ACCESS_COARSE_LOCATION = false;
    public static boolean PERMISSION_INTERNET = false;
    public static boolean PERMISSION_MAPS_RECEIVE = false;
    public static boolean PERMISSION_READ_EXTERNAL_STORAGE = false;

    public void Permissions() {

    }

    public static void checkPermissionsOnLoad(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            PERMISSION_ACCESS_FINE_LOCATION = true;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            PERMISSION_READ_EXTERNAL_STORAGE = true;
    }

    public static void requestPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public static void requestGalleryPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
}
