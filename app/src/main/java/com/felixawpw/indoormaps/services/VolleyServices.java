package com.felixawpw.indoormaps.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.felixawpw.indoormaps.AddMarkerWizardActivity;
import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.AddedPlaceDetailsActivity;
import com.felixawpw.indoormaps.AddedPlacesActivity;
import com.felixawpw.indoormaps.CalibrateScanPointActivity;
import com.felixawpw.indoormaps.MapActivity;
import com.felixawpw.indoormaps.OwnedReportActivity;
import com.felixawpw.indoormaps.OwnerMapActivity;
import com.felixawpw.indoormaps.SplashScreenActivity;
import com.felixawpw.indoormaps.adapter.ReportAdapter;
import com.felixawpw.indoormaps.dialog.ReportDialog;
import com.felixawpw.indoormaps.fragment.AddMarkerDataFragment;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.fragment.MapListOwnerFragment;
import com.felixawpw.indoormaps.fragment.MapViewFragment;
import com.felixawpw.indoormaps.fragment.PlacesFragment;
import com.felixawpw.indoormaps.mirror.Report;
import com.google.android.gms.maps.MapView;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import net.kibotu.kalmanrx.KalmanRx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;


public class VolleyServices {
    private static VolleyServices instance;
    private RequestQueue requestQueue;
//    private ImageLoader imageLoader;
    private static Context ctx;
    public static final String ADDRESS_DEFAULT = "http://192.168.1.20/";
    public static final String TAG = VolleyServices.class.getSimpleName();

    public static final String LOAD_MAP_IMAGE_BY_ID = ADDRESS_DEFAULT + "external/map/processed_map/download/";
    public static final String LOAD_MARKER_QR_CODE = ADDRESS_DEFAULT + "external/marker/generate_qr_code/";
    private VolleyServices(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

//        imageLoader = new ImageLoader(requestQueue,
//            new ImageLoader.ImageCache() {
//                private final LruCache<String, Bitmap>
//                        cache = new LruCache<String, Bitmap>(20);
//
//                @Override
//                public Bitmap getBitmap(String url) {
//                    return cache.get(url);
//                }
//
//                @Override
//                public void putBitmap(String url, Bitmap bitmap) {
//                    cache.put(url, bitmap);
//                }
//            });
    }

    public static synchronized VolleyServices getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyServices(context);
        }
        ctx = context;
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
//             getApplicationContext() is key, it keeps you from leaking the
//             Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        getRequestQueue().add(req);
    }

//    public ImageLoader getImageLoader() {
//        return imageLoader;
//    }
    int statusCode = 0;

    public void httpRequest(int requestMethod, String url, Context context, final Object activity, final int requestId, JSONObject parameters) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (requestMethod, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Status code = " + statusCode + " : " + response);
                        if (activity instanceof MapActivity) {
                            ((MapActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof AddNewPlacesActivity) {
                            ((AddNewPlacesActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof HomeFragment) {
                            ((HomeFragment)activity).handleResponse(requestId, response);
                        } else if (activity instanceof AddedPlacesActivity) {
                            ((AddedPlacesActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof AddedPlaceDetailsActivity) {
                            ((AddedPlaceDetailsActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof AddMarkerDataFragment) {
                            ((AddMarkerDataFragment)activity).handleResponse(requestId, response);
                        } else if (activity instanceof AddMarkerWizardActivity) {
                            ((AddMarkerWizardActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof MapListOwnerFragment) {
                            ((MapListOwnerFragment)activity).handleResponse(requestId, response);
                        } else if (activity instanceof OwnerMapActivity) {
                            ((OwnerMapActivity)activity).handleResponse(requestId, response);
                        } else if (activity instanceof ReportDialog)
                            ((ReportDialog)activity).handleResponse(requestId, response);
                        else if (activity instanceof SplashScreenActivity)
                            ((SplashScreenActivity)activity).handleResponse(requestId, response);
                        else if (activity instanceof OwnedReportActivity)
                            ((OwnedReportActivity)activity).handleResponse(requestId, response);
                        else if (activity instanceof ReportAdapter)
                            ((ReportAdapter)activity).handleResponse(requestId,response);
                        else if (activity instanceof CalibrateScanPointActivity)
                            ((CalibrateScanPointActivity)activity).handleResponse(requestId, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (activity instanceof Activity)
                            Toast.makeText((Activity)activity,
                                "There is an error while connecting your device to our server", Toast.LENGTH_SHORT).show();
                        else if (activity instanceof Fragment)
                            Toast.makeText(((Fragment) activity).getActivity(),
                                    "There is an error while connecting your device to our server", Toast.LENGTH_SHORT).show();
                        else if (activity instanceof ReportDialog)
                            Toast.makeText(((ReportDialog) activity).getActivity(),
                                    "There is an error while connecting your device to our server", Toast.LENGTH_SHORT).show();
                        else if (activity instanceof ReportAdapter)
                            Toast.makeText(((ReportAdapter) activity).getActivity(),
                                    "There is an error while connecting your device to our server", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "Error loading " + error.getMessage() + " status code = " + statusCode);

                    }
                }) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

        };

        VolleyServices.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static byte[] getFileDataFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
