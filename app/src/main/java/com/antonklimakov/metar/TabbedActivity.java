package com.antonklimakov.metar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.antonklimakov.metar.fragments.AirportFavoriteListFragment;
import com.antonklimakov.metar.fragments.AirportListFragment;

public class TabbedActivity extends Activity implements ActionBar.TabListener {

    AirportListFragment airportListFragment;
    AirportFavoriteListFragment favListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        ActionBar bar = getActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setText("Favorite");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("History");
        tab.setTabListener(this);
        bar.addTab(tab);

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        switch (tab.getText().toString()) {
            case "History":
                airportListFragment = new AirportListFragment();
                switchContent(airportListFragment, ft, AirportListFragment.ARG_ITEM_ID);
                break;
            case "Favorite":
                favListFragment = new AirportFavoriteListFragment();
                switchContent(favListFragment, ft, AirportFavoriteListFragment.ARG_ITEM_ID);
                break;
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void switchContent(Fragment fragment, FragmentTransaction transaction, String tag) {
        transaction.replace(R.id.content_frame, fragment, tag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}