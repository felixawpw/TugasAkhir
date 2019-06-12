package com.felixawpw.indoormaps;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.adapter.ReportAdapter;
import com.felixawpw.indoormaps.barcode.BarcodeCaptureActivity;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.mirror.Report;
import com.felixawpw.indoormaps.model.ReportModel;
import com.felixawpw.indoormaps.services.AuthServices;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.PinView;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CalibrateScanPointActivity extends AppCompatActivity {
    public static final int BARCODE_READER_REQUEST_CODE = 1;
    public static final String TAG = CalibrateScanPointActivity.class.getSimpleName();

    float orientation = -1f;
    int markerId;
    Button buttonScanQr, buttonCalibrate;
    PinView imageFloorPlan;
    TextView textScanPointName;
    Marker marker;
    PointF pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_scan_point);
        buttonScanQr = findViewById(R.id.activity_calibrate_scan_point_button_scan_qr);
        buttonCalibrate = findViewById(R.id.activity_calibrate_scan_point_button_calibrate);
        imageFloorPlan = findViewById(R.id.activity_calibrate_scan_point_map_view);
        textScanPointName = findViewById(R.id.activity_calibrate_scan_point_text_scan_point_name);

        showBarcodeScanner();

        buttonScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBarcodeScanner();
            }
        });

        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validateInput())
                        postCalibrationData();
                    else
                        Toast.makeText(CalibrateScanPointActivity.this,
                                "Make sure you have scanned the scan point QR code and select a point on your floor plan view.",
                                Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        imageFloorPlanGestureListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Calibrate Floor Plan");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void imageFloorPlanGestureListener() {
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageFloorPlan.isReady()) {
                    PointF sCoord = imageFloorPlan.viewToSourceCoord(e.getX(), e.getY());
                    pin = sCoord;
                    imageFloorPlan.setPin(sCoord);
                }
                return true;
            }
        });
        imageFloorPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }


    public void showBarcodeScanner() {
        Intent intent = new Intent(CalibrateScanPointActivity.this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String barcodeVal = barcode.displayValue;
                    if (!barcodeVal.equals("")) {
                        String[] token = barcodeVal.split("_");
                        if (token.length == 3) {
                             markerId = Integer.parseInt(token[2]);
                        }
                    }
                    //Get marker data from server
                    try {
                        getMarkerData(markerId);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                        ex.printStackTrace();
                    }
                    //LoadImage
                    //Set pin based on marker.
                    orientation = data.getFloatExtra("orientation", -1f);
                    Toast.makeText(this, "Orientation = " + orientation, Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public static final int GET_MARKER_DATA_BY_ID = 1;
    public static final int POST_CALIBRATION_DATA = 2;
    public static final String GET_MARKER_DATA_BY_ID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/by_id/";
    public static final String POST_CALIBRATION_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/calibrate_scan_point/";
    public static final String LOAD_MAP_AND_SHOW_MARKER = VolleyServices.ADDRESS_DEFAULT + "external/map/processed_map/by_marker/download/";

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case GET_MARKER_DATA_BY_ID:
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    marker = new Marker(response.getJSONObject("markerData"));
                    textScanPointName.setText(marker.getName());
                    Map map = new Map();
                    map.setId(marker.getMapId());
                    LoadImage loadImage = new LoadImage(imageFloorPlan, false);
                    loadImage.execute(LOAD_MAP_AND_SHOW_MARKER + marker.getId());
                    break;
                case POST_CALIBRATION_DATA:
                    status = response.getBoolean("status");
                    message = response.getString("message");
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error handling response : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean validateInput() {
        return marker != null && pin != null && orientation != -1f;
    }

    public void postCalibrationData() throws JSONException {
        JSONObject postData = new JSONObject();
        postData.put("marker_id", marker.getId());
        postData.put("calibrate_x", pin.x);
        postData.put("calibrate_y", pin.y);
        postData.put("orientation", orientation);

        VolleyServices.getInstance(this).httpRequest(
                Request.Method.POST,
                POST_CALIBRATION_DATA_ADDRESS,
                this,
                this,
                POST_CALIBRATION_DATA,
                postData);

    }

    public void getMarkerData(int markerId) throws JSONException {
        VolleyServices.getInstance(this).httpRequest(
                Request.Method.GET,
                GET_MARKER_DATA_BY_ID_ADDRESS + markerId,
                this,
                this,
                GET_MARKER_DATA_BY_ID,
                null);
    }

}
