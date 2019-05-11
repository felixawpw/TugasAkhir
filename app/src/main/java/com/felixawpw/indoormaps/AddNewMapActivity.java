package com.felixawpw.indoormaps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.services.DataPart;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.services.VolleyMultipartRequest;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.FloatLabeledEditText;
import com.felixawpw.indoormaps.view.siv.ShapeImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.HashMap;

public class AddNewMapActivity extends AppCompatActivity {

    private int tenantId;
    private FloatLabeledEditText textNama, textDeskripsi;
    EditText textHeight, textLengthScale, textWidthScale;
    private ShapeImageView imageMap;
    private Button buttonAddMap;

    public static final String TAG = AddNewMapActivity.class.getSimpleName();
    public static final int RESULT_GALLERY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_map);

        Intent intent = getIntent();
        tenantId = intent.getIntExtra("tenantId", -1);

        textNama = (FloatLabeledEditText)findViewById(R.id.activity_add_new_map_nama);
        textDeskripsi = (FloatLabeledEditText)findViewById(R.id.activity_add_new_map_deskripsi);
        textHeight = (EditText)findViewById(R.id.activity_add_new_map_textHeight);
        textLengthScale = (EditText)findViewById(R.id.activity_add_new_map_textLengthScale);
        textWidthScale = (EditText)findViewById(R.id.activity_add_new_map_textWidthScale);
        buttonAddMap = (Button) findViewById(R.id.activity_add_new_map_buttonAddMap);
        imageMap = (ShapeImageView)findViewById(R.id.activity_add_new_map_imageMap);

        Log.i(TAG, "Button " + buttonAddMap);
        imageMap.setOnClickListener(galleryClickListener);
        buttonAddMap.setOnClickListener(buttonAddPlaceClickListener);
        activity = this;
    }

    Activity activity;
    View.OnClickListener galleryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");

            startActivityForResult(galleryIntent , RESULT_GALLERY );
        }
    };
    Bitmap selectedBitmap = null;
    View.OnClickListener buttonAddPlaceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedBitmap != null)
                uploadImage(selectedBitmap);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddNewMapActivity.RESULT_GALLERY :
                Log.i(TAG, "Data = " + data + " Response Code = " + resultCode);
                if (null != data) {
                    Uri selectedImageUri = data.getData();

                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        imageMap.setImageBitmap(thumbnail);
                        selectedBitmap = thumbnail;
                        Log.d(TAG, "Uri = " + selectedImageUri + " Path = " + selectedImageUri.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void uploadImage(final Bitmap image) {
        String url = VolleyServices.ADDRESS_DEFAULT + "external/map/store/";
        // loading or check internet connection or something...
        // ... then
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Boolean status = result.getBoolean("status");
                    String message = result.getString("message");
                    Log.i(TAG, "Status = " + status);

                    if (status) {
                        Log.i(TAG, "Success : " + message);
                        finish();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("success");

                        Log.e("Error Status", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("nama", textNama.getTextString());
                params.put("deskripsi", textDeskripsi.getTextString());
                params.put("height", textHeight.getText().toString());
                params.put("scale_length", textLengthScale.getText().toString());
                params.put("scale_width", textWidthScale.getText().toString());
                params.put("tenant_id", tenantId + "");
                return params;
            }

            @Override
            protected java.util.Map<String, DataPart> getByteData() {
                java.util.Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("denah", new DataPart("file_avatar.jpg", VolleyServices.getFileDataFromBitmap(getApplicationContext(), image), "image/jpeg"));

                return params;
            }
        };

        VolleyServices.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }
}
