package com.felixawpw.indoormaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.felixawpw.indoormaps.dialog.LoadingDialog;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.services.AuthServices;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.services.PlacesServices;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.FloatLabeledEditText;
import com.felixawpw.indoormaps.view.PagerSlidingTabStrip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import afu.org.checkerframework.checker.oigj.qual.O;

public class AddNewPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    MapView mapView;
    FloatLabeledEditText textNama, textAlamat;
    Button buttonAddPlace;
    public static final String TAG = AddNewPlacesActivity.class.getSimpleName();
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    LoadingDialog loadingDialog;

    public Place selectedPlace;

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int POST_NEW_PLACE_DATA = 1;
    public static final String POST_NEW_PLACE_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/place/add";

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case POST_NEW_PLACE_DATA:
                    Log.i(TAG, "Response = " + response.toString());

                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    int tenantId = response.getInt("tenant_id");
                    String tenantName = response.getString("tenant_name");
                    String googleMapId = response.getString("google_maps_id");
                    loadingDialog.dismiss();
                    Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
                    if (status) {
                        Intent intent = new Intent(this, AddedPlaceDetailsActivity.class);
                        intent.putExtra("tenant_id", tenantId);
                        intent.putExtra("tenant_name", tenantName);
                        intent.putExtra("tenant_google_maps_id", googleMapId);
                        startActivity(intent);
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
    //</editor-fold>

    public boolean validateInput() {
        return textNama.getText().toString() != "" &&
                AuthServices.getInstance().getUser() != null &&
                selectedPlace != null;

    }

    //<editor-fold desc="Listeners" defaultstate="collapsed">
    public View.OnClickListener buttonAddPlaceOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (validateInput()) {
                    loadingDialog = new LoadingDialog(AddNewPlacesActivity.this, "Processing data", "Please wait . . .");
                    loadingDialog.show();

                    Log.i(TAG, "Selected Place ID = " + selectedPlace.getId());
                    JSONObject postData = new JSONObject();
                    postData.put("nama", textNama.getText());
                    postData.put("user_id", AuthServices.getInstance().getUser().getId());
                    postData.put("google_maps_id", selectedPlace.getId());
                    postData.put("google_maps_address", selectedPlace.getAddress());
                    VolleyServices.getInstance(getApplicationContext()).httpRequest(
                            Request.Method.POST,
                            POST_NEW_PLACE_DATA_ADDRESS,
                            getApplicationContext(),
                            AddNewPlacesActivity.this,
                            POST_NEW_PLACE_DATA,
                            postData);
                } else
                    Toast.makeText(AddNewPlacesActivity.this,
                            "Make sure you've filled all data fields correctly", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Log.e(TAG, "Error adding place : " + ex.getMessage());
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="ACTIVITY INITIATE" defaultstate="collapsed">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        //Initiate map view
        mapView = (MapView) findViewById(R.id.activity_add_new_place_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        textNama = (FloatLabeledEditText) findViewById(R.id.activity_add_new_place_textNama);
        textAlamat = (FloatLabeledEditText) findViewById(R.id.activity_add_new_place_textAlamat);
        buttonAddPlace = (Button) findViewById(R.id.activity_add_new_place_buttonAddPlace);
        buttonAddPlace.setOnClickListener(buttonAddPlaceOnClick);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_add_new_place_autoCompleteFragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setCountry("ID");

        textAlamat.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    initiateAutoComplete();
                }
            }
        });

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Place");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initiateAutoComplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                selectedPlace = place;

                textAlamat.setText(place.getName());
                textAlamat.clearFocus();
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName()));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (Permissions.hasPermissions(this))
        {
            PlacesServices.getInstance().getDeviceLocation(googleMap, this);
        }
        else
            Permissions.checkPermissionsOnLoad(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    //</editor-fold>

}
