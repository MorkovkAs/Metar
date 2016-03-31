package com.antonklimakov.metar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class MainActivity extends Activity {
    TextView textViewMetar;
    TextView textViewConditions;
    TextView textViewTemperature;
    TextView textViewDewpoint;
    TextView textViewPressure;
    TextView textViewWinds;
    TextView textViewVisibility;
    TextView textViewCeiling;
    TextView textViewClouds;
    TextView textViewWeather;

    EditText editTextICAO;
    String textICAO = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        textViewMetar = (TextView) findViewById(R.id.textViewMetar);
        textViewConditions = (TextView) findViewById(R.id.textViewConditions);
        textViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
        textViewDewpoint = (TextView) findViewById(R.id.textViewDewpoint);
        textViewPressure = (TextView) findViewById(R.id.textViewPressure);
        textViewWinds = (TextView) findViewById(R.id.textViewWinds);
        textViewVisibility = (TextView) findViewById(R.id.textViewVisibility);
        textViewCeiling = (TextView) findViewById(R.id.textViewCeiling);
        textViewClouds = (TextView) findViewById(R.id.textViewClouds);
        textViewWeather = (TextView) findViewById(R.id.textViewWeather);

        editTextICAO = (EditText) findViewById(R.id.editTextICAO);

        editTextICAO.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //if (s.length() > 0) {
                //    length = s.length();
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    textICAO = s.toString().toUpperCase();
                    writeMetar(textICAO);
                }
            }
        });
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textViewMetar", textViewMetar.getText().toString());
        outState.putString("textViewConditions", textViewConditions.getText().toString());
        outState.putString("textViewTemperature", textViewTemperature.getText().toString());
        outState.putString("textViewDewpoint", textViewDewpoint.getText().toString());
        outState.putString("textViewPressure", textViewPressure.getText().toString());
        outState.putString("textViewWinds", textViewWinds.getText().toString());
        outState.putString("textViewVisibility", textViewVisibility.getText().toString());
        outState.putString("textViewCeiling", textViewCeiling.getText().toString());
        outState.putString("textViewClouds", textViewClouds.getText().toString());
        outState.putString("textViewWeather", textViewWeather.getText().toString());

        outState.putString("editTextICAO", editTextICAO.getText().toString());
        outState.putString("ICAO", textICAO);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textViewMetar.setText(savedInstanceState.getString("textViewMetar"));
        textViewConditions.setText(savedInstanceState.getString("textViewConditions"));
        textViewTemperature.setText(savedInstanceState.getString("textViewTemperature"));
        textViewDewpoint.setText(savedInstanceState.getString("textViewDewpoint"));
        textViewPressure.setText(savedInstanceState.getString("textViewPressure"));
        textViewWinds.setText(savedInstanceState.getString("textViewWinds"));
        textViewVisibility.setText(savedInstanceState.getString("textViewVisibility"));
        textViewCeiling.setText(savedInstanceState.getString("textViewCeiling"));
        textViewClouds.setText(savedInstanceState.getString("textViewClouds"));
        textViewWeather.setText(savedInstanceState.getString("textViewWeather"));

        //editTextICAO.setText(savedInstanceState.getString("editTextICAO"));
        textICAO = savedInstanceState.getString("ICAO");
    }

    private void getMetar(String ICAO) {
        Document doc;
        try {
            String link = "http://aviationweather.gov/adds/metars/?chk_metars=ON&hoursStr=most+recent+only&station_ids=" + ICAO + "&submitmet=Get+info&std_trans=translated";
            doc = Jsoup.connect(link).get();

            final String metar = doc.select("body > table > tbody > tr:nth-child(3) > td:nth-child(2) > strong").text();
            final String conditions = doc.select("body > table > tbody > tr:nth-child(4) > td:nth-child(2)").text();
            final String temperature = doc.select("body > table > tbody > tr:nth-child(5) > td:nth-child(2)").text();
            final String dewpoint = doc.select("body > table > tbody > tr:nth-child(6) > td:nth-child(2)").text();
            final String pressure = doc.select("body > table > tbody > tr:nth-child(7) > td:nth-child(2)").text();
            final String winds = doc.select("body > table > tbody > tr:nth-child(8) > td:nth-child(2)").text();
            final String visibility = doc.select("body > table > tbody > tr:nth-child(9) > td:nth-child(2)").text();
            final String ceiling = doc.select("body > table > tbody > tr:nth-child(10) > td:nth-child(2)").text();
            final String clouds = doc.select("body > table > tbody > tr:nth-child(11) > td:nth-child(2)").text();
            final String weather = doc.select("body > table > tbody > tr:nth-child(12) > td:nth-child(2)").text();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewMetar.setText(metar);
                    textViewConditions.setText(conditions);
                    textViewTemperature.setText(temperature);
                    textViewDewpoint.setText(dewpoint);
                    textViewPressure.setText(pressure);
                    textViewWinds.setText(winds);
                    textViewVisibility.setText(visibility);
                    textViewCeiling.setText(ceiling);
                    textViewClouds.setText(clouds);
                    textViewWeather.setText(weather);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMetar(final String ICAO) {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getMetar(ICAO);
            }
        });
        myThread.start();
    }

    private boolean refreshMetar() {
        if(textICAO.length() == 4) {
            Toast.makeText(this, "" + textICAO + " is refreshed", Toast.LENGTH_SHORT).show();
            editTextICAO.setText(textICAO);
        }
        else
            Toast.makeText(this, "Nothing to refresh.", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_activity_actions items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshMetar();
                return true;
            case R.id.rate_settings:
                rateTheApp(this);
                return true;
            case R.id.more_apps:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Anton+Klimakov"));
                startActivity(intent);
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean rateTheApp(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_about_title);

        final Context con = context;
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builderYes = new AlertDialog.Builder(con);
                builderYes.setTitle(R.string.dialog_rate_title);
                builderYes.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //saving();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.antonklimakov.metar"));
                        startActivity(intent);
                    }
                });
                builderYes.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builderYes.create();
                alert.show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anton.android.apps@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client:"));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }
}
