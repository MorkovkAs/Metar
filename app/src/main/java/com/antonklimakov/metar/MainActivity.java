package com.antonklimakov.metar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.antonklimakov.metar.utils.PrefsName;
import com.antonklimakov.metar.utils.SavedPreference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    SwipeRefreshLayout swipeRefreshLayout;

    TextView textViewMetar;
    TextView textViewConditions;
    TextView textViewTemperature;
    TextView textViewDewpoint;
    TextView textViewPressure;
    TextView textViewWinds;
    TextView textViewVisibility;
    TextView textViewCeiling;
    TextView textViewClouds;

    EditText editTextICAO;
    String textICAO = "";

    SavedPreference savedPreference;
    boolean isJustToCaps = false;

    protected static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        savedPreference = new SavedPreference();

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String firstStartOfVersionName = "firstStart-v" + BuildConfig.VERSION_NAME;
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean(firstStartOfVersionName, true);

                if (isFirstStart) {
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    });
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean(firstStartOfVersionName, false);
                    e.apply();
                }
            }
        });
        t.start();

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_halo, R.color.view_divider_color, R.color.icao);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshMetar();
            }
        });

        textViewMetar = findViewById(R.id.textViewMetar);
        textViewConditions = findViewById(R.id.textViewConditions);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewDewpoint = findViewById(R.id.textViewDewpoint);
        textViewPressure = findViewById(R.id.textViewPressure);
        textViewWinds = findViewById(R.id.textViewWinds);
        textViewVisibility = findViewById(R.id.textViewVisibility);
        textViewCeiling = findViewById(R.id.textViewCeiling);
        textViewClouds = findViewById(R.id.textViewClouds);

        editTextICAO = findViewById(R.id.editTextICAO);
        editTextICAO.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    if (!isJustToCaps) {
                        isJustToCaps = true;
                        if (!s.toString().equalsIgnoreCase(textICAO)) {
                            textICAO = s.toString().toUpperCase(Locale.US);
                        }
                        writeMetar(textICAO);
                        invalidateOptionsMenu();
                        editTextICAO.setText(textICAO);
                        editTextICAO.setSelection(4);
                    } else {
                        isJustToCaps = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                textICAO = data.getStringExtra("icao");
                refreshMetar();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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

        editTextICAO.setText(savedInstanceState.getString("editTextICAO"));
        textICAO = savedInstanceState.getString("ICAO");
        invalidateOptionsMenu();
    }

    private void setMetar(String ICAO) {
        final Map<String, String> weatherValues;
        try {
            weatherValues = getMetar(ICAO);
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.failed_to_update, Toast.LENGTH_SHORT).show();
                }
            });
            stopRefreshing();
            return;
        }

        String conditions = weatherValues.get("conditions");
        String airportDescription = conditions;
        if (!isNullOrEmpty(conditions)) {
            int leftBracket = conditions.indexOf("(");
            int rightBracket = conditions.indexOf(")");
            if (leftBracket != -1 && rightBracket != -1) {
                airportDescription = conditions.substring(leftBracket + 1, rightBracket);
            }
        }
        savedPreference.addSaved(getBaseContext(), PrefsName.HISTORY, new Airport(textICAO, airportDescription));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewMetar.setText(weatherValues.get("metar"));
                textViewConditions.setText(weatherValues.get("conditions"));
                textViewTemperature.setText(weatherValues.get("temperature"));
                textViewDewpoint.setText(weatherValues.get("dewpoint"));
                textViewPressure.setText(weatherValues.get("pressure"));
                textViewWinds.setText(weatherValues.get("winds"));
                textViewVisibility.setText(weatherValues.get("visibility"));
                textViewCeiling.setText(weatherValues.get("ceiling"));
                textViewClouds.setText(weatherValues.get("clouds"));
                stopRefreshing();
            }
        });
    }

    public Map<String, String> getMetar(String ICAO) throws IOException {
        Document doc;
        String link = "https://aviationweather.gov/metar/data?ids=" + ICAO + "&format=decoded&date=&hours=0";
        String link2 = "http://aviationweather.gov/adds/metars/?chk_metars=ON&hoursStr=most+recent+only&station_ids=" + ICAO + "&submitmet=Get+info&std_trans=translated";
        doc = Jsoup.connect(link).timeout(30000).get();

        final String metar = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(2) > td:nth-child(2)").text();
        final String conditions = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(1) > td:nth-child(2)").text();
        final String temperature = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(3) > td:nth-child(2)").text();
        final String dewpoint = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(4) > td:nth-child(2)").text();
        final String pressure = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(5) > td:nth-child(2)").text();
        final String winds = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(6) > td:nth-child(2)").text();
        final String visibility = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(7) > td:nth-child(2)").text();
        final String ceiling = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(8) > td:nth-child(2)").text();
        final String clouds = doc.select("#awc_main_content_wrap > table:nth-child(3) > tbody > tr:nth-child(9) > td:nth-child(2)").text();

        Map<String, String> values = new HashMap<>();
        values.put("metar", metar);
        values.put("conditions", conditions);
        values.put("temperature", temperature);
        values.put("dewpoint", dewpoint);
        values.put("pressure", pressure);
        values.put("winds", winds);
        values.put("visibility", visibility);
        values.put("ceiling", ceiling);
        values.put("clouds", clouds);

        return values;
    }

    private void writeMetar(final String ICAO) {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                setMetar(ICAO);
                stopRefreshing();
            }
        });
        myThread.start();
    }

    private void refreshMetar() {
        if (textICAO.length() == 4) {
            Toast.makeText(this, "" + textICAO + " " + getString(R.string.metar_is_refreshing), Toast.LENGTH_SHORT).show();
            editTextICAO.setText(textICAO);
        } else {
            Toast.makeText(this, R.string.nothing_to_refresh, Toast.LENGTH_SHORT).show();
            stopRefreshing();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isSaved = savedPreference.isItemSaved(getApplicationContext(), PrefsName.FAVORITE, textICAO);

        menu.getItem(0).setVisible(!isSaved);
        menu.getItem(1).setVisible(isSaved);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentNew;
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshMetar();
                return true;
            case R.id.favorites:
                intentNew = new Intent(this, TabbedActivity.class);

                Bundle bundle = new Bundle();
                bundle.putBoolean("fav", true);
                intentNew.putExtras(bundle);

                startActivityForResult(intentNew, 1);
                return true;
            case R.id.history:
                intentNew = new Intent(this, TabbedActivity.class);
                startActivityForResult(intentNew, 1);
                return true;
            case R.id.set_bookmark:
                final CharSequence conditionsText = textViewConditions.getText();
                if (textICAO.length() == 4 && conditionsText != null && !conditionsText.equals("")) {
                    final String conditions = conditionsText.toString();
                    if (!savedPreference.isItemSaved(getApplicationContext(), PrefsName.FAVORITE, textICAO)) {
                        String airportDescription = textICAO;
                        if (!isNullOrEmpty(conditions)) {
                            int leftBracket = conditions.indexOf("(");
                            int rightBracket = conditions.indexOf(")");
                            if (leftBracket != -1 && rightBracket != -1) {
                                airportDescription = conditions.substring(leftBracket + 1, rightBracket);
                            }
                        }
                        savedPreference.addSaved(getBaseContext(), PrefsName.FAVORITE,
                                new Airport(textICAO, airportDescription));
                        Toast.makeText(this, getString(R.string.add_bookmark), Toast.LENGTH_SHORT).show();
                    }
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(this, R.string.nothing_to_add_to_favorites, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.unset_bookmark:
                if (savedPreference.isItemSaved(getApplicationContext(), PrefsName.FAVORITE, textICAO)) {
                    savedPreference.removeSaved(getApplicationContext(), PrefsName.FAVORITE, textICAO);
                    Toast.makeText(this, getString(R.string.remove_bookmark), Toast.LENGTH_SHORT).show();
                }
                invalidateOptionsMenu();
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

    public void rateTheApp(Context context) {
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
                startActivity(Intent.createChooser(email, getString(R.string.choose_mail_client)));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void stopRefreshing() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
