package com.antonklimakov.metar.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.antonklimakov.metar.Airport;
import com.antonklimakov.metar.R;
import com.antonklimakov.metar.utils.PrefsName;
import com.antonklimakov.metar.utils.SavedPreference;

import java.util.ArrayList;
import java.util.List;

public class AirportListAdapter extends ArrayAdapter<Airport> {

    List<Airport> airports;
    SavedPreference preference;
    private Context context;

    public AirportListAdapter(Context context, List<Airport> airports) {
        super(context, R.layout.airport_list_item, airports);
        this.context = context;
        this.airports = airports != null ? airports : new ArrayList<Airport>();
        preference = new SavedPreference();
    }

    @Override
    public int getCount() {
        return airports.size();
    }

    @Override
    public Airport getItem(int position) {
        return airports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.airport_list_item, null);
            holder = new ViewHolder();
            holder.airportIcaoTxt = convertView.findViewById(R.id.txt_airport_icao);
            holder.favoriteImg = convertView.findViewById(R.id.imgbtn_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Airport airport = getItem(position);
        holder.airportIcaoTxt.setText(airport.getIcao());

        /*If a airport exists in shared preferences then set heart_red drawable and set a tag*/
        if (checkFavoriteItem(airport)) {
            holder.favoriteImg.setImageResource(R.drawable.ic_bookmark_white_24dp);
            holder.favoriteImg.setTag("red");
        } else {
            holder.favoriteImg.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            holder.favoriteImg.setTag("grey");
        }

        return convertView;
    }

    /*Checks whether a particular airport exists in SharedPreferences*/
    public boolean checkFavoriteItem(Airport checkAirport) {
        boolean check = false;
        List<Airport> favorites = preference.getItems(context, PrefsName.FAVORITE);
        if (favorites != null) {
            for (Airport airport : favorites) {
                if (airport.equals(checkAirport)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void add(Airport airport) {
        super.add(airport);
        airports.add(airport);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Airport airport) {
        super.remove(airport);
        airports.remove(airport);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView airportIcaoTxt;
        ImageView favoriteImg;
    }
}
