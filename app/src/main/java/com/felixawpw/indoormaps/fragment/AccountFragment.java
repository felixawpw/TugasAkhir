package com.felixawpw.indoormaps.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felixawpw.indoormaps.AccountActivity;
import com.felixawpw.indoormaps.AddedPlacesActivity;
import com.felixawpw.indoormaps.CalibrateActivity;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.view.MaterialRippleLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = AccountFragment.class.getSimpleName();
    MaterialRippleLayout linkAccount, linkAddedPlaces, linkHistory, linkCalibrate, linkSignOut;
    private OnFragmentInteractionListener mListener;
    TextView textAccount, textAddedPlaces, textHistory, textCalibrate, textSignOut;

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.fragment_account_text_accounts:
                intent = new Intent(getContext(), AccountActivity.class);
                break;
            case R.id.fragment_account_text_added_places:
                intent = new Intent(getContext(), AddedPlacesActivity.class);
                break;
            case R.id.fragment_account_text_signout:
                //Sign out user
                break;
            default:
                break;
        }
        Log.i(TAG, "Intent = " + intent);
        if (intent != null)
            startActivity(intent);
    }

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(Integer param1) {
        AccountFragment fragment = new AccountFragment();
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
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        linkAccount = (MaterialRippleLayout) v.findViewById(R.id.fragment_account_link_accounts);
        linkAddedPlaces = (MaterialRippleLayout)v.findViewById(R.id.fragment_account_link_added_places);
        linkSignOut = (MaterialRippleLayout)v.findViewById(R.id.fragment_account_link_signout);

        textAccount = (TextView) v.findViewById(R.id.fragment_account_text_accounts);
        textAddedPlaces = (TextView)v.findViewById(R.id.fragment_account_text_added_places);
        textSignOut = (TextView)v.findViewById(R.id.fragment_account_text_signout);

        linkAccount.setOnClickListener(this);
        linkAddedPlaces.setOnClickListener(this);
//        textAccount.setOnClickListener(this);
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
        void onFragmentInteraction(Uri uri);
    }
}
