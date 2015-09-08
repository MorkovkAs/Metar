package com.antonklimakov.metar;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    TextView textViewICAO;
    EditText editTextICAO;
    String link1 = "http://www.jetplan.com/jeppesen/jsp/weather/aocTextWeather.jsp?icao=";
    String link2 = "&rt=METAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        textViewICAO = (TextView) findViewById(R.id.textViewICAO);
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
                    Log.d("YourTag", "4");
                    final String ICAO = s.toString();
                    //final Handler myHandler = new Handler(); // автоматически привязывается к текущему потоку.
                    Thread myThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Document doc = null;
                                try {
                                    String link = link1 + ICAO + link2;
                                    doc = Jsoup.connect(link).get();
                                    final Elements metar = doc.select("body > table:nth-child(4) > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(3) > pre");
                                    //Log.d("YourTag", metar.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textViewICAO.setText(metar.text());
                                        }
                                    });
                                    Log.d("YourTag", metar.text());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    });
                    myThread.start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
