package com.felixawpw.indoormaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.felixawpw.indoormaps.fragment.AddMarkerDataFragment;
import com.felixawpw.indoormaps.fragment.AddMarkerFinishingFragment;
import com.felixawpw.indoormaps.fragment.AddMarkerSetFragment;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;

public class AddMarkerWizardActivity extends AppCompatActivity implements
        AddMarkerDataFragment.OnFragmentInteractionListener,
        AddMarkerSetFragment.OnFragmentInteractionListener,
        AddMarkerFinishingFragment.OnFragmentInteractionListener{

    public class DataContainer {
        public float pinX = -1f, pinY = -1f;
        public float targetedPinX = -1f, targetedPinY = -1f;
        public Map targetedMap;
        public String name, description;
        public int type = -1;
        public DataContainer(){ }

        public String toString() {
            return String.format("Name : %s , Description : %s , Type : %s , Pin X: %s , Pin Y: %s", name, description, type, pinX, pinY);
        }

        public void notifyChanges(AddMarkerFinishingFragment fragment) {
            if (name != null)
                fragment.textName.setText(name);
            if (description != null)
                fragment.textDescription.setText(description);
            if (type != -1)
                fragment.textType.setText(type == Marker.TYPE_PUBLIC ? "Public Places" :
                        type == Marker.TYPE_UPSTAIR ? "Upstairs" :
                        type == Marker.TYPE_DOWNSTAIR ? "Down Stairs" :
                        type == Marker.TYPE_TOILET ? "Toilet" :
                        type == Marker.TYPE_SCAN_POINT ? "Scan Point" : "");

            LoadImage load = new LoadImage(fragment.imageMapOrigin, new PointF(pinX, pinY));
            load.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + fragment.mapId);

            if ((type == 2 || type == 3) && targetedMap != null) {
                fragment.layoutTargetedMap.setVisibility(View.VISIBLE);
                LoadImage load2 = new LoadImage(fragment.imageMapTarget,new PointF(targetedPinX, targetedPinY));
                load2.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + targetedMap.getId());
            }
//            else if (type == Marker.TYPE_SCAN_POINT) {
////                String text=String.format("scan_point_%s_%s_%s_%s", name, description, id, (int)(Math.random() * 1000000)); //Format = scan_point_nama_description_mapid_randomnumber
////                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
////                try {
////                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
////                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
////                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
////                    fragment.imageMapTarget.setImage(ImageSource.bitmap(bitmap));
////                    fragment.textSecondImageTitle.setText("QR Code");
////                } catch (WriterException e) {
////                    e.printStackTrace();
////                }
//            }
            else
                fragment.layoutTargetedMap.setVisibility(View.INVISIBLE);

        }
    }

    public static final String TAG = AddMarkerWizardActivity.class.getSimpleName();
    public DataContainer dataContainer;
    public AddMarkerWizardActivity activity;
    private MyPagerAdapter adapter;
    private ViewPager pager;
    private TextView previousButton;
    private TextView nextButton;
    private TextView navigator;
    private int currentItem;
    private int id;
    AddMarkerSetFragment markerSet;
    AddMarkerDataFragment markerData;
    AddMarkerFinishingFragment markerFinishing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker_wizard);
        Intent intent = getIntent();
        dataContainer = new DataContainer();
        activity =this;
        id = intent.getIntExtra("mapId", 0);
        markerSet = AddMarkerSetFragment.newInstance(id, activity);
        markerData = AddMarkerDataFragment.newInstance(id, activity);
        markerFinishing = AddMarkerFinishingFragment.newInstance(id, activity);

        currentItem = 0;

        pager = (ViewPager) findViewById(R.id.activity_add_marker_wizard_pager);
        previousButton = (TextView) findViewById(R.id.activity_add_marker_wizard_previous);
        nextButton = (TextView) findViewById(R.id.activity_add_marker_wizard_next);
        navigator = (TextView) findViewById(R.id.activity_add_marker_wizard_position);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentItem);

        setNavigator();

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
                setNavigator();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				if (pager.getCurrentItem() != 0) {
                    if (pager.getCurrentItem() == 1)
                        previousButton.setText("Close");

                    if (dataContainer.type == Marker.TYPE_PUBLIC || dataContainer.type == Marker.TYPE_TOILET || dataContainer.type == Marker.TYPE_SCAN_POINT)
                        pager.setCurrentItem(1);
                    else
                        pager.setCurrentItem(pager.getCurrentItem() - 2);

                    setNavigator();
				} else {
				    finish();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println(dataContainer.toString());
                if (pager.getCurrentItem() == 0) {
                    if (dataContainer.type == Marker.TYPE_PUBLIC || dataContainer.type == Marker.TYPE_TOILET || dataContainer.type == Marker.TYPE_SCAN_POINT) {
                        pager.setCurrentItem(pager.getCurrentItem() + 2);
                    } else {
                        pager.setCurrentItem(pager.getCurrentItem() + 1);
                    }
                    previousButton.setText("Previous");
                } else if (pager.getCurrentItem() == 1) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                    dataContainer.notifyChanges(markerFinishing);
                } else if (pager.getCurrentItem() == 2) {
                    try {
                        sendData();
                    } catch (JSONException ex) {
                        Log.e(TAG, "Error sending POST_NEW_MARKER_ADDRESS " + ex.getMessage());
                    }
//                    Toast.makeText(AddMarkerWizardActivity.this, "Finish",
//                            Toast.LENGTH_SHORT).show();
                }
                if (pager.getCurrentItem() == 2)
                    nextButton.setText("Finish");
                setNavigator();
            }
        });

    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int POST_NEW_MARKER_DATA = 1;
    public static final String POST_NEW_MARKER_DATA_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/marker/add";

    public void sendData() throws JSONException{
        JSONObject postData = new JSONObject();
        postData.put("map_id", id);
        postData.put("name", dataContainer.name);
        postData.put("description", dataContainer.description);
        postData.put("point_x", dataContainer.pinX);
        postData.put("point_y", dataContainer.pinY);
        postData.put("marker_type", dataContainer.type);
        postData.put("targeted_point_x", dataContainer.targetedPinX);
        postData.put("targeted_point_y", dataContainer.targetedPinY);
        postData.put("targeted_map_id", dataContainer.targetedMap == null ? "" : dataContainer.targetedMap.getId());

        VolleyServices.getInstance(getApplicationContext()).httpRequest(
                Request.Method.POST,
                POST_NEW_MARKER_DATA_ADDRESS,
                getApplicationContext(),
                AddMarkerWizardActivity.this,
                POST_NEW_MARKER_DATA,
                postData);
    }

    public void handleResponse(int requestId, JSONObject response) {
        try {
            switch (requestId) {
                case POST_NEW_MARKER_DATA:
                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    Toast.makeText(AddMarkerWizardActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (status) {
                        if (dataContainer.type == Marker.TYPE_SCAN_POINT) {
                            String markerId = response.getString("marker_id");
                            Intent intent = new Intent(AddMarkerWizardActivity.this, ScanPointDetailActivity.class);
                            intent.putExtra("markerId", markerId);
                            startActivity(intent);
                        } else
                            finish();
                    }
                    Log.i(TAG, "Response = " + response);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {

        }
    }
    //</editor-fold>

    public void setNavigator() {
        String navigation = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i == pager.getCurrentItem()) {
                navigation += getString(R.string.material_icon_point_full)
                        + "  ";
            } else {
                navigation += getString(R.string.material_icon_point_empty)
                        + "  ";
            }
        }
        navigator.setText(navigation);
    }

    public void setCurrentSlidePosition(int position) {
        this.currentItem = position;
    }

    public int getCurrentSlidePosition() {
        return this.currentItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return markerSet;
            } else if (position == 1) {
                return markerData;
            } else {
                return markerFinishing;
            }
        }
    }
}
