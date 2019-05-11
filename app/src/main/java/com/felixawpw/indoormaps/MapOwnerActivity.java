package com.felixawpw.indoormaps;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;
import android.widget.Toolbar;

import com.felixawpw.indoormaps.fragment.MapListFragment;
import com.felixawpw.indoormaps.fragment.MapListOwnerFragment;
import com.felixawpw.indoormaps.fragment.MapViewFragment;
import com.felixawpw.indoormaps.fragment.MapViewOwnerFragment;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.view.PagerSlidingTabStrip;

import java.util.ArrayList;

public class MapOwnerActivity extends AppCompatActivity {

    public static final String TAG = MapActivity.class.getSimpleName();
    private MapOwnerActivityPagerAdpater adapter;
    private Toolbar toolbar;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static String placeId = null;

    Map map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_owner);

        Intent intent = getIntent();
        map = new Map(
                intent.getIntExtra("mapId", 0),
                intent.getStringExtra("nama"),
                intent.getStringExtra("deskripsi"),
                intent.getStringExtra("processedPath"),
                intent.getStringExtra("originalPath"),
                intent.getFloatExtra("height", 0f),
                intent.getFloatExtra("scaleWidth", 0f),
                intent.getFloatExtra("scaleLength", 0f)
        );

        tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_map_owner_universal_tab);
        pager = (ViewPager) findViewById(R.id.activity_map_owner_universal_pager);

        adapter = new MapOwnerActivityPagerAdpater(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MapOwnerActivity.this,
                        "Tab reselected: " + position, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public class MapOwnerActivityPagerAdpater extends FragmentPagerAdapter {

        private final ArrayList<String> tabNames = new ArrayList<String>() {
            {
                add("Maps");
                add("Lists");
            }
        };

        public MapOwnerActivityPagerAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

        @Override
        public int getCount() {
            return tabNames.size();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MapViewOwnerFragment.newInstance(map);
            } else {
                return MapListOwnerFragment.newInstance(map);
            }
        }
    }
}
