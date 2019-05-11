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

import com.android.volley.Request;
import com.felixawpw.indoormaps.R;
import com.felixawpw.indoormaps.adapter.GoogleCardsTravelAdapter;
import com.felixawpw.indoormaps.mirror.Tenant;
import com.felixawpw.indoormaps.model.TenantModel;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlacesFragment extends Fragment implements OnDismissCallback {
    private OnFragmentInteractionListener mListener;
    public static final String TAG = PlacesFragment.class.getSimpleName();
    private static final int INITIAL_DELAY_MILLIS = 300;

    private GoogleCardsTravelAdapter mGoogleCardsAdapter;
    Context context;
    private EditText searchField;

    public PlacesFragment() {

    }

    public static PlacesFragment newInstance(Integer param1) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //<editor-fold desc="VOLLEY SERVICES" defaultstate="collapsed">
    public static final int GET_PLACES_DATA_BY_SEARCH = 1;
    public static final String GET_PLACES_DATA_BY_SEARCH_ADDRESS = VolleyServices.ADDRESS_DEFAULT + "external/place/search/";
    List<Tenant> tenantsData = new ArrayList<>();
    public void handleResponse(int requestId, JSONObject response) {
        switch (requestId) {
            case GET_PLACES_DATA_BY_SEARCH:
                try {
                    if (response.getBoolean("message")) {
                        Log.i(TAG, "Response = " + response.toString());
                        ArrayList<TenantModel> list = new ArrayList<>();
                        JSONArray tenants = response.getJSONArray("data");
                        for (int i = 0; i < tenants.length(); i++) {
                            Tenant tenant = new Tenant(tenants.getJSONObject(i));
                            tenantsData.add(tenant);
                            list.add(new TenantModel(i+1,
                                    "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg",
                                    tenant.getNama(),
                                    R.string.fontello_heart_empty,
                                    tenant));
                        }
                        list.add(new TenantModel(0, "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg", "Add new place", R.string.fontello_heart_empty));
                        mGoogleCardsAdapter.addAll(list);
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "Error handling response : " + ex.getMessage());
                }
                break;
            default:
                break;
        }
    }
    //</editor-fold>

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        searchField = (EditText) v.findViewById(R.id.fragment_places_search_field);
        //Implement Search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            private Timer timer=new Timer();
            private final long DELAY = 1000; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject data = new JSONObject();
                                    data.put("nama", s.toString());
                                    Log.i(TAG, "Response " + data.toString());
                                    VolleyServices.getInstance(getContext()).httpRequest(
                                            Request.Method.POST,
                                            GET_PLACES_DATA_BY_SEARCH_ADDRESS,
                                            getContext(),
                                            PlacesFragment.this,
                                            GET_PLACES_DATA_BY_SEARCH,
                                            data);
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            }
                        },
                        DELAY
                );

                if (mGoogleCardsAdapter != null) {
                    mGoogleCardsAdapter.getFilter().filter(s);
                } else {
                    Log.d(TAG, "no filter availible");
                }
            }
        });



        ListView listView = (ListView) v.findViewById(R.id.fragment_places_list_view);
        mGoogleCardsAdapter = new GoogleCardsTravelAdapter(context, new ArrayList<TenantModel>());
        //Add new place card

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                new SwipeDismissAdapter(mGoogleCardsAdapter, this));
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

        addPlacesCards();

        return v;
    }

    public void addPlacesCards() {
        mGoogleCardsAdapter.clear();
        ArrayList<TenantModel> list = new ArrayList<>();
        //Default card
        list.add(new TenantModel(0, "http://chittagongit.com//images/new-location-icon/new-location-icon-4.jpg", "Add new place", R.string.fontello_heart_empty));
        mGoogleCardsAdapter.addAll(list);
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
            mGoogleCardsAdapter.remove(mGoogleCardsAdapter.getItem(position));
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
