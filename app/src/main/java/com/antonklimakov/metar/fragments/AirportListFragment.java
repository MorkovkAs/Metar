package com.antonklimakov.metar.fragments;

import android.app.Activity;
import android.app.Fragment;
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

public class AirportListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String ARG_ITEM_ID = "airport_list";

    Activity activity;
    ListView airportListView;
    List<Airport> airports;
    AirportListAdapter airportListAdapter;

    SavedPreference sharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreference = new SavedPreference();
        airports = sharedPreference.getItems(activity, PrefsName.HISTORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_airport_list, container, false);
        findViewsById(view);

        airportListAdapter = new AirportListAdapter(activity, airports);
        airportListView.setAdapter(airportListAdapter);
        airportListView.setOnItemClickListener(this);
        airportListView.setOnItemLongClickListener(this);
        return view;
    }

    private void findViewsById(View view) {
        airportListView = view.findViewById(R.id.list_airport);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Airport airport = (Airport) parent.getItemAtPosition(position);
        Toast.makeText(activity, airport.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
        ImageView button = view.findViewById(R.id.imgbtn_favorite);

        String tag = button.getTag().toString();
        if (tag.equalsIgnoreCase("grey")) {
            sharedPreference.addSaved(activity, PrefsName.FAVORITE, airports.get(position));
            Toast.makeText(activity, activity.getResources().getString(R.string.add_bookmark), Toast.LENGTH_SHORT).show();
            button.setTag("red");
            button.setImageResource(R.drawable.ic_bookmark_white_24dp);
            airportListAdapter.notifyDataSetChanged();
        } else {
            sharedPreference.removeSaved(activity, PrefsName.FAVORITE, airports.get(position));
            button.setTag("grey");
            button.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            Toast.makeText(activity, activity.getResources().getString(R.string.remove_bookmark), Toast.LENGTH_SHORT).show();
            airportListAdapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.history);
        getActivity().getActionBar().setTitle(R.string.app_name);
        super.onResume();
    }
}
