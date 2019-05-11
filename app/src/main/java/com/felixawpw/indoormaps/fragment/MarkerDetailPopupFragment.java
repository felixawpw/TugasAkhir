package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.font.RobotoTextView;

public class MarkerDetailPopupFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    RobotoTextView textName, textDescription, textType;
    Button buttonDirection;

    public MarkerDetailPopupFragment() {
        // Required empty public constructor
    }

    public static MarkerDetailPopupFragment newInstance(String param1, String param2) {
        MarkerDetailPopupFragment fragment = new MarkerDetailPopupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_marker_detail_popup, container, false);
        textName = v.findViewById(R.id.fragment_marker_detail_popup_text_name);
        textDescription = v.findViewById(R.id.fragment_marker_detail_popup_text_description);
        textType = v.findViewById(R.id.fragment_marker_detail_popup_text_type);
        buttonDirection = v.findViewById(R.id.fragment_marker_detail_popup_button_direction);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
