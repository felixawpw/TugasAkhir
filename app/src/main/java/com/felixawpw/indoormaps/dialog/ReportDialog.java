package com.felixawpw.indoormaps.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.AddNewPlacesActivity;
import com.felixawpw.indoormaps.AddedPlaceDetailsActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.SensorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportDialog {
    public static final String TAG = ReportDialog.class.getSimpleName();

    Dialog dialog;
    Spinner spinnerType, spinnerMarker;
    Button buttonCancel, buttonSubmit;
    EditText textReportDetail;
    LinearLayout layoutMarkerSelection;
    Activity activity;

    public Activity getActivity() {
        return activity;
    }

    int tenantId;
    ArrayList<Marker> markers;

    public ReportDialog(Activity activity, int tenantId, ArrayList<Marker> markers) {
        this.activity = activity;
        this.tenantId = tenantId;
        this.markers = (ArrayList<Marker>)markers.clone();

        View v = activity.getLayoutInflater().inflate(R.layout.dialog_report, null);
        spinnerType = v.findViewById(R.id.dialog_report_spinner_report_type);
        spinnerMarker = v.findViewById(R.id.dialog_report_spinner_marker_selection);
        buttonCancel = v.findViewById(R.id.dialog_report_button_cancel);
        buttonSubmit = v.findViewById(R.id.dialog_report_button_submit);
        textReportDetail = v.findViewById(R.id.dialog_report_text_detail);
        layoutMarkerSelection = v.findViewById(R.id.dialog_report_layout_marker_selection);


        dialog = new Dialog(activity, R.style.MaterialDialogSheet);
        dialog.setContentView(v);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        spinnerMarker.setAdapter(new ArrayAdapter<Marker>(activity, android.R.layout.simple_spinner_item, markers));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postData();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public static final int POST_REPORT_DATA = 1;
    public static final String POST_REPORT_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/report/add";

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case POST_REPORT_DATA:
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    if (status) {
                        Toast.makeText(activity, message.toString(), Toast.LENGTH_LONG).show();
                        this.dismiss();
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error handling response : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void postData() throws JSONException {
        JSONObject postData = new JSONObject();
        postData.put("report_detail", textReportDetail.getText());
        postData.put("report_type", spinnerType.getSelectedItemPosition());
        postData.put("report_tenant_id", tenantId);
        postData.put("report_marker_id", ((Marker)spinnerMarker.getSelectedItem()).getId());

        Log.d(TAG, "Marker data"+ spinnerMarker.getSelectedItem());
        VolleyServices.getInstance(activity).httpRequest(
                Request.Method.POST,
                POST_REPORT_DATA_ADDRESS,
                activity,
                this,
                POST_REPORT_DATA,
                postData);
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
