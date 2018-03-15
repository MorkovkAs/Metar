package com.antonklimakov.metar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.antonklimakov.metar.fragments.AirportFavoriteListFragment;
import com.antonklimakov.metar.fragments.AirportListFragment;

public class TabbedActivity extends FragmentActivity {

    AirportListFragment airportListFragment;
    AirportFavoriteListFragment favListFragment;
    private Fragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        FragmentManager fragmentManager = getSupportFragmentManager();

		/*
         * This is called when orientation is changed.
		 */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("content")) {
                String content = savedInstanceState.getString("content");
                if (content.equals(AirportFavoriteListFragment.ARG_ITEM_ID)) {
                    if (fragmentManager.findFragmentByTag(AirportFavoriteListFragment.ARG_ITEM_ID) != null) {
                        setFragmentTitle(R.string.app_name);
                        contentFragment = fragmentManager.findFragmentByTag(AirportFavoriteListFragment.ARG_ITEM_ID);
                    }
                }
            }
            if (fragmentManager.findFragmentByTag(AirportListFragment.ARG_ITEM_ID) != null) {
                airportListFragment = (AirportListFragment) fragmentManager.findFragmentByTag(AirportListFragment.ARG_ITEM_ID);
                contentFragment = airportListFragment;
            }
        } else {
            airportListFragment = new AirportListFragment();
            setFragmentTitle(R.string.app_name);
            switchContent(airportListFragment, AirportListFragment.ARG_ITEM_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof AirportFavoriteListFragment) {
            outState.putString("content", AirportFavoriteListFragment.ARG_ITEM_ID);
        } else {
            outState.putString("content", AirportListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_favorites:
                setFragmentTitle(R.string.favorite);
                favListFragment = new AirportFavoriteListFragment();
                switchContent(favListFragment, AirportFavoriteListFragment.ARG_ITEM_ID);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate()) ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            //Only FavoriteListFragment is added to the back stack.
            if (!(fragment instanceof AirportListFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }

    protected void setFragmentTitle(int resourseId) {
        setTitle(resourseId);
        if (getActionBar() != null) {
            getActionBar().setTitle(resourseId);
        }
    }
}