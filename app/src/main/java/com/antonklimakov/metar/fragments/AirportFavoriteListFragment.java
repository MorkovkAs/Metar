package com.antonklimakov.metar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.antonklimakov.metar.Airport;
import com.antonklimakov.metar.R;
import com.antonklimakov.metar.utils.PrefsName;
import com.antonklimakov.metar.utils.SavedPreference;

import java.util.List;

public class AirportFavoriteListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String ARG_ITEM_ID = "favorite_list";

    ListView favoriteList;
    SavedPreference sharedPreference;
    List<Airport> airports;

    Activity activity;
    AirportListAdapter airportListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        sharedPreference = new SavedPreference();
        airports = sharedPreference.getItems(activity, PrefsName.FAVORITE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_airport_list, container, false);
        favoriteList = view.findViewById(R.id.list_airport);

        airportListAdapter = new AirportListAdapter(activity, airports);
        favoriteList.setAdapter(airportListAdapter);
        favoriteList.setOnItemClickListener(this);
        favoriteList.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Airport airport = (Airport) parent.getItemAtPosition(position);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("icao", airport.getIcao());
        activity.setResult(Activity.RESULT_OK, returnIntent);
        activity.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
        ImageView button = view.findViewById(R.id.imgbtn_favorite);

        String tag = button.getTag().toString();
        if (tag != null && tag.equalsIgnoreCase("red")) {
            sharedPreference.removeSaved(activity, PrefsName.FAVORITE, airports.get(position));
            button.setTag("grey");
            button.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            airportListAdapter.remove(airports.get(position));
            Toast.makeText(activity, activity.getResources().getString(R.string.remove_bookmark), Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}