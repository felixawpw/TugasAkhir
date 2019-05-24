package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.felixawpw.indoormaps.AddMarkerWizardActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.mirror.Marker;
import com.felixawpw.indoormaps.services.LoadImage;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.view.FloatLabeledEditText;
import com.felixawpw.indoormaps.view.PinView;

public class AddMarkerSetFragment extends Fragment {

    private int position;
    private LinearLayout layout;
    private int mapId;
    public PinView imageView;
    private OnFragmentInteractionListener mListener;
    private FloatLabeledEditText textName, textDescription;
    public float selectedX, selectedY;
    AddMarkerWizardActivity parentActivity;
    Spinner spinnerMarkerType;

    public AddMarkerSetFragment() {
        // Required empty public constructor
    }

    public static AddMarkerSetFragment newInstance(Integer param1, AddMarkerWizardActivity activity) {
        AddMarkerSetFragment fragment = new AddMarkerSetFragment();
        fragment.mapId = param1;
        fragment.parentActivity = activity;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private long then;
    private int longClickDuration = 2000; //for long click to trigger after 5 seconds

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_marker, container, false);
        layout = (LinearLayout) v
                .findViewById(R.id.fragment_set_marker_layout);

        layout.invalidate();
        imageView = v.findViewById(R.id.fragment_set_marker_imageMap);
        textName = v.findViewById(R.id.fragment_set_marker_nama);
        textDescription = v.findViewById(R.id.fragment_set_marker_deskripsi);
        spinnerMarkerType =v.findViewById(R.id.fragment_set_marker_spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parentActivity,
                R.array.marker_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerType.setAdapter(adapter);

        LoadImage load = new LoadImage(imageView, false);
        load.execute(VolleyServices.LOAD_MAP_IMAGE_BY_ID + mapId);
        addListeners();
        return v;
    }

    public void addListeners() {
        imageView.setOnTouchListener(imageMapOnTouchListener);

        spinnerMarkerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        parentActivity.dataContainer.type = Marker.TYPE_PUBLIC;
                        break;
                    case 1:
                        parentActivity.dataContainer.type = Marker.TYPE_UPSTAIR;
                        break;
                    case 2:
                        parentActivity.dataContainer.type = Marker.TYPE_DOWNSTAIR;
                        break;
                    case 3:
                        parentActivity.dataContainer.type = Marker.TYPE_TOILET;
                        break;
                    case 4:
                        parentActivity.dataContainer.type = Marker.TYPE_SCAN_POINT;
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                parentActivity.dataContainer.name = s.toString();
            }
        });

        textDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                parentActivity.dataContainer.description = s.toString();
            }
        });
    }



    View.OnTouchListener imageMapOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageView.setPin(new PointF(event.getX(), event.getY()));

                parentActivity.dataContainer.pinX = event.getX();
                parentActivity.dataContainer.pinY = event.getY();
            }
            return true;
        }
    };

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
