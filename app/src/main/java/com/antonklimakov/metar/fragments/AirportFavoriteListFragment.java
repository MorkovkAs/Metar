package com.antonklimakov.metar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.antonklimakov.metar.Airport;
import com.antonklimakov.metar.R;
import com.antonklimakov.metar.utils.PrefsName;
import com.antonklimakov.metar.utils.SavedPreference;

import java.util.List;

public class AirportFavoriteListFragment extends Fragment {

    public static final String ARG_ITEM_ID = "favorite_list";

    ListView favoriteList;
    SavedPreference sharedPreference;
    List<Airport> favorites;

    Activity activity;
    AirportListAdapter airportListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_airport_list, container, false);
        // Get favorite items from SharedPreferences.
        sharedPreference = new SavedPreference();
        favorites = sharedPreference.getItems(activity, PrefsName.FAVORITE);

        if (favorites == null) {
            showAlert(getResources().getString(R.string.no_favorites_items), getResources().getString(R.string.no_favorites_msg));
        } else {
            if (favorites.size() == 0) {
                showAlert(getResources().getString(R.string.no_favorites_items), getResources().getString(R.string.no_favorites_msg));
            }

            favoriteList = view.findViewById(R.id.list_airport);
            if (favorites != null) {
                airportListAdapter = new AirportListAdapter(activity, favorites);
                favoriteList.setAdapter(airportListAdapter);

                favoriteList.setOnItemClickListener(new OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                    }
                });

                favoriteList.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ImageView button = view.findViewById(R.id.imgbtn_favorite);

                        String tag = button.getTag().toString();
                        if (tag.equalsIgnoreCase("grey")) {
                            sharedPreference.addSaved(activity, PrefsName.FAVORITE, favorites.get(position));
                            Toast.makeText(activity, activity.getResources().getString(R.string.add_bookmark), Toast.LENGTH_SHORT).show();

                            button.setTag("red");
                            button.setImageResource(R.drawable.ic_bookmark_white_24dp);
                        } else {
                            sharedPreference.removeSaved(activity, PrefsName.FAVORITE, favorites.get(position));
                            button.setTag("grey");
                            button.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                            airportListAdapter.remove(favorites
                                    .get(position));
                            Toast.makeText(activity, activity.getResources().getString(R.string.remove_bookmark), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        }
        return view;
    }

    public void showAlert(String title, String message) {
        if (activity != null && !activity.isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);

            // setting OK Button
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // activity.finish();
                    getFragmentManager().popBackStackImmediate();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.favorite);
        getActivity().getActionBar().setTitle(R.string.app_name);
        super.onResume();
    }
}