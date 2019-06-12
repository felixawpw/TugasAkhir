package com.felixawpw.indoormaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.felixawpw.indoormaps.adapter.ImageGallerySubcategoryAdapter;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.MapsModel;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.DummyContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddedPlaceDetailsActivity extends AppCompatActivity {
    public static final String TAG = AddedPlaceDetailsActivity.class.getSimpleName();
    Tenant tenant;
    private ListView mListView;
    private boolean mIsLayoutOnTop;

    public static final int REQUEST_ADD_NEW_MAP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_place_details);

        Intent intent = getIntent();
        int tenantId = intent.getIntExtra("tenant_id", -1);
        String tenantName = intent.getStringExtra("tenant_name");
        String tenantGoogleMapsId = intent.getStringExtra("tenant_google_maps_id");

        tenant = new Tenant(tenantId, tenantName, tenantGoogleMapsId);
        mListView = (ListView) findViewById(R.id.list_view);
        mIsLayoutOnTop = false;

        getMapsData();
        Log.i(TAG, "Tenant data = " + tenant.getId() + " : " + tenant.getNama() + " : " + tenant.getGoogleMapsId());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tenantName);

    }


    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int GET_MAPS_BY_TENANT_ID = 1;
    public static final String GET_MAPS_BY_TENANT_ID_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/map/by_tenant_id/";
    List<Map> mapsData = new ArrayList<>();

    public void handleResponse(int requestId, JSONObject response) {
        switch (requestId) {
            case GET_MAPS_BY_TENANT_ID:
                ArrayList<MapsModel> list = new ArrayList<>();
                try {
                    JSONArray maps = response.getJSONArray("data");
                    for (int i = 0; i < maps.length(); i++) {
                        Map map = new Map(maps.getJSONObject(i));

                        MapsModel mapsModel = new MapsModel();
                        mapsModel.setId(i+i);
                        mapsModel.setTitle(map.getNama());
                        mapsModel.setUrl(VolleyServices.LOAD_MAP_IMAGE_BY_ID + map.getId());
                        mapsModel.setMap(map);
                        list.add(mapsModel);
                        mapsData.add(map);
                    }

                    MapsModel mapsModel = new MapsModel();
                    mapsModel.setId(0);
                    mapsModel.setTitle("Add New");
                    mapsModel.setUrl("https://cdn2.iconfinder.com/data/icons/rounded-white-basic-ui-set-3/139/Photo_Add-RoundedWhite-512.png");
                    mapsModel.setMap(null);
                    list.add(mapsModel);
                    mListView.setAdapter(new ImageGallerySubcategoryAdapter(this, list, mIsLayoutOnTop, tenant, this));
                } catch (Exception ex) {
                    Log.e(TAG, "Error receiving data : " + ex.getMessage());
                }
                break;
            default:
                break;
        }
        Log.i(TAG, "Request ID = " + response.toString());
    }

    public void getMapsData() {
        VolleyServices.getInstance(this).httpRequest(
                Request.Method.GET,
                GET_MAPS_BY_TENANT_ID_ADDRESS + tenant.getId(),
                this,
                AddedPlaceDetailsActivity.this,
                GET_MAPS_BY_TENANT_ID,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getMapsData();
    }
}
