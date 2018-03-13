package com.antonklimakov.metar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SavedPreference {

    private static final String APP_PREFS_NAME = "PRODUCT_APP";

    public SavedPreference() {
        super();
    }

    public void addSaved(Context context, PrefsName prefsName, String item) {
        item = item.toUpperCase();
        Set<String> items = getItems(context, prefsName);
        if (items == null) {
            items = new HashSet<>();
        }
        items.add(item);
        saveItems(context, prefsName, items);
    }

    public boolean isItemSaved(Context context, PrefsName prefsName, String item) {
        item = item.toUpperCase();
        Set<String> items = getItems(context, prefsName);
        return items != null && items.contains(item);
    }

    public void removeSaved(Context context, PrefsName prefsName, String item) {
        item = item.toUpperCase();
        Set<String> items = getItems(context, prefsName);
        if (items != null) {
            items.remove(item);
            saveItems(context, prefsName, items);
        }
    }

    public void removeAllSaved(Context context, PrefsName prefsName) {
        Set<String> items = getItems(context, prefsName);
        if (items != null) {
            items.clear();
            saveItems(context, prefsName, items);
        }
    }

    private void saveItems(Context context, PrefsName prefsName, Set<String> items) {
        SharedPreferences settings;

        settings = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit()
                .putStringSet(prefsName.name(), items)
                .apply();
    }

    private Set<String> getItems(Context context, PrefsName prefsName) {
        SharedPreferences settings;
        Set<String> items;

        settings = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(prefsName.name())) {
            items = settings.getStringSet(prefsName.name(), null);
        } else {
            return null;
        }

        return items;
    }
}
