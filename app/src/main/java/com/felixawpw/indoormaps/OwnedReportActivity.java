package com.felixawpw.indoormaps;

import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.adapter.ReportAdapter;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.mirror.Report;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.ReportModel;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.services.AuthServices;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.google.android.gms.auth.api.Auth;
import com.idunnololz.widgets.AnimatedExpandableListView;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OwnedReportActivity extends AppCompatActivity {
    ExpandableListView listView;
    ReportAdapter adapter;
    private List<ReportModel> listHeader;
    private HashMap<ReportModel, String> mapChild;
    public static final String TAG = OwnedReportActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_report);

        listView = findViewById(R.id.activity_owned_report_list_view);
        try {
            getReports();
        } catch (Exception ex) {
            Log.e(TAG, "Error getting report data");
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reports");
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

    public static final int GET_REPORT_DATA = 1;
    public static final String GET_REPORT_DATA_ADDRESS =
            VolleyServices.ADDRESS_DEFAULT + "external/report/" + AuthServices.getInstance().getUser().getId();

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case GET_REPORT_DATA:
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    JSONArray reports = response.getJSONArray("reportData");
                    ArrayList<Report> listReports = new ArrayList<>();
                    listHeader = new ArrayList<>();
                    mapChild = new HashMap<>();

                    for (int i = 0; i < reports.length(); i++) {
                        JSONObject obj = reports.getJSONObject(i);
                        Report r = new Report(obj);
                        ReportModel rm = new ReportModel(r, obj.getString("tenant_name"), obj.getString("marker_name"));
                        listReports.add(r);
                        listHeader.add(rm);
                        mapChild.put(rm, r.getReportDetail());
                    }

                    adapter = new ReportAdapter(this, listHeader, mapChild);
                    listView.setAdapter(adapter);

                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error handling response : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getReports() throws JSONException {
        VolleyServices.getInstance(this).httpRequest(
                Request.Method.GET,
                GET_REPORT_DATA_ADDRESS,
                this,
                this,
                GET_REPORT_DATA,
                null);
    }


//    public void dummy() {
//        listHeader = new ArrayList<>();
//        mapChild = new HashMap<>();
//
//        Tenant t1 = new Tenant(1, "Testing", "123");
//        Tenant t2 = new Tenant(2, "Testing2", "123");
//        Tenant t3 = new Tenant(3, "Testing3", "123");
//        listHeader.add(new TenantModel(1,
//                "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
//                t1.getNama(),
//                R.string.fontello_heart_empty,
//                t1));
//        listHeader.add(new TenantModel(2,
//                "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
//                t2.getNama(),
//                R.string.fontello_heart_empty,
//                t2));
//        listHeader.add(new TenantModel(3,
//                "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
//                t3.getNama(),
//                R.string.fontello_heart_empty,
//                t3));
//
//        mapChild.put(listHeader.get(0), "Child untuk anak 1\n2\n3\n4");
//        mapChild.put(listHeader.get(1), "Child untuk anak 2");
//        mapChild.put(listHeader.get(2), "Child untuk anak 3");
//    }
}
