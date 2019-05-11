package com.felixawpw.indoormaps.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.adapter.MarkerListAdapter;
import com.felixawpw.indoormaps.model.MarkerModel;
import com.felixawpw.indoormaps.util.DummyContent;
import com.google.android.gms.maps.SupportMapFragment;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.ArrayList;

public class MapListFragment extends Fragment implements OnDismissCallback {
    private MapListFragment.OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static final String TAG = MapListFragment.class.getSimpleName();
    private SupportMapFragment mapFragment;


    //Google Cards Travel
    private static final int INITIAL_DELAY_MILLIS = 300;

    private MarkerListAdapter mMarkerListAdapter;
    Context context;
    private EditText searchField;

    public MapListFragment() {

    }

    public static MapListFragment newInstance(Integer param1) {
        MapListFragment fragment = new MapListFragment();
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
        context = getContext();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_list, container, false);
        searchField = (EditText) v.findViewById(R.id.fragment_map_list_search_field);
        //Implement Search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMarkerListAdapter != null) {
                    mMarkerListAdapter.getFilter().filter(s);
                } else {
                    Log.d(TAG, "no filter availible");
                }
            }
        });

        ListView listView = (ListView) v.findViewById(R.id.fragment_map_list_list_view);

        //Get Google Cards Content
        mMarkerListAdapter = new MarkerListAdapter(context, new ArrayList<MarkerModel>());

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                new SwipeDismissAdapter(mMarkerListAdapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);

        listView.setClipToPadding(false);
        listView.setDivider(null);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, r.getDisplayMetrics());
        listView.setDividerHeight(px);
        listView.setFadingEdgeLength(0);
        listView.setFitsSystemWindows(true);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                r.getDisplayMetrics());
        listView.setPadding(px, px, px, px);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setAdapter(swingBottomInAnimationAdapter);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(@NonNull final ViewGroup listView,
                          @NonNull final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mMarkerListAdapter.remove(mMarkerListAdapter.getItem(position));
        }
    }


}
