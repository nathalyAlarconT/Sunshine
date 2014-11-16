package com.nathalyalarcont.sunshine;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nathaly on 11/11/2014.
 */
    public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){  //Este método ocurre antes del onCreateView
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); //Indica que el fragmento tiene un menu
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            Log.v("NAT","NATHALY");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Creating fake data
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70//40",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 65/75",
                "Sat - HELP TRAPPED IN WEAThERSTATION - 22/33",
                "Sun - Sunny - 80/68"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                // The current context (this fragment's parameter activity)
                getActivity(),
                // ID of list item layout
                R.layout.list_item_forecast,
                // ID of the textView to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                weekForecast);
        // Get a reference to the ListView
        // and attach this adapter to ListView
        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            //### FRAGMENT OF CODE FROM GIT URL ###

            if(params.length == 0){
                return null; //Si no hay paramtros no se hace nada
            }
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                                .appendQueryParameter(QUERY_PARAM, params[0])
                                                .appendQueryParameter(FORMAT_PARAM, format)
                                                .appendQueryParameter(UNITS_PARAM, units)
                                                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                                                .build();

                                        URL url = new URL(builtUri.toString());

                                        Log.v(LOG_TAG, "***** Built URL with URI " + builtUri.toString());
               //  URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                 //Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    //   forecastJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    // forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);  //Haciendo debug de lo obtenido
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
                //forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                        return null;
                    }
                }

                //###

            }
            return null;
        }

    }
}


