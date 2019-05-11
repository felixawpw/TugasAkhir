package com.felixawpw.indoormaps;


import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.felixawpw.indoormaps.fragment.AccountFragment;
import com.felixawpw.indoormaps.fragment.HomeFragment;
import com.felixawpw.indoormaps.fragment.PlacesFragment;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.services.VolleyServices;
import com.felixawpw.indoormaps.util.ImageUtil;
import com.felixawpw.indoormaps.view.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HomeActivity extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener,
        PlacesFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener {

    private MyPagerAdapter adapter;
    private Toolbar toolbar;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tabs universal");
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_tab_universal_tabs);
        pager = (ViewPager) findViewById(R.id.activity_tab_universal_pager);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
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
            Toast.makeText(HomeActivity.this,
                "Tab reselected: " + position, Toast.LENGTH_SHORT)
                .show();
            }
        });

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

        private final ArrayList<String> tabNames = new ArrayList<String>() {
            {
                add("Home");
                add("Places");
                add("Account");
                add("Help");
            }
        };

        public MyPagerAdapter(FragmentManager fm) {
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
                return HomeFragment.newInstance(position);
            } else if (position == 1){
                return PlacesFragment.newInstance(position);
            } else if (position == 2) {
                return AccountFragment.newInstance(position);
            } else
                return AccountFragment.newInstance(position);
        }
    }

}
