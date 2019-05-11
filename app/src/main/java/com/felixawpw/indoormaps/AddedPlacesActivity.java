package com.felixawpw.indoormaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.Request;
import com.felixawpw.indoormaps.adapter.ParallaxTravelAdapter;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.pzv.PullToZoomListViewEx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddedPlacesActivity extends AppCompatActivity {
    public static final String TAG = AddedPlacesActivity.class.getSimpleName();
    ImageView header;

    PullToZoomListViewEx listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_places);
        header = (ImageView) findViewById(R.id.header_parallax_travel_image);

        listView = (PullToZoomListViewEx) findViewById(R.id.activity_added_places_listViewPlaces);
        listView.setShowDividers(0);
//        listView.setAdapter(new ParallaxTravelAdapter(this, getDummyModelListTravel(), false));

        getPlacesData();

        getSupportActionBar().hide();
    }


    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int GET_PLACES_DATA_BY_UID = 1;
    public static final String GET_PLACES_DATA_BY_UID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/tenant/by_user_id/1";
    List<Tenant> tenantsData = new ArrayList<>();

    public void handleResponse(int requestId, JSONObject response) {
        switch (requestId) {
            case GET_PLACES_DATA_BY_UID:
                ArrayList<TenantModel> list = new ArrayList<>();
                try {
                    JSONArray tenants = response.getJSONArray("data");
                    for (int i = 0; i < tenants.length(); i++) {
                        Tenant tenant = new Tenant(tenants.getJSONObject(i));
                        tenantsData.add(tenant);
                        list.add(new TenantModel(i+1,
                                "",
                                tenant.getNama(),
                                R.string.fontello_heart_empty,
                                tenant));
                    }
                    list.add(new TenantModel(0, "", "Add new place", R.string.fontello_heart_empty));
                    listView.setAdapter(new ParallaxTravelAdapter(this, list, false));
                } catch (Exception ex) {
                    Log.e(TAG, "Error receiving data : " + ex.getMessage());
                }
                break;
            default:
                break;
        }
        Log.i(TAG, "Request ID = " + response.toString());
    }

    public void getPlacesData() {
        VolleyServices.getInstance(this).httpRequest(
                Request.Method.GET,
                GET_PLACES_DATA_BY_UID_ADDRESS,
                this,
                AddedPlacesActivity.this,
                GET_PLACES_DATA_BY_UID,
                null);
    }
    //</editor-fold>

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPlacesData();
        Log.i(TAG, "Resuming");
    }
}
