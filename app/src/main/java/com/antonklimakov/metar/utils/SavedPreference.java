package com.antonklimakov.metar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.antonklimakov.metar.Airport;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedPreference {

    private static final String APP_PREFS_NAME = "PRODUCT_APP";

    public SavedPreference() {
        super();
    }

    public void addSaved(Context context, PrefsName prefsName, Airport item) {
        item = item;
        List<Airport> items = getItems(context, prefsName);
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        saveItems(context, prefsName, items);
    }

    public boolean isItemSaved(Context context, PrefsName prefsName, Airport item) {
        List<Airport> items = getItems(context, prefsName);
        return items != null && items.contains(item);
    }

    public boolean isItemSaved(Context context, PrefsName prefsName, String airportIcao) {
        List<Airport> items = getItems(context, prefsName);
        if (items != null) {
            for (Airport airport : items) {
                if (airport.getIcao().equalsIgnoreCase(airportIcao)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void removeSaved(Context context, PrefsName prefsName, Airport item) {
        List<Airport> items = getItems(context, prefsName);
        if (items != null) {
            items.remove(item);
            saveItems(context, prefsName, items);
        }
    }

    public void removeSaved(Context context, PrefsName prefsName, String airportIcao) {
        List<Airport> items = getItems(context, prefsName);
        if (items != null) {
            for (Airport airport : items) {
                if (airport.getIcao().equalsIgnoreCase(airportIcao)) {
                    items.remove(airport);
                    break;
                }
            }
            saveItems(context, prefsName, items);
        }
    }

    public void removeAllSaved(Context context, PrefsName prefsName) {
        List<Airport> items = getItems(context, prefsName);
        if (items != null) {
            items.clear();
            saveItems(context, prefsName, items);
        }
    }

    public void saveItems(Context context, PrefsName prefsName, List<Airport> items) {
        SharedPreferences preference;
        preference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);

        Editor editor = preference.edit();
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);
        editor.putString(prefsName.name(), jsonItems);
        editor.apply();
    }

    public List<Airport> getItems(Context context, PrefsName prefsName) {
        SharedPreferences preference;
        List<Airport> items = null;

        preference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);

        if (preference.contains(prefsName.name())) {
            String jsonItems = preference.getString(prefsName.name(), null);
            Gson gson = new Gson();
            Airport[] favoriteItems = gson.fromJson(jsonItems, Airport[].class);

            if (favoriteItems != null) {
                items = Arrays.asList(favoriteItems);
                items = new ArrayList<>(items);
            }
        }

        return items;
    }
}
