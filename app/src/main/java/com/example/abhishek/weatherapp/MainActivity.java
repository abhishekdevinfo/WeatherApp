package com.example.abhishek.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView showWeatherTextView;
    TextView cityNameTextView;

    public void showWeather(View view) {

        cityNameTextView = findViewById(R.id.cityNameTextView);
        String cityName = cityNameTextView.getText().toString();

        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");

            WeatherAPI task = new WeatherAPI();
            task.execute("http://api.wunderground.com/api/91e14f3b0fc356f6/conditions/q/" + encodedCityName + ".json");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showWeatherTextView.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Unable to find weather.", Toast.LENGTH_SHORT).show();
        }

        showWeatherTextView.setVisibility(View.VISIBLE);

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityNameTextView.getWindowToken(), 0);


    }

    public class WeatherAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                /*int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }*/
                BufferedReader bufferedReader = new BufferedReader(reader);
                String inputLine;
                StringBuilder builder = new StringBuilder();

                while ((inputLine = bufferedReader.readLine()) != null) {
                    builder.append(inputLine);
                }

                result = builder.toString();

                return result;


            } catch (Exception e) {
                e.getStackTrace();
                showWeatherTextView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Unable to find weather.", Toast.LENGTH_SHORT).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);
                String weather_info = jsonObject.getString("current_observation");

                JSONObject jsonObject_child = new JSONObject(weather_info);
                String weather = jsonObject_child.getString("weather");
                String temperature = jsonObject_child.getString("temperature_string");
                String humidity = jsonObject_child.getString("relative_humidity");

                showWeatherTextView.setText("Weather: " + weather + "\r\n" + "Temperature: " + temperature + "\r\n" + "Humidity: " + humidity);

            } catch (JSONException e) {
                e.getStackTrace();
                showWeatherTextView.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "Unable to find weather.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showWeatherTextView = findViewById(R.id.showWeatherTextView);

    }
}
