package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.felixawpw.indoormaps.AddMarkerWizardActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.font.RobotoTextView;
import com.felixawpw.indoormaps.view.FloatLabeledEditText;
import com.felixawpw.indoormaps.view.PinView;

public class AddMarkerFinishingFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public int mapId;
    AddMarkerWizardActivity parentActivity;
    public FloatLabeledEditText textName, textDescription, textType;
    public PinView imageMapOrigin, imageMapTarget;
    public LinearLayout layoutTargetedMap;
    public RobotoTextView textSecondImageTitle;
    public AddMarkerFinishingFragment() {
        // Required empty public constructor
    }
    public static AddMarkerFinishingFragment newInstance(Integer param1, AddMarkerWizardActivity activity) {
        AddMarkerFinishingFragment fragment = new AddMarkerFinishingFragment();
        Bundle args = new Bundle();
        fragment.mapId = param1;
        fragment.parentActivity = activity;
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
        View v = inflater.inflate(R.layout.fragment_add_marker_finishing, container, false);
        textName = v.findViewById(R.id.fragment_add_marker_finishing_textName);
        textDescription = v.findViewById(R.id.fragment_add_marker_finishing_textDescription);
        textType = v.findViewById(R.id.fragment_add_marker_finishing_textType);
        imageMapOrigin = v.findViewById(R.id.fragment_add_marker_finishing_imageMapOrigin);
        imageMapTarget = v.findViewById(R.id.fragment_add_marker_finishing_imageMapTarget);
        layoutTargetedMap = v.findViewById(R.id.fragment_add_marker_finishing_layoutTargetedMap);
        textSecondImageTitle = v.findViewById(R.id.fragment_add_marker_finishing_text_second_image_title);
        parentActivity.dataContainer.notifyChanges(this);

        textName.setEnabled(false);
        textDescription.setEnabled(false);
        textType.setEnabled(false);

        return v;
    }

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
